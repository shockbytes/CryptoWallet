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
import at.shockbytes.coins.currency.CurrencyConversionRates;
import at.shockbytes.coins.currency.OwnedCurrency;
import at.shockbytes.coins.fragment.MainFragment;
import at.shockbytes.coins.util.ResourceManager;
import butterknife.BindView;
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
    private CurrencyConversionRates conversionRates;

    private MainFragment.ViewType viewType;

    public OwnedCurrencyAdapter(Context cxt, List<OwnedCurrency> data,
                                MainFragment.ViewType viewType,
                                OnEntryPopupItemSelectedListener popupListener) {
        super(cxt, data);
        this.viewType = viewType;
        this.popupListener = popupListener;
    }

    @Override
    public BaseAdapter<OwnedCurrency>.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_currency, parent, false));
    }

    public void setLocalCurrency(Currency localCurrency, CurrencyConversionRates conversionRates) {
        this.localCurrency = localCurrency;
        this.conversionRates = conversionRates;
    }

    class ViewHolder extends BaseAdapter<OwnedCurrency>.ViewHolder implements PopupMenu.OnMenuItemClickListener {

        @BindView(R.id.item_currency_icon)
        ImageView imgViewIcon;

        @BindView(R.id.item_currency_txt_amount)
        TextView txtAmount;

        @BindView(R.id.item_currency_txt_bought_price)
        TextView txtBoughtPrice;

        @BindView(R.id.item_currency_txt_current_price)
        TextView txtCurrentPrice;

        @BindView(R.id.item_currency_txt_diff)
        TextView txtDiff;

        @BindView(R.id.item_currency_icon_imgbtn_overflow)
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
            txtAmount.setText(ResourceManager.roundDoubleWithDigits(content.getAmount(), 8)
                    + " " + content.getCryptoCurrency().name());


            double boughtPrice;
            if (content.getBoughtCurrency() != localCurrency) {
                boughtPrice = conversionRates.convert(content.getBoughtPrice(),
                        content.getBoughtCurrency(), localCurrency);
            } else {
                boughtPrice = content.getBoughtPrice();
            }

            txtBoughtPrice.setText(String.valueOf(ResourceManager.roundDoubleWithDigits(boughtPrice, 2))
                    + " " + ResourceManager.getSymbolForCurrency(localCurrency));

            txtCurrentPrice.setText(String.valueOf(content.getCurrentPrice())
                    + " " + ResourceManager.getSymbolForCurrency(localCurrency));

            double diff = content.getPriceDiffPercentage(boughtPrice);
            int diffColor = diff >= 0 ? R.color.percentage_win : R.color.percentage_loose_card;
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

            // Hide the cashout action if already cashed out
            if (viewType == MainFragment.ViewType.CASHOUT) {
                popupMenu.getMenu().getItem(0).setVisible(false);
            }

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
