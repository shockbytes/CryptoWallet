package at.shockbytes.coins.currency;

import at.shockbytes.coins.util.ResourceManager;

/**
 * @author Martin Macheiner
 *         Date: 15.06.2017.
 */

public class Balance {

    private double invested;
    private double current;

    public Balance() {

    }

    public double getInvested() {
        return ResourceManager.roundDoubleWithDigits(invested, 2);
    }

    public void setInvested(double invested) {
        this.invested = invested;
    }

    public double getCurrent() {
        return ResourceManager.roundDoubleWithDigits(current, 2);
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getPercentageDiff() {

        if (invested == 0) {
            return 0;
        }

        double diff = (current  / (invested/100)) - 100;
        return ResourceManager.roundDoubleWithDigits(diff, 2);
    }

    public void addInvested(double price) {
        invested += price;
    }

    public void addCurrent(double price) {
        current += price;
    }
}
