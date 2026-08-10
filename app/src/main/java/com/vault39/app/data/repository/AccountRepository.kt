package com.vault39.app.data.repository

import com.vault39.app.data.db.AccountDao
import com.vault39.app.data.model.Account
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val dao: AccountDao
) {
    fun getAllAccounts(): Flow<List<Account>> = dao.getAllAccounts()

    fun searchAccounts(query: String): Flow<List<Account>> = dao.searchAccounts(query)

    suspend fun getAccountById(id: Long): Account? = dao.getAccountById(id)

    suspend fun saveAccount(account: Account) = dao.insertAccount(account)

    suspend fun updateAccount(account: Account) = dao.updateAccount(account)

    suspend fun deleteAccount(account: Account) = dao.deleteAccount(account)
}
