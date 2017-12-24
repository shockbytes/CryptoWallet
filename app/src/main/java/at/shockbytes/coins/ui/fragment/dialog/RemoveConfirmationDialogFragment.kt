package at.shockbytes.coins.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import at.shockbytes.coins.R

/**
 * @author Martin Macheiner
 * Date: 23.12.2017.
 */

class RemoveConfirmationDialogFragment : DialogFragment() {

    private var confirmationListener: (() -> Unit)? = null

    private lateinit var amount: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        amount = arguments.getString(ARG_AMOUNT)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context)
                .setTitle(R.string.dialog_remove_confirmation_title)
                .setIcon(R.drawable.ic_cancel)
                .setMessage(getString(R.string.dialog_remove_confirmation_msg, amount))
                .setPositiveButton(R.string.remove) { _, _ ->
                    confirmationListener?.invoke()
                    dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> dismiss() }
                .create()
    }

    fun setConfirmationListener(listener: () -> Unit): RemoveConfirmationDialogFragment {
        confirmationListener = listener
        return this
    }

    companion object {

        private val ARG_AMOUNT = "arg_amount"

        fun newInstance(amount: String): RemoveConfirmationDialogFragment {
            val fragment = RemoveConfirmationDialogFragment()
            val args = Bundle()
            args.putString(ARG_AMOUNT, amount)
            fragment.arguments = args
            return fragment
        }
    }

}