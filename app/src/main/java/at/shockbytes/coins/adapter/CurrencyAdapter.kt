package at.shockbytes.coins.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.view.menu.MenuPopupHelper
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import at.shockbytes.coins.R
import at.shockbytes.coins.currency.Currency
import at.shockbytes.coins.currency.RealCurrency
import at.shockbytes.coins.currency.conversion.CurrencyConversionRates
import at.shockbytes.coins.ui.fragment.MainFragment
import at.shockbytes.coins.util.ResourceManager
import kotterknife.bindView

/**
 * @author Martin Macheiner
 * Date: 15.06.2017.
 */

class CurrencyAdapter(cxt: Context, data: List<Currency>,
                      private val viewType: MainFragment.ViewType,
                      private val popupListener: OnEntryPopupItemSelectedListener?) : AdBaseAdapter<Currency>(cxt, data) {

    interface OnEntryPopupItemSelectedListener {

        fun onCashout(c: Currency)

        fun onDelete(c: Currency)
    }

    private var localCurrency: RealCurrency? = null
    private var conversionRates: CurrencyConversionRates? = null

    override val testDeviceId = "75B521A6A3B633AF4A75D6A82EF04165" // <-- Nexus 5

    override fun getViewHolder(parent: ViewGroup): BaseAdapter<Currency>.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_currency, parent, false))
    }

    override fun addAdvertisementEntity() {
        if (adPosition < itemCount) {
            addEntity(adPosition, Currency())
        }
    }

    fun setLocalCurrency(localCurrency: RealCurrency, conversionRates: CurrencyConversionRates?) {
        this.localCurrency = localCurrency
        this.conversionRates = conversionRates
    }

    internal inner class ViewHolder(itemView: View) : BaseAdapter<Currency>.ViewHolder(itemView),
            PopupMenu.OnMenuItemClickListener {

        private val imgViewIcon: ImageView by bindView(R.id.item_currency_icon)
        private val txtAmount: TextView by bindView(R.id.item_currency_txt_amount)
        private val txtBoughtPrice: TextView by bindView(R.id.item_currency_txt_bought_price)
        private val txtCurrentPrice: TextView by bindView(R.id.item_currency_txt_current_price)
        private val txtDiff: TextView by bindView(R.id.item_currency_txt_diff)
        private val imgBtnOverflow: ImageButton by bindView(R.id.item_currency_icon_imgbtn_overflow)

        private lateinit var popupMenu: PopupMenu

        init {
            setupPopupMenu()
        }

        override fun bind(t: Currency) {
            content = t

            imgViewIcon.setImageResource(content?.getCryptoCurrency()?.icon!!)
            txtAmount.text = (ResourceManager.roundDouble(content?.cryptoAmount!!, 8).toString()
                    + " " + content?.getCryptoCurrency()?.name)

            val boughtPrice = if (content?.getRealCurrency() !== localCurrency) {
                // Fall back to realAmount, if conversion not possible
                conversionRates?.convert(content?.realAmount!!, content?.getRealCurrency()!!,
                        localCurrency!!) ?: content?.realAmount
            } else {
                content?.realAmount
            }

            txtBoughtPrice.text = (ResourceManager.roundDouble(boughtPrice!!, 2).toString()
                    + " " + localCurrency?.symbol)

            txtCurrentPrice.text = (content?.currentPrice.toString() + " " + localCurrency?.symbol)

            val diff = content?.getPricePercentageDiff(boughtPrice)!!
            val diffColor = if (diff >= 0) R.color.percentage_win else R.color.percentage_loose
            txtDiff.setTextColor(ContextCompat.getColor(context, diffColor))
            txtDiff.text = diff.toString().plus("%")
        }

        private fun setupPopupMenu() {
            popupMenu = PopupMenu(context, imgBtnOverflow)
            popupMenu.menuInflater.inflate(R.menu.popup_item, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(this)

            // Hide the cashout action if already cashed out
            if (viewType === MainFragment.ViewType.CASHOUT) {
                popupMenu.menu.getItem(0).isVisible = false
            }

            tryShowIconsInPopupMenu(popupMenu)
            imgBtnOverflow.setOnClickListener { popupMenu.show() }
        }

        @SuppressLint("RestrictedApi")
        private fun tryShowIconsInPopupMenu(menu: PopupMenu) {

            try {
                val fieldPopup = menu.javaClass.getDeclaredField("mPopup")
                fieldPopup.isAccessible = true
                val popup = fieldPopup.get(menu) as MenuPopupHelper
                popup.setForceShowIcon(true)
            } catch (e: Exception) {
                Log.d("Coins", "Cannot force to show icons in popupmenu")
            }
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {

            when (item.itemId) {

                R.id.popup_item_cashout -> popupListener?.onCashout(content!!)

                R.id.popup_item_remove -> popupListener?.onDelete(content!!)
            }
            return true
        }
    }
}
