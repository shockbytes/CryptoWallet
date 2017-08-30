package at.shockbytes.coins.currency;

/**
 * @author Martin Macheiner
 *         Date: 01.07.2017.
 */

public class CurrencyConversionRates {

    private String base;
    private String date;

    private Rates rates;

    public CurrencyConversionRates() {

    }

    public double convert(double value, Currency from, Currency to) {

        // Do nothing in this case
        if (from == to) {
            return value;
        } else if (from.name().equals(base)) {
            // Everything is straightforward
            return value * getRateForCurrency(to);
        } else {
            // A conversion from the requested to the base currency
            return value / getRateForCurrency(from);
        }

    }

    private double getRateForCurrency(Currency currency) {

        switch (currency) {

            case EUR:
                return rates.EUR;

            case USD:
                return rates.USD;

            /*
            case GBP:
                return rates.GBP;

            case CHF:
                return rates.CHF; */
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Base: " + base + "\nDate: " + date + "\n" + rates.toString();
    }

    static class Rates {

        double CHF;
        double GBP;
        double EUR;
        double USD;

        @Override
        public String toString() {
            return "To CHF: " + CHF + "\nTo GBP: " + GBP + "\nTo EUR: " + EUR + "\nTo USD: " + USD;
        }

        Rates defaultInitialize() {
            CHF = GBP = EUR = USD = 1;
            return this;
        }
    }

    public static CurrencyConversionRates getDefaultCurrencyConversionRates() {

        CurrencyConversionRates def = new CurrencyConversionRates();
        def.base = "EUR";
        def.date = "";
        def.rates = new Rates().defaultInitialize();

        return def;
    }

}
