package at.shockbytes.coins.util.auth

import android.app.Activity
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import at.shockbytes.coins.R
import at.shockbytes.coins.util.AppParams

/**
 * @author Martin Macheiner
 * Date: 29.12.2017.
 */
class CryptoWatcherFingerprintManager(c: Activity,
                                      f: FingerprintManagerCompat): ShockFingerprintManager(c,f ) {

    override val keyName = "crypto_watcher_key"
    override val permissionRationale: String = c.getString(R.string.auth_perm_rationale)
    override val requestFingerprintCode = AppParams.requestFingerprintCode

}