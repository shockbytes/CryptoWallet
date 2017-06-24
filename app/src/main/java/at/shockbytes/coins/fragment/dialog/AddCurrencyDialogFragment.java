package at.shockbytes.coins.fragment.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import at.shockbytes.coins.R;
import at.shockbytes.coins.adapter.CurrencySpinnerAdapter;
import at.shockbytes.coins.currency.CryptoCurrency;
import at.shockbytes.coins.currency.Currency;
import at.shockbytes.coins.currency.OwnedCurrency;
import at.shockbytes.coins.util.ResourceManager;
import butterknife.Bind;
import butterknife.ButterKnife;

public class AddCurrencyDialogFragment extends DialogFragment {

    public interface OnCurrencyAddedListener {

        void onCurrencyAdded(OwnedCurrency ownedCurrency);
    }

    @Bind(R.id.fragment_dialog_add_currency_edit_cryptocurrency)
    protected TextInputEditText editCryptoCurrency;

    @Bind(R.id.fragment_dialog_add_currency_edit_currency)
    protected TextInputEditText editCurrency;

    @Bind(R.id.fragment_dialog_add_currency_til_cryptocurrency)
    protected TextInputLayout tilCryptoCurrency;

    @Bind(R.id.fragment_dialog_add_currency_til_currency)
    protected TextInputLayout tilCurrency;

    @Bind(R.id.fragment_dialog_add_currency_spinner_cryptocurrency)
    protected Spinner spinnerCryptoCurrency;

    @Bind(R.id.fragment_dialog_add_currency_spinner_currency)
    protected Spinner spinnerCurrency;


    private OnCurrencyAddedListener listener;

    public static AddCurrencyDialogFragment newInstance() {
        AddCurrencyDialogFragment fragment = new AddCurrencyDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AddCurrencyDialogFragment() {
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getContext())
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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

                        if (listener != null) {
                            listener.onCurrencyAdded(new OwnedCurrency(currency, amount,
                                    boughtCurrency, boughtPrice));
                        }

                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setView(createView())
                .setIcon(R.drawable.ic_money_filled)
                .setTitle("Add bought coins")
                .create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setOnCurrencyAddedListener(OnCurrencyAddedListener listener) {
        this.listener = listener;
    }

    private View createView() {

        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_dialog_add_currency, null, false);
        ButterKnife.bind(this, v);

        spinnerCryptoCurrency.setAdapter(new CurrencySpinnerAdapter(getContext(),
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

        spinnerCurrency.setAdapter(new CurrencySpinnerAdapter(getContext(), getCurrencyItems()));
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

        return v;
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
