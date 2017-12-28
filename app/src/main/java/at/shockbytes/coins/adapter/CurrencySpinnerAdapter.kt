package at.shockbytes.coins.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import at.shockbytes.coins.R

/**
 * @author Martin Macheiner
 * Date: 24.06.2017.
 */

class CurrencySpinnerAdapter(context: Context, data: List<CurrencySpinnerAdapterItem>)
    : ArrayAdapter<CurrencySpinnerAdapter.CurrencySpinnerAdapterItem>(context, 0, data) {

    class CurrencySpinnerAdapterItem(var name: String, internal var iconId: Int)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var v = convertView
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.item_spinner, parent, false)
        }

        val text = v?.findViewById<TextView>(R.id.item_spinner_text)
        text?.text = getItem(position)?.name
        val imgView = v?.findViewById<ImageView>(R.id.item_spinner_icon)
        imgView?.setImageResource(getItem(position)?.iconId!!)
        return v!!
    }
}
