package com.vault39.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vault39.app.data.model.Account

const val VAULT_DATABASE_NAME = "vault39_db"

@Database(entities = [Account::class], version = 1, exportSchema = true)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
}
