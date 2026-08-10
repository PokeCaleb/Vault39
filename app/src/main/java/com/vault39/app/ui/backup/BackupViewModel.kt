package com.vault39.app.ui.backup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vault39.app.data.backup.BackupManager
import com.vault39.app.data.backup.BackupResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BackupUiState {
    object Idle : BackupUiState()
    object Loading : BackupUiState()
    data class ExportSuccess(val message: String) : BackupUiState()
    data class ImportSuccess(val message: String) : BackupUiState()
    data class Failure(val message: String) : BackupUiState()
}

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<BackupUiState>(BackupUiState.Idle)
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    val suggestedFileName: String get() = backupManager.suggestedFileName()

    fun exportBackup(destination: Uri) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Loading
            when (val result = backupManager.exportTo(destination)) {
                is BackupResult.Success ->
                    _uiState.value = BackupUiState.ExportSuccess("Copia de seguridad guardada correctamente.")
                is BackupResult.Error ->
                    _uiState.value = BackupUiState.Failure(result.message)
            }
        }
    }

    fun importBackup(source: Uri) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Loading
            when (val result = backupManager.importFrom(source)) {
                is BackupResult.Success ->
                    _uiState.value = BackupUiState.ImportSuccess(
                        "Copia restaurada. Cierra y vuelve a abrir la app para ver los cambios."
                    )
                is BackupResult.Error ->
                    _uiState.value = BackupUiState.Failure(result.message)
            }
        }
    }

    fun resetState() {
        _uiState.value = BackupUiState.Idle
    }
}
