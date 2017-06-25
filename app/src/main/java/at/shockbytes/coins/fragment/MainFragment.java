package at.shockbytes.coins.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.coins.R;
import at.shockbytes.coins.adapter.OwnedCurrencyAdapter;
import at.shockbytes.coins.core.CoinsApp;
import at.shockbytes.coins.currency.Balance;
import at.shockbytes.coins.currency.CurrencyManager;
import at.shockbytes.coins.currency.OwnedCurrency;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        OwnedCurrencyAdapter.OnEntryPopupItemSelectedListener {

    public enum ViewType {
        BALANCE, CASHOUT
    }

    private static final String ARG_VIEWTYPE = "arg_viewtype";

    @Inject
    protected CurrencyManager currencyManager;

    @Bind(R.id.main_fragment_rv)
    protected RecyclerView recyclerView;

    @Bind(R.id.fragment_main_swipe_container)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.balance_header_txt_current)
    protected TextView txtCurrent;

    @Bind(R.id.balance_header_txt_invested)
    protected TextView txtInvested;

    @Bind(R.id.balance_header_txt_percentage)
    protected TextView txtDiffPercentage;

    @Bind(R.id.fragment_main_empty_view)
    protected View emptyView;

    @Bind(R.id.fragment_main_empty_view_text)
    protected TextView emptyTextView;

    private OwnedCurrencyAdapter adapter;

    private boolean isViewSetup;

    private ViewType viewType;

    public static MainFragment newInstance(ViewType viewType) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VIEWTYPE, viewType);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CoinsApp) getActivity().getApplication()).getAppComponent().inject(this);
        viewType = (ViewType) getArguments().getSerializable(ARG_VIEWTYPE);
        isViewSetup = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isViewSetup) {
            setupViews();
        }

        loadData();
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    private void setupViews() {

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

    private void setupHeader(Balance balance) {

        txtCurrent.setText(balance.getCurrent() + " " + currencyManager.getLocalCurrency());
        txtInvested.setText(balance.getInvested() + " " + currencyManager.getLocalCurrency());

        double diff = balance.getPercentageDiff();
        int diffColor = diff >= 0 ? R.color.colorAccent : android.R.color.holo_red_light;
        txtDiffPercentage.setTextColor(ContextCompat.getColor(getContext(), diffColor));
        txtDiffPercentage.setText(diff + "%");
    }

    private void loadData() {

        swipeRefreshLayout.setRefreshing(true);

        Observable<List<OwnedCurrency>> dataSource = Observable.empty();
        if (viewType == ViewType.BALANCE) {
            dataSource = currencyManager.getOwnedCurrencies();
        } else if (viewType == ViewType.CASHOUT) {
            dataSource = currencyManager.getCashedoutCurrencies();
        }

        dataSource.subscribe(new Action1<List<OwnedCurrency>>() {
            @Override
            public void call(List<OwnedCurrency> ownedCurrencies) {

                swipeRefreshLayout.setRefreshing(false);

                adapter.setLocalCurrency(currencyManager.getLocalCurrency());
                adapter.setData(ownedCurrencies);

                int visibility = ownedCurrencies.size() == 0 ? View.VISIBLE : View.GONE;
                emptyView.setVisibility(visibility);

                // Call in here makes sure, that the balance object is loaded at this point in time
                setupHeader(currencyManager.getBalance());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public void onNewCurrencyEntryAvailable(OwnedCurrency ownedCurrency) {
        currencyManager.addOwnedCurrency(ownedCurrency);
        if (isVisible()) {
            loadData();
        }
    }

    @Override
    public void onCashout(OwnedCurrency ownedCurrency) {
        currencyManager.cashoutCurrency(ownedCurrency);
        loadData();
    }

    @Override
    public void onDelete(OwnedCurrency ownedCurrency) {
        currencyManager.removeCurrency(ownedCurrency);
        loadData();
    }
}
