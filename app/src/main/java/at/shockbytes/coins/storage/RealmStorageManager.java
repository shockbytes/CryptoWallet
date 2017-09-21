package at.shockbytes.coins.storage;

import java.util.List;

import javax.inject.Inject;

import at.shockbytes.coins.currency.OwnedCurrency;
import at.shockbytes.coins.network.model.PriceConversion;
import io.realm.Realm;
import io.realm.Sort;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Martin Macheiner
 *         Date: 16.06.2017.
 */

public class RealmStorageManager implements StorageManager {

    private Realm realm;

    @Inject
    public RealmStorageManager(Realm realm) {
        this.realm = realm;
    }

    @Override
    public void storeOwnedCurrency(OwnedCurrency currency) {

        realm.beginTransaction();
        currency.setId(getLastPrimaryKey());
        realm.copyToRealmOrUpdate(currency);
        realm.commitTransaction();
    }

    @Override
    public void removeOwnedCurrency(OwnedCurrency currency) {

        realm.beginTransaction();
        currency.deleteFromRealm();
        realm.commitTransaction();
    }

    @Override
    public void cashoutOwnedCurrency(OwnedCurrency currency) {

        realm.beginTransaction();
        currency.setCashedOut(true);
        realm.commitTransaction();
    }

    @Override
    public Observable<List<OwnedCurrency>> loadOwnedCurrencies(boolean isCashedOut) {
        List<OwnedCurrency> stored = realm.where(OwnedCurrency.class)
                .equalTo("isCashedOut", isCashedOut)
                .findAllAsync()
                .sort("id", Sort.DESCENDING);
        return Observable.just(stored)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public void updateConversionRates(OwnedCurrency oc, List<PriceConversion> conversions) {

        realm.beginTransaction();
        for (PriceConversion conversion : conversions) {
            if (oc.getCryptoCurrency() == conversion.cryptoCurrency) {
                oc.setConversionRate(conversion.getConversionRate());
                break;
            }
        }
        realm.commitTransaction();
    }

    @Override
    public OwnedCurrency getOwnedCurrencyById(long id) {
        return realm.where(OwnedCurrency.class).equalTo("id", id).findFirst();
    }

    @Override
    public void splitCashout(OwnedCurrency currency, double cashoutAmount) {

        double cashoutBoughtPrice = currency.getBoughtPrice()
                * (cashoutAmount / currency.getAmount());
        double leftBoughtPrice = currency.getBoughtPrice() - cashoutBoughtPrice;
        double leftAmount = currency.getAmount() - cashoutAmount;

        realm.beginTransaction();

        // Update old currency
        currency.setAmount(leftAmount);
        currency.setBoughtPrice(leftBoughtPrice);
        realm.copyToRealmOrUpdate(currency);

        // Create and store a cashout instance
        OwnedCurrency cashoutCurrency = new OwnedCurrency(currency.getCryptoCurrency(),
                cashoutAmount, currency.getBoughtCurrency(), cashoutBoughtPrice);
        cashoutCurrency.setCashedOut(true);
        cashoutCurrency.setId(getLastPrimaryKey());
        cashoutCurrency.setConversionRate(currency.getConversionRate());
        realm.copyToRealm(cashoutCurrency);

        realm.commitTransaction();
    }

    private long getLastPrimaryKey() {

        // This method can only be called inside a transaction
        ShockConfig config = realm.where(ShockConfig.class).findFirst();
        if (config == null) { // First time config is written
            config = realm.createObject(ShockConfig.class);
        }
        return config.getLastPrimaryKey();
    }
}
