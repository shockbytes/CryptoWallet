package at.shockbytes.coins.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.shockbytes.coins.R
import at.shockbytes.coins.util.CoinUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.util.*


/**
 * @author Martin Macheiner
 * Date: 26.12.2017.
 */
abstract class AdBaseAdapter<T>(c: Context, d: List<T>) : BaseAdapter<T>(c, d) {

    var adPosition = 2

    override var data: MutableList<T> = ArrayList()
        set(value) {

            field = ArrayList()
            //Add and move items
            for (i in value.indices) {
                val entity = value[i]
                val location = getLocation(field, entity)
                if (location < 0) {
                    addEntity(i, entity)
                } else if (location != i) {
                    moveEntity(i, location)
                }
            }

            addAdEntity()
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return if (position == adPosition) adViewType else itemViewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        return when (viewType) {

            adViewType -> AdViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_ad, parent, false))

            else -> getViewHolder(parent) // If nothing found, use default item

        }
    }

    abstract fun addAdEntity()

    abstract fun getViewHolder(parent: ViewGroup): ViewHolder

    inner class AdViewHolder(itemView: View) : ViewHolder(itemView) {

        private val adView: AdView = itemView.findViewById(R.id.item_ad_adview)

        override fun bind(t: T) {
            val request = AdRequest.Builder()
                    .addTestDevice(CoinUtils.testDeviceMotoG5s)
                    .build()
            adView.loadAd(request)
        }

    }

    companion object {

        // Usual item
        protected val itemViewType = 0

        // Ad
        protected val adViewType = 1

    }

}