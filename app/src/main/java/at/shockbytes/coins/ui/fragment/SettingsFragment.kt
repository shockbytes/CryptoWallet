package at.shockbytes.coins.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v4.app.ActivityCompat
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import at.shockbytes.coins.R
import at.shockbytes.coins.core.CoinsApp
import at.shockbytes.coins.currency.Currency
import at.shockbytes.coins.currency.CurrencyManager
import javax.inject.Inject

class SettingsFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {

    @Inject
    lateinit var currencyManager: CurrencyManager

    private lateinit var localCurrencyPref: ListPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as CoinsApp).appComponent.inject(this)
        addPreferencesFromResource(R.xml.settings)

        localCurrencyPref = findPreference(getString(R.string.prefs_key_local_currency)) as ListPreference
        localCurrencyPref.summary = currencyManager.localCurrency.name
        localCurrencyPref.onPreferenceChangeListener = this

        checkForFingerprintSecurity()
    }

    override fun onPreferenceChange(preference: Preference, o: Any): Boolean {
        val value = o as String
        localCurrencyPref.summary = value
        currencyManager.localCurrency = Currency.valueOf(value)
        return true
    }

    private fun checkForFingerprintSecurity() {

        val fingerprintManager = FingerprintManagerCompat.from(activity)
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
            if (!fingerprintManager.isHardwareDetected) {
                findPreference(getString(R.string.prefs_key_fingerprint_as_auth)).isEnabled = false
            }
        }
    }

    companion object {

        fun newInstance(): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}
