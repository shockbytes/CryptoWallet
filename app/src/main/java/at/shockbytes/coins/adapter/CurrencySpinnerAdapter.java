package at.shockbytes.coins.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import at.shockbytes.coins.R;

/**
 * @author Martin Macheiner
 *         Date: 24.06.2017.
 */

public class CurrencySpinnerAdapter extends ArrayAdapter<CurrencySpinnerAdapter.CurrencySpinnerAdapterItem> {

    public static class CurrencySpinnerAdapterItem {

        public String text;
        int iconId;

        public CurrencySpinnerAdapterItem(String text, int iconId) {
            this.text = text;
            this.iconId = iconId;
        }

    }

    public CurrencySpinnerAdapter(@NonNull Context context,
                                  List<CurrencySpinnerAdapterItem> data) {
        super(context, 0, data);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner, parent, false);
        }

        TextView text = v.findViewById(R.id.item_spinner_text);
        text.setText(getItem(position).text);
        ImageView imgView = v.findViewById(R.id.item_spinner_icon);
        imgView.setImageResource(getItem(position).iconId);
        return v;

    }
}
