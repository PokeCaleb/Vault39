package com.vault39.app.di

import android.content.Context
import androidx.room.Room
import com.vault39.app.data.backup.BackupManager
import com.vault39.app.data.db.AccountDao
import com.vault39.app.data.db.VAULT_DATABASE_NAME
import com.vault39.app.data.db.VaultDatabase
import com.vault39.app.data.security.DatabaseKeyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseKeyManager(@ApplicationContext context: Context): DatabaseKeyManager =
        DatabaseKeyManager(context)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        keyManager: DatabaseKeyManager
    ): VaultDatabase {
        // Carga la librería nativa de SQLCipher antes de abrir la base de datos.
        System.loadLibrary("sqlcipher")

        val passphrase = keyManager.getOrCreateDatabasePassphrase()
        val factory = SupportOpenHelperFactory(passphrase)

        return Room.databaseBuilder(context, VaultDatabase::class.java, VAULT_DATABASE_NAME)
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAccountDao(db: VaultDatabase): AccountDao = db.accountDao()

    @Provides
    @Singleton
    fun provideBackupManager(
        @ApplicationContext context: Context,
        database: VaultDatabase
    ): BackupManager = BackupManager(context, database)
}
