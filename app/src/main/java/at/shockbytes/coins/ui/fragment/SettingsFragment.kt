package at.shockbytes.coins.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v4.app.ActivityCompat
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import at.shockbytes.coins.R
import at.shockbytes.coins.core.CryptoWatcherApp
import at.shockbytes.coins.currency.CurrencyManager
import at.shockbytes.coins.currency.RealCurrency
import at.shockbytes.coins.currency.price.PriceProxy
import at.shockbytes.coins.ui.activity.BackNavigableActivity
import at.shockbytes.coins.ui.fragment.dialog.LocalCurrencyDialogFragment
import at.shockbytes.coins.ui.fragment.dialog.PriceProviderDialogFragment
import javax.inject.Inject

class SettingsFragment : PreferenceFragment(),
        Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @Inject
    lateinit var currencyManager: CurrencyManager

    @Inject
    lateinit var priceProxy: PriceProxy

    private lateinit var localCurrencyPref: Preference

    private lateinit var providerSelectionPref: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as CryptoWatcherApp).appComponent.inject(this)
        addPreferencesFromResource(R.xml.settings)

        setupLocalCurrency()
        setupProviderSelection()
        checkForFingerprintSecurity()
    }

    override fun onPreferenceChange(preference: Preference, o: Any): Boolean {
        val value = o as String
        localCurrencyPref.summary = value
        currencyManager.localCurrency = RealCurrency.valueOf(value)
        return true
    }

    override fun onPreferenceClick(pref: Preference?): Boolean {

        return when (pref?.key){
            getString(R.string.prefs_key_price_provider_selection) -> {
                PriceProviderDialogFragment.newInstance()
                        .setOnCompletionListener { providerSelectionPref.summary = buildProviderSummary() }
                        .show((activity as BackNavigableActivity).supportFragmentManager,
                                "price-provider-selection-dialogfragment")
                true
            }
            getString(R.string.prefs_key_local_currency) -> {
                LocalCurrencyDialogFragment.newInstance()
                        .setOnCompletionListener {
                            localCurrencyPref.summary = currencyManager.localCurrency.name
                        }
                        .show((activity as BackNavigableActivity).supportFragmentManager,
                                "local-currency-dialog-fragment")
                true
            }
            else -> false
        }
    }

    private fun setupLocalCurrency() {
        localCurrencyPref = findPreference(getString(R.string.prefs_key_local_currency)) as Preference
        localCurrencyPref.summary = currencyManager.localCurrency.name
        localCurrencyPref.onPreferenceClickListener = this
    }

    private fun setupProviderSelection() {
        providerSelectionPref = findPreference(
                getString(R.string.prefs_key_price_provider_selection))
        providerSelectionPref.summary = buildProviderSummary()
        providerSelectionPref.onPreferenceClickListener = this
    }

    private fun buildProviderSummary(): String {
        return priceProxy.priceProvider.filter { it.isEnabled }
                .joinToString(", ") { it.providerInfo.name }
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
