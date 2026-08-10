package com.vault39.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountName: String,
    val email: String,
    val password: String,
    val isActive: Boolean = true
)
