package at.shockbytes.coins.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.coins.R;
import at.shockbytes.coins.adapter.CurrencySpinnerAdapter;
import at.shockbytes.coins.currency.CryptoCurrency;
import at.shockbytes.coins.currency.Currency;
import at.shockbytes.coins.currency.CurrencyManager;
import at.shockbytes.coins.currency.OwnedCurrency;
import at.shockbytes.coins.util.ResourceManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddCurrencyActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, AddCurrencyActivity.class);
    }

    @Inject
    protected CurrencyManager currencyManager;

    @BindView(R.id.fragment_dialog_add_currency_edit_cryptocurrency)
    protected TextInputEditText editCryptoCurrency;

    @BindView(R.id.fragment_dialog_add_currency_edit_currency)
    protected TextInputEditText editCurrency;

    @BindView(R.id.fragment_dialog_add_currency_til_cryptocurrency)
    protected TextInputLayout tilCryptoCurrency;

    @BindView(R.id.fragment_dialog_add_currency_til_currency)
    protected TextInputLayout tilCurrency;

    @BindView(R.id.fragment_dialog_add_currency_spinner_cryptocurrency)
    protected Spinner spinnerCryptoCurrency;

    @BindView(R.id.fragment_dialog_add_currency_spinner_currency)
    protected Spinner spinnerCurrency;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setExitTransition(new Slide(Gravity.BOTTOM));
        getWindow().setEnterTransition(new Explode());
        setContentView(R.layout.activity_add_currency);
        ((CoinsApp) getApplication()).getAppComponent().inject(this);
        unbinder = ButterKnife.bind(this);
        setupViews();
        setupActionBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fragment_dialog_add_currency_btn_save)
    protected void onClickSave() {

        String strbp = editCurrency.getText().toString();
        if (strbp.isEmpty()) {
            return;
        }

        String strcc = editCryptoCurrency.getText().toString();
        if (strcc.isEmpty()) {
            return;
        }

        double boughtPrice = Double.parseDouble(strbp);
        Currency boughtCurrency = Currency.values()[spinnerCurrency
                .getSelectedItemPosition()];
        double amount = Double.parseDouble(strcc);
        CryptoCurrency currency = CryptoCurrency.values()[spinnerCryptoCurrency
                .getSelectedItemPosition()];

        OwnedCurrency ownedCurrency = new OwnedCurrency(currency, amount,
                boughtCurrency, boughtPrice);

        currencyManager.addOwnedCurrency(ownedCurrency);
        supportFinishAfterTransition();
    }

    private void setupViews() {

        spinnerCryptoCurrency.setAdapter(new CurrencySpinnerAdapter(this,
                getCryptoCurrencyItems()));
        spinnerCryptoCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CurrencySpinnerAdapter.CurrencySpinnerAdapterItem item =
                        (CurrencySpinnerAdapter.CurrencySpinnerAdapterItem) spinnerCryptoCurrency.getSelectedItem();
                tilCryptoCurrency.setHint(item.text);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerCurrency.setAdapter(new CurrencySpinnerAdapter(this, getCurrencyItems()));
        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CurrencySpinnerAdapter.CurrencySpinnerAdapterItem item =
                        (CurrencySpinnerAdapter.CurrencySpinnerAdapterItem) spinnerCurrency.getSelectedItem();
                tilCurrency.setHint(item.text);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setupActionBar() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }

    private List<CurrencySpinnerAdapter.CurrencySpinnerAdapterItem> getCurrencyItems() {
        List<CurrencySpinnerAdapter.CurrencySpinnerAdapterItem> items = new ArrayList<>();
        for (Currency c : Currency.values()) {
            items.add(new CurrencySpinnerAdapter.CurrencySpinnerAdapterItem(c.name(),
                    ResourceManager.getResourceForCurrency(c)));
        }
        return items;
    }

    private List<CurrencySpinnerAdapter.CurrencySpinnerAdapterItem> getCryptoCurrencyItems() {
        List<CurrencySpinnerAdapter.CurrencySpinnerAdapterItem> items = new ArrayList<>();
        for (CryptoCurrency c : CryptoCurrency.values()) {
            items.add(new CurrencySpinnerAdapter.CurrencySpinnerAdapterItem(c.name(),
                    ResourceManager.getResourceForCryptoCurrency(c)));
        }
        return items;
    }

}
