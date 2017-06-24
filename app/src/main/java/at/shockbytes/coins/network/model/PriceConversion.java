package at.shockbytes.coins.network.model;

import at.shockbytes.coins.currency.CryptoCurrency;

/**
 * @author Martin Macheiner
 *         Date: 14.06.2017.
 */

public class PriceConversion {

    public CryptoCurrency cryptoCurrency;
    public Data data;

    public PriceConversion() {
        data = new Data();
    }

    public String getTo() {
        return data.currency;
    }

    public double getConversionRate() {
        return data.amount;
    }

    @Override
    public String toString() {
        return "Amount: " + data.amount + " / Currency: " + data.currency
                + " / From: " + cryptoCurrency.name();
    }

    public class Data {

        public double amount;
        public String currency;

    }

}
