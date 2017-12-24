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
import at.shockbytes.coins.currency.RealCurrency;
import at.shockbytes.coins.currency.conversion.CurrencyConversionRates;
import at.shockbytes.coins.ui.fragment.MainFragment;
import at.shockbytes.coins.util.CoinUtils;
import at.shockbytes.coins.util.ResourceManager;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Martin Macheiner
 *         Date: 15.06.2017.
 */

public class CurrencyAdapter extends BaseAdapter<Currency> {

    public interface OnEntryPopupItemSelectedListener {

        void onCashout(Currency ownedCurrency);

        void onDelete(Currency ownedCurrency);

    }

    private OnEntryPopupItemSelectedListener popupListener;

    private RealCurrency localCurrency;
    private CurrencyConversionRates conversionRates;

    private MainFragment.ViewType viewType;

    public CurrencyAdapter(Context cxt, List<Currency> data,
                           MainFragment.ViewType viewType,
                           OnEntryPopupItemSelectedListener popupListener) {
        super(cxt, data);
        this.viewType = viewType;
        this.popupListener = popupListener;
    }

    @Override
    public BaseAdapter<Currency>.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_currency, parent, false));
    }

    public void setLocalCurrency(RealCurrency localCurrency, CurrencyConversionRates conversionRates) {
        this.localCurrency = localCurrency;
        this.conversionRates = conversionRates;
    }

    class ViewHolder extends BaseAdapter<Currency>.ViewHolder implements PopupMenu.OnMenuItemClickListener {

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
        public void bind(Currency ownedCurrency) {
            content = ownedCurrency;

            imgViewIcon.setImageResource(CoinUtils.INSTANCE.getResourceForCryptoCurrency(
                    content.getCryptoCurrency()));
            txtAmount.setText(ResourceManager.roundDoubleWithDigits(content.getCryptoAmount(), 8)
                    + " " + content.getCryptoCurrency().name());

            double boughtPrice;
            if (content.getRealCurrency() != localCurrency) {
                boughtPrice = conversionRates.convert(content.getRealAmount(),
                        content.getRealCurrency(), localCurrency);
            } else {
                boughtPrice = content.getRealAmount();
            }

            txtBoughtPrice.setText(String.valueOf(ResourceManager.roundDoubleWithDigits(boughtPrice, 2))
                    + " " + CoinUtils.INSTANCE.getSymbolForCurrency(localCurrency));

            txtCurrentPrice.setText(String.valueOf(content.getCurrentPrice())
                    + " " + CoinUtils.INSTANCE.getSymbolForCurrency(localCurrency));

            double diff = content.getPricePercentageDiff(boughtPrice);
            int diffColor = diff >= 0 ? R.color.percentage_win : R.color.percentage_loose;
            txtDiff.setTextColor(ContextCompat.getColor(context, diffColor));
            txtDiff.setText(diff + "%");
        }

        @OnClick(R.id.item_currency_icon_imgbtn_overflow)
        void onClickOverflow() {
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

                case R.id.popup_item_remove:

                    popupListener.onDelete(content);
                    break;
            }
            return true;
        }
    }
}