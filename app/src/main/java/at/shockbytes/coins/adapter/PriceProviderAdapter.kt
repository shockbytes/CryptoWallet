package at.shockbytes.coins.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import at.shockbytes.coins.R
import at.shockbytes.coins.currency.price.PriceSource
import kotterknife.bindView

/**
 * @author Martin Macheiner
 * Date: 27.12.2017.
 */

class PriceProviderAdapter(c: Context, data: List<PriceProviderItem>)
    : BaseAdapter<PriceProviderAdapter.PriceProviderItem>(c, data) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_price_provider, parent, false))
    }

    inner class ViewHolder(itemView: View) : BaseAdapter<PriceProviderItem>.ViewHolder(itemView) {

        private val imgView: ImageView by bindView(R.id.item_price_provider_imgview)
        private val txtView: TextView by bindView(R.id.item_price_provider_txt)
        private val enableSwitch: Switch by bindView(R.id.item_price_provider_switch)

        override fun bind(t: PriceProviderItem) {
            content = t

            txtView.text = t.priceSource.name
            imgView.setImageResource(t.priceSource.icon)
            enableSwitch.isChecked = t.isEnabled

            enableSwitch.setOnCheckedChangeListener { _, enabled -> content?.isEnabled = enabled }
        }

    }

    data class PriceProviderItem(val priceSource: PriceSource, var isEnabled: Boolean)

}