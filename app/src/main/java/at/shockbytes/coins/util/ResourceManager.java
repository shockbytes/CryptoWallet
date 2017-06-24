package at.shockbytes.coins.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import at.shockbytes.coins.R;
import at.shockbytes.coins.currency.CryptoCurrency;
import at.shockbytes.coins.currency.Currency;

/**
 * @author Martin Macheiner
 *         Date: 15.06.2017.
 */

public class ResourceManager {

    public static double roundDoubleWithDigits(double value, int digits) {

        if (value == 0) {
            return 0.00;
        }

        if (digits < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(digits, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int getResourceForCryptoCurrency(CryptoCurrency currency) {

        switch (currency) {

            case BTC:
                return R.drawable.ic_btc;
            case ETH:
                return R.drawable.ic_eth;
            case LTC:
                return R.drawable.ic_ltc;
        }
        return 0;
    }

    public static int getResourceForCurrency(Currency currency) {


        switch (currency) {

            case EUR:
                return R.drawable.ic_eur;
            case USD:
                return R.drawable.ic_usd;
        }
        return 0;

    }

    public static String getSymbolForCurrency(Currency currency) {

        switch (currency) {

            case EUR:
                return "€";
            case USD:
                return "$";
        }
        return "";
    }

}
