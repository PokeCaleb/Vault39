package com.vault39.app.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

/**
 * Genera y protege la passphrase que cifra la base de datos SQLCipher.
 *
 * La clave real de cifrado de la base de datos NUNCA se deriva del PIN del
 * usuario (un PIN de 6 dígitos es demasiado débil como llave criptográfica).
 * En su lugar, se genera una clave aleatoria de 256 bits una sola vez y se
 * guarda cifrada con Android Keystore mediante EncryptedSharedPreferences.
 * El PIN solo sirve para controlar el acceso a la UI (ver [PinManager]).
 */
class DatabaseKeyManager(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "vault39_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Devuelve la passphrase de la base de datos (en bytes UTF-8), generándola
     * la primera vez que se llama (por ejemplo, en la primera ejecución de la app).
     */
    fun getOrCreateDatabasePassphrase(): ByteArray {
        val existing = prefs.getString(KEY_DB_PASSPHRASE, null)
        if (existing != null) return existing.toByteArray(Charsets.UTF_8)

        val newPassphrase = generateRandomPassphrase()
        prefs.edit().putString(KEY_DB_PASSPHRASE, newPassphrase).apply()
        return newPassphrase.toByteArray(Charsets.UTF_8)
    }

    private fun generateRandomPassphrase(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val KEY_DB_PASSPHRASE = "db_passphrase"
    }
}
