package at.shockbytes.coins.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import at.shockbytes.coins.R
import at.shockbytes.coins.adapter.PriceProviderAdapter
import at.shockbytes.coins.core.CryptoWatcherApp
import at.shockbytes.coins.currency.price.PriceProxy
import at.shockbytes.coins.util.ResourceManager
import javax.inject.Inject

/**
 * @author Martin Macheiner
 * Date: 26.12.2017.
 */
class PriceProviderDialogFragment : DialogFragment() {

    @Inject
    protected lateinit var priceProxy: PriceProxy

    private var completionListener: (() -> Unit)? = null

    private var providerAdapter: PriceProviderAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as CryptoWatcherApp).appComponent.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context)
                .setTitle(R.string.dialog_provider_selection_title)
                .setIcon(R.drawable.ic_price_provider)
                .setView(buildView())
                .setPositiveButton(R.string.ok) { _, _ ->
                    applySelection()
                    completionListener?.invoke()
                    dismiss()
                }
                .create()
    }

    fun setOnCompletionListener(listener: () -> Unit): PriceProviderDialogFragment {
        this.completionListener = listener
        return this
    }

    private fun buildView(): View {

        val recyclerView = RecyclerView(context)
        val px = ResourceManager.convertDpInPixel(8, context)
        recyclerView.setPadding(px, px, px, px)
        val sources = priceProxy.priceProvider
                .map { PriceProviderAdapter.PriceProviderItem(it.providerInfo, it.isEnabled) }
        providerAdapter = PriceProviderAdapter(context, sources)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                false)
        recyclerView.adapter = providerAdapter
        return recyclerView
    }

    private fun applySelection() {

        providerAdapter?.data?.forEach { item ->
            priceProxy.priceProvider
                    .filter { item.priceSource == it.providerInfo }
                    .forEach { it.isEnabled = item.isEnabled }
        }
    }

    companion object {

        fun newInstance(): PriceProviderDialogFragment {
            val fragment = PriceProviderDialogFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}