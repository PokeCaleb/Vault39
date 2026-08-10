package com.vault39.app.data.backup

import android.content.Context
import android.net.Uri
import androidx.sqlite.db.SimpleSQLiteQuery
import com.vault39.app.data.db.VAULT_DATABASE_NAME
import com.vault39.app.data.db.VaultDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

sealed class BackupResult {
    object Success : BackupResult()
    data class Error(val message: String) : BackupResult()
}

/**
 * Exporta e importa el archivo físico de la base de datos (ya cifrada por
 * SQLCipher). Como el archivo ya está cifrado en disco, la copia de
 * seguridad resultante también queda protegida: sin la clave (guardada de
 * forma segura en este dispositivo) el archivo exportado no se puede leer.
 */
class BackupManager(
    private val context: Context,
    private val database: VaultDatabase
) {

    private fun databaseFile(): File = context.getDatabasePath(VAULT_DATABASE_NAME)

    /** Copia el archivo de base de datos actual hacia el Uri elegido por el usuario. */
    suspend fun exportTo(destination: Uri): BackupResult = withContext(Dispatchers.IO) {
        try {
            val dbFile = databaseFile()
            if (!dbFile.exists()) {
                return@withContext BackupResult.Error("No se encontró la base de datos para respaldar.")
            }

            // Vuelca todo lo pendiente del WAL (write-ahead log) al archivo
            // principal antes de copiarlo, para que el respaldo quede completo.
            database.openHelper.writableDatabase
                .query(SimpleSQLiteQuery("PRAGMA wal_checkpoint(FULL)"))
                .close()

            context.contentResolver.openOutputStream(destination)?.use { output ->
                dbFile.inputStream().use { input -> input.copyTo(output) }
            } ?: return@withContext BackupResult.Error("No se pudo abrir el destino seleccionado.")

            BackupResult.Success
        } catch (e: Exception) {
            BackupResult.Error(e.message ?: "Error desconocido al exportar el respaldo.")
        }
    }

    /**
     * Reemplaza la base de datos actual con el contenido del archivo elegido.
     * IMPORTANTE: el archivo debe ser un respaldo generado por esta misma
     * app en este mismo dispositivo, ya que la base de datos está cifrada
     * con una clave que nunca sale del dispositivo (ver DatabaseKeyManager).
     */
    suspend fun importFrom(source: Uri): BackupResult = withContext(Dispatchers.IO) {
        try {
            database.close()
            val dbFile = databaseFile()

            context.contentResolver.openInputStream(source)?.use { input ->
                dbFile.outputStream().use { output -> input.copyTo(output) }
            } ?: return@withContext BackupResult.Error("No se pudo leer el archivo seleccionado.")

            // Elimina archivos auxiliares de WAL/SHM obsoletos para forzar
            // que la app abra limpio el archivo recién restaurado.
            File(dbFile.path + "-wal").delete()
            File(dbFile.path + "-shm").delete()

            BackupResult.Success
        } catch (e: Exception) {
            BackupResult.Error(e.message ?: "Error desconocido al importar el respaldo.")
        }
    }

    fun suggestedFileName(): String {
        val timestamp = System.currentTimeMillis()
        return "vault39_backup_$timestamp.db"
    }
}
