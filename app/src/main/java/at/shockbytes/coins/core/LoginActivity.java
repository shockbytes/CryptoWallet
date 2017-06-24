package at.shockbytes.coins.core;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import at.shockbytes.coins.R;
import at.shockbytes.coins.util.AppParams;
import at.shockbytes.coins.util.auth.ShockFingerprintManager;
import pub.devrel.easypermissions.EasyPermissions;


public class LoginActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks {

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }


    private ShockFingerprintManager shockFingerprintManager;
    private FingerprintManager.AuthenticationCallback authCallback = new FingerprintManager.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                makeSnackbar(errString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                goToMain(deriveRealmKey(result));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                makeSnackbar("Authentication failed");
            }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setupFingerprint();
    }

    private void setupFingerprint() {

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        shockFingerprintManager = new ShockFingerprintManager(this,
                fingerprintManager);

        if (!keyguardManager.isKeyguardSecure()) {
            makeSnackbar("Lock screen security not enabled!");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT)
                != PackageManager.PERMISSION_GRANTED) {

            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your fingerprint, Google account and your external storage.",
                    AppParams.REQUEST_FINGERPRINT_PERMISSION,
                    Manifest.permission.USE_FINGERPRINT);
            return;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {
            makeSnackbar("Please register a fingerprint first!");
            return;
        }

        authenticate();
    }

    private void authenticate() {
        if (shockFingerprintManager.setup()) {
            shockFingerprintManager.authenticate(authCallback);
        } else {
            makeSnackbar("Cannot initialize Fingerprint library!");
        }
    }

    private void makeSnackbar(String text) {
        Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show();
    }

    private void goToMain(byte[] realmKey) {
        setResult(RESULT_OK, new Intent().putExtra("realm_key", realmKey));
        supportFinishAfterTransition();
    }

    private byte[] deriveRealmKey(FingerprintManager.AuthenticationResult result) {

        // TODO Derive realm key cryptoCurrency fingerprint data
        return new byte[0];
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        authenticate();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

}
