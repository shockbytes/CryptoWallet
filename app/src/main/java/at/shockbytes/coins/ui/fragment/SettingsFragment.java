package at.shockbytes.coins.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;

import javax.inject.Inject;

import at.shockbytes.coins.R;
import at.shockbytes.coins.core.CoinsApp;
import at.shockbytes.coins.currency.Currency;
import at.shockbytes.coins.currency.CurrencyManager;

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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

        checkForFingerprintSecurity();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String value = (String) o;
        localCurrencyPref.setSummary(value);
        currencyManager.setLocalCurrency(Currency.valueOf(value));
        return true;
    }

    private void checkForFingerprintSecurity() {

        FingerprintManager fingerprintManager = (FingerprintManager) getContext()
                .getSystemService(Context.FINGERPRINT_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
            if (!fingerprintManager.isHardwareDetected()) {
                findPreference(getString(R.string.prefs_key_fingerprint_as_auth)).setEnabled(false);
            }
        }
    }

}
