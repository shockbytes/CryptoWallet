package at.shockbytes.coins.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import at.shockbytes.coins.R
import at.shockbytes.coins.core.CryptoWatcherApp
import at.shockbytes.coins.currency.Currency
import at.shockbytes.coins.currency.CurrencyManager
import at.shockbytes.coins.util.ResourceManager
import kotterknife.bindView
import javax.inject.Inject

/**
 * @author Martin Macheiner
 * Date: 21.09.2017.
 */

class CashoutDialogFragment : DialogFragment(), SeekBar.OnSeekBarChangeListener {

    @Inject
    protected lateinit var currencyManager: CurrencyManager

    private val txtAmount: TextView by bindView(R.id.dialog_fragment_cashout_txt_amount)
    private val seekBarAmount: SeekBar by bindView(R.id.dialog_fragment_cashout_seekbar_amount)

    private lateinit var currency: Currency

    private var completeListener: (() -> Unit)? = null

    private val cashoutView: View
        get() = LayoutInflater.from(context)
                .inflate(R.layout.dialog_fragment_cashout, null, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as CryptoWatcherApp).appComponent.inject(this)

        val id = arguments.getLong(ARG_CURRENCY_ID)
        currency = currencyManager.getCurrencyById(id)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context)
                .setTitle(getString(R.string.dialog_cashout_title, currency.getCryptoCurrency().name))
                .setIcon(R.drawable.ic_money_filled)
                .setView(cashoutView)
                .setPositiveButton(R.string.title_cashout) { _, _ ->

                    val amountToPayout = seekBarAmount.progress / SEEKBAR_FACTOR
                    if (amountToPayout > 0) {

                        // All is selected, make a full cashout
                        if (amountToPayout == currency.cryptoAmount) {
                            currencyManager.cashoutCurrency(currency)
                        } else {
                            currencyManager.partialCashout(currency, amountToPayout)
                        }
                        completeListener?.invoke()
                    } else {
                        Toast.makeText(context, R.string.hint_dialog_cashout_bigger_zero, Toast.LENGTH_SHORT).show()
                    }
                    dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> dismiss() }
                .setNeutralButton(R.string.cashout_all) { _, _ ->
                    currencyManager.cashoutCurrency(currency)
                    completeListener?.invoke()
                    dismiss()
                }
                .create()
    }

    override fun onStart() {
        super.onStart()
        setup()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
        txtAmount.text = ("${(progress / SEEKBAR_FACTOR)} / ${ResourceManager.roundDouble(currency.cryptoAmount, 8)}")
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    fun setOnCashoutCompletedListener(listener: () -> Unit) {
        this.completeListener = listener
    }

    private fun setup() {

        seekBarAmount.max = Math.ceil(currency.cryptoAmount * SEEKBAR_FACTOR).toInt()
        seekBarAmount.setOnSeekBarChangeListener(this)

        txtAmount.text = ("0.0 / ${ResourceManager.roundDouble(currency.cryptoAmount, 8)}")
    }

    companion object {

        private val ARG_CURRENCY_ID = "currency_id"
        private val SEEKBAR_FACTOR = 100.0

        fun newInstance(currencyId: Long): CashoutDialogFragment {
            val fragment = CashoutDialogFragment()
            val args = Bundle()
            args.putLong(ARG_CURRENCY_ID, currencyId)
            fragment.arguments = args
            return fragment
        }
    }


}
