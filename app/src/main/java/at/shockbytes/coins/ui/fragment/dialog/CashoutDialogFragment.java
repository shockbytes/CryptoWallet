package at.shockbytes.coins.ui.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import at.shockbytes.coins.R;
import at.shockbytes.coins.core.CoinsApp;
import at.shockbytes.coins.currency.CurrencyManager;
import at.shockbytes.coins.currency.OwnedCurrency;
import at.shockbytes.coins.util.ResourceManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Martin Macheiner
 *         Date: 21.09.2017.
 */

public class CashoutDialogFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener {

    public interface OnCashoutCompletedListener {

        void onCashoutCompleted();
    }

    private static final String ARG_CURRENCY_ID = "currency_id";
    private static final double SEEKBAR_FACTOR = 100d;

    public static CashoutDialogFragment newInstance(long currencyId) {
        CashoutDialogFragment fragment = new CashoutDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CURRENCY_ID, currencyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    protected CurrencyManager currencyManager;

    @BindView(R.id.dialog_fragment_cashout_txt_currency)
    protected TextView txtCurrency;

    @BindView(R.id.dialog_fragment_cashout_txt_amount)
    protected TextView txtAmount;

    @BindView(R.id.dialog_fragment_cashout_seekbar_amount)
    protected SeekBar seekBarAmount;

    private OwnedCurrency currency;

    private OnCashoutCompletedListener listener;

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CoinsApp) getActivity().getApplication()).getAppComponent().inject(this);

        long id = getArguments().getLong(ARG_CURRENCY_ID);
        currency = currencyManager.getOwnedCurrencyById(id);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getContext())
                .setTitle("Cashout money")
                .setIcon(R.drawable.ic_money_filled)
                .setView(getCashoutView())
                .setPositiveButton("Cashout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        double amountToPayout = seekBarAmount.getProgress() / SEEKBAR_FACTOR;
                        if (amountToPayout > 0) {
                            currencyManager.splitCashout(currency, amountToPayout);

                            if (listener != null) {
                                listener.onCashoutCompleted();
                            }
                        } else {
                            Toast.makeText(getContext(), "Amount must be bigger than 0", Toast.LENGTH_SHORT).show();
                        }

                        dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setNeutralButton("Cashout all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currencyManager.cashoutCurrency(currency);
                        if (listener != null) {
                            listener.onCashoutCompleted();
                        }
                        dismiss();
                    }
                })
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        setup();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        txtAmount.setText((progress / SEEKBAR_FACTOR) + " / "
                + ResourceManager.roundDoubleWithDigits(currency.getAmount(), 8));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void setOnCashoutCompletedListener(OnCashoutCompletedListener listener) {
        this.listener = listener;
    }

    private View getCashoutView() {
        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_fragment_cashout, null, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    private void setup() {

        seekBarAmount.setMax((int) (Math.ceil(currency.getAmount() * SEEKBAR_FACTOR)));
        seekBarAmount.setOnSeekBarChangeListener(this);
        txtCurrency.setText("Cashout your " + currency.getCryptoCurrency().name());
        txtAmount.setText("0 / " + ResourceManager.roundDoubleWithDigits(currency.getAmount(), 8));
    }


}
