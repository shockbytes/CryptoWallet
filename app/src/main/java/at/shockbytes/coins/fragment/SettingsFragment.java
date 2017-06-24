package at.shockbytes.coins.fragment;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import javax.inject.Inject;

import at.shockbytes.coins.R;
import at.shockbytes.coins.core.CoinsApp;
import at.shockbytes.coins.currency.Currency;
import at.shockbytes.coins.currency.CurrencyManager;

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    @Inject
    protected CurrencyManager currencyManager;

    private ListPreference localCurrencyPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CoinsApp) getActivity().getApplication()).getAppComponent().inject(this);
        addPreferencesFromResource(R.xml.settings);

        localCurrencyPref = (ListPreference) findPreference(getString(R.string.prefs_key_local_currency));
        localCurrencyPref.setSummary(currencyManager.getLocalCurrency().name());
        localCurrencyPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        String value = (String) o;
        localCurrencyPref.setSummary(value);
        currencyManager.setLocalCurrency(Currency.valueOf(value));
        return true;
    }
}
