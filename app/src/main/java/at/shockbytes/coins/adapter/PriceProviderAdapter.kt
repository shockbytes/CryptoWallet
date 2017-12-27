package at.shockbytes.coins.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import at.shockbytes.coins.R
import at.shockbytes.coins.currency.price.PriceSource
import butterknife.BindView

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

        @BindView(R.id.item_price_provider_imgview)
        protected lateinit var imgView: ImageView

        @BindView(R.id.item_price_provider_txt)
        protected lateinit var txtView: TextView

        @BindView(R.id.item_price_provider_switch)
        protected lateinit var enableSwitch: Switch

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