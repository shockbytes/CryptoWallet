package at.shockbytes.coins.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

import at.shockbytes.coins.R;
import at.shockbytes.coins.currency.Currency;
import at.shockbytes.coins.currency.OwnedCurrency;
import at.shockbytes.coins.util.ResourceManager;
import butterknife.Bind;
import butterknife.OnClick;

import static at.shockbytes.coins.util.ResourceManager.getResourceForCryptoCurrency;

/**
 * @author Martin Macheiner
 *         Date: 15.06.2017.
 */

public class OwnedCurrencyAdapter extends BaseAdapter<OwnedCurrency> {

    public interface OnEntryPopupItemSelectedListener {

        void onCashout(OwnedCurrency ownedCurrency);

        void onDelete(OwnedCurrency ownedCurrency);

    }

    private OnEntryPopupItemSelectedListener popupListener;

    private Currency localCurrency;

    public OwnedCurrencyAdapter(Context cxt, List<OwnedCurrency> data,
                                OnEntryPopupItemSelectedListener popupListener) {
        super(cxt, data);
        this.popupListener = popupListener;
    }

    @Override
    public BaseAdapter<OwnedCurrency>.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_currency, parent, false));
    }

    public void setLocalCurrency(Currency localCurrency) {
        this.localCurrency = localCurrency;
    }

    class ViewHolder extends BaseAdapter<OwnedCurrency>.ViewHolder implements PopupMenu.OnMenuItemClickListener {

        @Bind(R.id.item_currency_icon)
        ImageView imgViewIcon;

        @Bind(R.id.item_currency_txt_amount)
        TextView txtAmount;

        @Bind(R.id.item_currency_txt_bought_price)
        TextView txtBoughtPrice;

        @Bind(R.id.item_currency_txt_bought_currency)
        TextView txtBoughtCurrency;

        @Bind(R.id.item_currency_txt_current_price)
        TextView txtCurrentPrice;

        @Bind(R.id.item_currency_txt_current_currency)
        TextView txtCurrentCurrency;

        @Bind(R.id.item_currency_txt_diff)
        TextView txtDiff;

        @Bind(R.id.item_currency_icon_imgbtn_overflow)
        ImageButton imgBtnOverflow;

        private PopupMenu popupMenu;

        ViewHolder(View itemView) {
            super(itemView);

            setupPopupMenu();
        }

        @Override
        public void bind(OwnedCurrency ownedCurrency) {
            content = ownedCurrency;

            imgViewIcon.setImageResource(getResourceForCryptoCurrency(
                    content.getCryptoCurrency()));
            txtAmount.setText(content.getAmount() + " "
                    + content.getCryptoCurrency().name());
            txtBoughtPrice.setText(String.valueOf(content.getBoughtPrice()));
            txtBoughtCurrency.setText(ResourceManager.getSymbolForCurrency(content.getBoughtCurrency()));
            txtCurrentPrice.setText(String.valueOf(content.getCurrentPrice()));
            txtCurrentCurrency.setText(ResourceManager.getSymbolForCurrency(localCurrency));

            double diff = content.getPriceDiffPercentage();
            int diffColor = diff >= 0 ? R.color.colorAccent : android.R.color.holo_red_light;
            txtDiff.setTextColor(ContextCompat.getColor(context, diffColor));
            txtDiff.setText(diff + "%");
        }

        @OnClick(R.id.item_currency_icon_imgbtn_overflow)
        public void onClickOverflow() {
            popupMenu.show();
        }

        private void setupPopupMenu() {
            popupMenu = new PopupMenu(context, imgBtnOverflow);
            popupMenu.getMenuInflater().inflate(R.menu.popup_item, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this);
            tryShowIconsInPopupMenu(popupMenu);
        }

        private void tryShowIconsInPopupMenu(PopupMenu menu) {

            try {
                Field fieldPopup = menu.getClass().getDeclaredField("mPopup");
                fieldPopup.setAccessible(true);
                MenuPopupHelper popup = (MenuPopupHelper) fieldPopup.get(menu);
                popup.setForceShowIcon(true);
            } catch (Exception e) {
                Log.d("Dante", "Cannot force to show icons in popupmenu");
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (popupListener == null) {
                return false;
            }

            switch (item.getItemId()) {

                case R.id.popup_item_cashout:

                    popupListener.onCashout(content);
                    break;

                case R.id.popup_item_delete:

                    popupListener.onDelete(content);
                    break;
            }
            return true;
        }
    }
}
