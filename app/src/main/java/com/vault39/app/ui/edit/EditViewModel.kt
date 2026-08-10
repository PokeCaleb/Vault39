package com.vault39.app.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vault39.app.data.model.Account
import com.vault39.app.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {

    private val _account = MutableStateFlow<Account?>(null)
    val account: StateFlow<Account?> = _account.asStateFlow()

    fun loadAccount(id: Long) {
        viewModelScope.launch {
            _account.value = repository.getAccountById(id)
        }
    }

    fun updateAccount(id: Long, name: String, email: String, password: String, active: Boolean, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.updateAccount(
                Account(id = id, accountName = name, email = email, password = password, isActive = active)
            )
            onDone()
        }
    }
}
