package at.shockbytes.coins.ui.activity

import android.Manifest
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.widget.TextView
import at.shockbytes.coins.R
import at.shockbytes.coins.dagger.AppComponent
import at.shockbytes.coins.util.AppParams
import at.shockbytes.coins.util.auth.CryptoWatcherFingerprintManager
import at.shockbytes.coins.util.auth.ShockFingerprintManager
import com.mattprecious.swirl.SwirlView
import kotterknife.bindView
import pub.devrel.easypermissions.EasyPermissions


class LoginActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {

    private val fingerprintView: SwirlView by bindView(R.id.activity_login_img_fingerprint)
    private val txtFingerprint: TextView by bindView(R.id.activity_login_fingerprint_error)

    private lateinit var fpManager: ShockFingerprintManager
    private val authCallback = object : FingerprintManagerCompat.AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            txtFingerprint.text = errString
        }

        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            goToMain(fpManager.deriveRealmKey(result))
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            fingerprintView.setState(SwirlView.State.ERROR, true)
            Handler().postDelayed({
                fingerprintView.setState(SwirlView.State.ON, true)
                txtFingerprint.text = ""
            },3000)
            txtFingerprint.text = getString(R.string.auth_failed)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.dark_background)
        }
        // Set it default to canceled and set to OK when 'save' is clicked
        setResult(Activity.RESULT_CANCELED, Intent())

        supportActionBar?.hide()
        setupFingerprint()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        authenticate()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {}

    override fun injectToGraph(appComponent: AppComponent) {
        // Do nothing
    }

    private fun setupFingerprint() {

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val fingerprintManager = FingerprintManagerCompat.from(this)
        fpManager = CryptoWatcherFingerprintManager(this, fingerprintManager)

        if (!keyguardManager.isKeyguardSecure) {
            txtFingerprint.text = getString(R.string.auth_lockscreen_not_enabled)
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
            txtFingerprint.text = getString(R.string.auth_no_fingerprint_registered)
            return
        }

        authenticate()
        fingerprintView.setState(SwirlView.State.ON, true)
    }

    private fun authenticate() {
        if (fpManager.setup()) {
            fpManager.authenticate(authCallback)
        } else {
            txtFingerprint.text = getString(R.string.auth_lib_error)
        }
    }

    private fun goToMain(realmKey: ByteArray) {
        setResult(Activity.RESULT_OK, Intent().putExtra("realm_key", realmKey))
        supportFinishAfterTransition()
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

}
