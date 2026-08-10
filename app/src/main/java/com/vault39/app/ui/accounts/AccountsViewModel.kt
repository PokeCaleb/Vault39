package com.vault39.app.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vault39.app.data.model.Account
import com.vault39.app.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val accounts: StateFlow<List<Account>> = _searchQuery
        .debounce(250)
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllAccounts()
            else repository.searchAccounts(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) { _searchQuery.value = query }

    fun addAccount(name: String, email: String, password: String, active: Boolean) {
        viewModelScope.launch {
            repository.saveAccount(Account(accountName = name, email = email, password = password, isActive = active))
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch { repository.deleteAccount(account) }
    }
}
