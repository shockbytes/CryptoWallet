package at.shockbytes.coins.ui.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import at.shockbytes.coins.R;
import at.shockbytes.coins.adapter.OwnedCurrencyAdapter;
import at.shockbytes.coins.currency.Balance;
import at.shockbytes.coins.currency.CurrencyManager;
import at.shockbytes.coins.currency.OwnedCurrency;
import at.shockbytes.coins.dagger.AppComponent;
import at.shockbytes.coins.ui.fragment.dialog.CashoutDialogFragment;
import at.shockbytes.coins.util.AppParams;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        OwnedCurrencyAdapter.OnEntryPopupItemSelectedListener {

    public enum ViewType {
        BALANCE, CASHOUT
    }

    public static MainFragment newInstance(ViewType viewType) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VIEWTYPE, viewType);
        fragment.setArguments(args);
        return fragment;
    }

    private static final String ARG_VIEWTYPE = "arg_viewtype";

    @Inject
    protected CurrencyManager currencyManager;

    @Inject
    protected SharedPreferences preferences;

    @BindView(R.id.main_fragment_rv)
    protected RecyclerView recyclerView;

    @BindView(R.id.fragment_main_swipe_container)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.balance_header_txt_current)
    protected TextView txtCurrent;

    @BindView(R.id.balance_header_txt_invested)
    protected TextView txtInvested;

    @BindView(R.id.balance_header_txt_percentage)
    protected TextView txtDiffPercentage;

    @BindView(R.id.balance_header_imgview_trend)
    protected ImageView imgViewTrend;

    @BindView(R.id.fragment_main_empty_view)
    protected View emptyView;

    @BindView(R.id.fragment_main_empty_view_text)
    protected TextView emptyTextView;

    private OwnedCurrencyAdapter adapter;

    private boolean isViewSetup;

    private ViewType viewType;
    
    private double lastBalance;

    private Disposable timerDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewType = (ViewType) getArguments().getSerializable(ARG_VIEWTYPE);
        isViewSetup = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    protected void injectToGraph(@NotNull AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isViewSetup) {
            setupViews();
        }

        lastBalance = currencyManager.getLatestBalance();
        loadData();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (timerDisposable != null && !timerDisposable.isDisposed()) {
            timerDisposable.dispose();
        }

        currencyManager.storeLatestBalance();
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void setupViews() {

        // SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.colorPrimary),
                ContextCompat.getColor(getContext(), R.color.colorAccent),
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setEnabled(viewType != ViewType.CASHOUT);

        emptyTextView.setText(viewType == ViewType.CASHOUT
                ? R.string.empty_indicator_cashout : R.string.empty_indicator_balance);

        // RecyclerView
        adapter = new OwnedCurrencyAdapter(getContext(), new ArrayList<OwnedCurrency>(),
                viewType, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        isViewSetup = true;
    }

    @Override
    public void onCashout(OwnedCurrency ownedCurrency) {

        CashoutDialogFragment fragment = CashoutDialogFragment.newInstance(ownedCurrency.getId());
        fragment.setOnCashoutCompletedListener(new CashoutDialogFragment.OnCashoutCompletedListener() {
            @Override
            public void onCashoutCompleted() {
                loadData();
            }
        });
        fragment.show(getFragmentManager(), "cashout-fragment");

    }

    @Override
    public void onDelete(OwnedCurrency ownedCurrency) {
        currencyManager.removeCurrency(ownedCurrency);
        loadData();
    }

    private void setupHeader(Balance balance) {

        txtCurrent.setText(balance.getCurrent() + " " + currencyManager.getLocalCurrency());
        txtInvested.setText(balance.getInvested() + " " + currencyManager.getLocalCurrency());

        double diff = balance.getPercentageDiff();
        int diffColor = diff >= 0 ? R.color.percentage_win : R.color.percentage_loose;
        txtDiffPercentage.setTextColor(ContextCompat.getColor(getContext(), diffColor));
        txtDiffPercentage.setText(diff + "%");

        animateTrendArrow(balance.getCurrent());
    }

    private void animateTrendArrow(double balance) {

        // Do not animate anything if it has the same value
        if (lastBalance == balance) {
            return;
        }

        float rotation = (lastBalance > balance) ? 90 : -90;
        imgViewTrend.setRotation(rotation);

        imgViewTrend.animate().alpha(1).setDuration(AppParams.INSTANCE.getTREND_ANIM_DURATION()).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (imgViewTrend != null) {
                    imgViewTrend.animate().alpha(0).setDuration(AppParams.INSTANCE.getTREND_ANIM_DURATION());
                }
            }
        });

        lastBalance = balance;
    }

    private void loadData() {

        swipeRefreshLayout.setRefreshing(true);

        if (viewType == ViewType.BALANCE) {

            if (preferences.getBoolean(getString(R.string.prefs_key_auto_update), false)) {
                subscribeToPeriodicDataSource();
            } else {
                subscribeToSingleDataSource(currencyManager.getOwnedCurrencies());
            }

        } else if (viewType == ViewType.CASHOUT) {
            subscribeToSingleDataSource(currencyManager.getCashedoutCurrencies());
        }

    }

    private void subscribeToSingleDataSource(Observable<List<OwnedCurrency>> dataSource) {

        dataSource.subscribe(new Consumer<List<OwnedCurrency>>() {
            @Override
            public void accept(List<OwnedCurrency> ownedCurrencies) {

                swipeRefreshLayout.setRefreshing(false);

                adapter.setLocalCurrency(currencyManager.getLocalCurrency(),
                        currencyManager.getCurrencyConversionRates());
                adapter.setData(ownedCurrencies);

                int visibility = ownedCurrencies.size() == 0 ? View.VISIBLE : View.GONE;
                emptyView.setVisibility(visibility);

                // Call in here makes sure, that the balance object is loaded at this point in time
                setupHeader(currencyManager.getBalance());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void subscribeToPeriodicDataSource() {

        timerDisposable = Observable.timer(AppParams.INSTANCE.getAUTO_UPDATE_TIME(),
                TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).doOnNext(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                subscribeToSingleDataSource(currencyManager.getOwnedCurrencies());
            }
        }).subscribe();
    }

    public void onNewCurrencyEntryAvailable() {
        if (isVisible()) {
            loadData();
        }
    }


}
