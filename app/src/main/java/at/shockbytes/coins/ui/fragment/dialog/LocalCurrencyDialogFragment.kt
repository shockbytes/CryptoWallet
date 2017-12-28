package at.shockbytes.coins.ui.fragment.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import at.shockbytes.coins.R
import at.shockbytes.coins.core.CryptoWatcherApp
import at.shockbytes.coins.currency.CurrencyManager
import at.shockbytes.coins.currency.RealCurrency
import javax.inject.Inject


/**
 * @author Martin Macheiner
 * Date: 23.12.2017.
 */

class LocalCurrencyDialogFragment : DialogFragment() {

    @Inject
    protected lateinit var currencyManager: CurrencyManager

    private var checkedIdx = 0

    private var completionListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as CryptoWatcherApp).appComponent.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context)
                .setTitle(R.string.dialog_set_local_currency_title)
                .setIcon(R.drawable.ic_real_currency_usd)
                .setSingleChoiceItems(RealCurrency.values().map { it.name }.toTypedArray(),
                        currencyManager.localCurrency.ordinal,
                        { di: DialogInterface, _ ->
                            checkedIdx = (di as AlertDialog).listView.checkedItemPosition
                        })
                .setPositiveButton(R.string.ok) { _, _ ->
                    currencyManager.localCurrency = RealCurrency.values()[checkedIdx]
                    completionListener?.invoke()
                    dismiss()
                }
                .setCancelable(false)
                .create()
    }

    fun setOnCompletionListener(listener: () -> Unit): LocalCurrencyDialogFragment {
        completionListener = listener
        return this
    }

    companion object {

        fun newInstance(): LocalCurrencyDialogFragment {
            val fragment = LocalCurrencyDialogFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}