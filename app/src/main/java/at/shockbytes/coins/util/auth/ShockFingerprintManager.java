package at.shockbytes.coins.util.auth;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import at.shockbytes.coins.util.AppParams;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author Martin Macheiner
 *         Date: 26.04.2017.
 */

@TargetApi(Build.VERSION_CODES.M)
public class ShockFingerprintManager {

    private static final String KEY_NAME = "shockfingerprint_key";

    private KeyStore keyStore;
    private Cipher cipher;

    private Activity context;
    private FingerprintManagerCompat fingerprintManager;

    public ShockFingerprintManager(Activity activity, FingerprintManagerCompat fingerprintManager) {
        this.fingerprintManager = fingerprintManager;
        this.context = activity;
    }

    public boolean setup() {
        generateKey();
        return initializeCipher();
    }

    private FingerprintManagerCompat.CryptoObject getCryptoObject() {
        if (cipher != null) {
            return new FingerprintManagerCompat.CryptoObject(cipher);
        }
        return null;
    }

    public void authenticate(@NonNull FingerprintManagerCompat.AuthenticationCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT)
                != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(
                    context,
                    "This app needs to access your fingerprint.",
                    AppParams.INSTANCE.getRequestFingerprintCode(),
                    Manifest.permission.USE_FINGERPRINT);
        } else {
            fingerprintManager.authenticate(getCryptoObject(), 0, new CancellationSignal(),callback, null);
        }
    }

    private void generateKey() {

        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);

            if (keyStore.getKey(KEY_NAME, null) == null) {
                keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());

                keyGenerator.generateKey();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean initializeCipher() {

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                                        KeyProperties.BLOCK_MODE_CBC + "/" +
                                        KeyProperties.ENCRYPTION_PADDING_PKCS7);

            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
