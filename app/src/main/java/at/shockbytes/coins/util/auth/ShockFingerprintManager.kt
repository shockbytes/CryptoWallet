package at.shockbytes.coins.util.auth

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.v4.app.ActivityCompat
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v4.os.CancellationSignal
import at.shockbytes.coins.util.AppParams
import pub.devrel.easypermissions.EasyPermissions
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * @author Martin Macheiner
 * Date: 26.04.2017.
 */

@TargetApi(Build.VERSION_CODES.M)
class ShockFingerprintManager(private val context: Activity,
                              private val fingerprintManager: FingerprintManagerCompat) {

    private var keyStore: KeyStore? = null
    private var cipher: Cipher? = null

    private val cryptoObject: FingerprintManagerCompat.CryptoObject?
        get() = if (cipher != null) {
            FingerprintManagerCompat.CryptoObject(cipher)
        } else null

    fun setup(): Boolean {
        generateKey()
        return initializeCipher()
    }

    fun authenticate(callback: FingerprintManagerCompat.AuthenticationCallback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT)
                != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(
                    context,
                    "This app needs to access your fingerprint.",
                    AppParams.requestFingerprintCode,
                    Manifest.permission.USE_FINGERPRINT)
        } else {
            fingerprintManager.authenticate(cryptoObject, 0, CancellationSignal(), callback, null)
        }
    }

    private fun generateKey() {

        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore")
            val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyStore?.load(null)

            if (keyStore?.getKey(KEY_NAME, null) == null) {
                keyGenerator.init(KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build())
                val key = keyGenerator.generateKey()
                // TODO v1.2 - Do something with this key!
                println(key.toString())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun initializeCipher(): Boolean {

        return try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" +
                    KeyProperties.ENCRYPTION_PADDING_PKCS7)

            keyStore?.load(null)
            val key = keyStore?.getKey(KEY_NAME, null) as SecretKey
            cipher?.init(Cipher.ENCRYPT_MODE, key)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object {

        private val KEY_NAME = "shockfingerprint_key"
    }

}
