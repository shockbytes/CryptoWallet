package at.shockbytes.coins.currency;

import at.shockbytes.coins.util.ResourceManager;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * @author Martin Macheiner
 *         Date: 15.06.2017.
 */

public class OwnedCurrency extends RealmObject {

    @PrimaryKey
    private long id;

    @Ignore
    private CryptoCurrency cryptoCurrency;
    private int cryptoCurrencyOrdinal;
    private double amount;

    @Ignore
    private Currency boughtCurrency;
    private int boughtCurrencyOrdinal;
    private double boughtPrice;

    private boolean isCashedOut;

    private double conversionRate;

    public OwnedCurrency() {
        this(CryptoCurrency.BTC, 0, Currency.EUR, 0);
    }

    public OwnedCurrency(CryptoCurrency cryptoCurrency, double amount,
                         Currency boughtCurrency, double boughtPrice) {
        this.cryptoCurrency = cryptoCurrency;
        this.cryptoCurrencyOrdinal = cryptoCurrency.ordinal();
        this.amount = amount;
        this.boughtCurrency = boughtCurrency;
        this.boughtCurrencyOrdinal = boughtCurrency.ordinal();
        this.boughtPrice = boughtPrice;
        this.isCashedOut = false;
    }

    public CryptoCurrency getCryptoCurrency() {
        return CryptoCurrency.values()[cryptoCurrencyOrdinal];
        // return cryptoCurrency;
    }

    public double getAmount() {
        return amount;
    }

    public Currency getBoughtCurrency() {
        return Currency.values()[boughtCurrencyOrdinal];
        //return boughtCurrency;
    }

    public double getBoughtPrice() {
        return boughtPrice;
    }

    public void setBoughtPrice(double boughtPrice) {
        this.boughtPrice = boughtPrice;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public double getCurrentPrice() {
        if (conversionRate != 0) {
            return ResourceManager.roundDoubleWithDigits(amount * conversionRate, 2);
        }
        return -1;
    }

    public double getPriceDiffPercentage(double bought) {
        double diff = (getCurrentPrice()  / (bought /100)) - 100;
        return ResourceManager.roundDoubleWithDigits(diff, 2);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isCashedOut() {
        return isCashedOut;
    }

    public void setCashedOut(boolean cashedOut) {
        isCashedOut = cashedOut;
    }

    @Override
    public String toString() {
        return boughtPrice + " " + boughtCurrency.name()
                + " -> " + amount + " " + cryptoCurrency.name() + " with rate: " + conversionRate;
    }

}
