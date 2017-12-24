package at.shockbytes.coins.ui.activity

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat

import at.shockbytes.coins.R
import at.shockbytes.coins.dagger.AppComponent
import at.shockbytes.coins.util.AppParams
import at.shockbytes.coins.util.auth.ShockFingerprintManager
import pub.devrel.easypermissions.EasyPermissions


class LoginActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {

    private var shockFingerprintManager: ShockFingerprintManager? = null
    private val authCallback = object : FingerprintManagerCompat.AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            showSnackbar(errString.toString())
        }

        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            goToMain(deriveRealmKey(result))
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            showSnackbar(getString(R.string.auth_failed))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.dark_background)
        }

        supportActionBar?.hide()
        setupFingerprint()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        authenticate()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) { }

    override fun injectToGraph(appComponent: AppComponent) {
        // Do nothing
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun setupFingerprint() {

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val fingerprintManager = FingerprintManagerCompat.from(this)
        shockFingerprintManager = ShockFingerprintManager(this, fingerprintManager)

        if (!keyguardManager.isKeyguardSecure) {
            showSnackbar(getString(R.string.auth_lockscreen_not_enabled))
            return
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)
                != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.auth_perm_rationale),
                    AppParams.requestFingerprintCode,
                    Manifest.permission.USE_FINGERPRINT)
            return
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {
            showSnackbar(getString(R.string.auth_no_fingerprint_registered))
            return
        }

        authenticate()
    }

    private fun authenticate() {
        if (shockFingerprintManager?.setup() == true) {
            shockFingerprintManager?.authenticate(authCallback)
        } else {
            showSnackbar(getString(R.string.auth_lib_error))
        }
    }

    private fun goToMain(realmKey: ByteArray) {
        setResult(Activity.RESULT_OK, Intent().putExtra("realm_key", realmKey))
        supportFinishAfterTransition()
    }

    private fun deriveRealmKey(result: FingerprintManagerCompat.AuthenticationResult?): ByteArray {

        // TODO Derive realm key cryptoCurrency fingerprint data
        println(result?.toString())
        return ByteArray(0)
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

}
