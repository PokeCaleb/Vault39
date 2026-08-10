package com.vault39.app.ui.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vault39.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    onNavigateBack: () -> Unit,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri -> uri?.let { viewModel.exportBackup(it) } }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { viewModel.importBackup(it) } }

    var showImportWarning by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MikuNavy,
        topBar = {
            TopAppBar(
                title = { Text("Copia de seguridad", color = MikuTeal, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = MikuTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MikuNavySurface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MikuNavy)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape)
                            .background(MikuTeal.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, tint = MikuTeal, modifier = Modifier.size(36.dp))
                    }
                    Text("Respalda o restaura tus cuentas", color = MikuGray, fontSize = 13.sp)
                }
            }

            InfoCard(
                text = "El archivo exportado ya viene cifrado, igual que tu base de datos. " +
                    "Solo esta instalación de Vault39, en este dispositivo, puede volver a abrirlo."
            )

            BackupActionCard(
                icon = Icons.Default.Upload,
                title = "Exportar copia de seguridad",
                description = "Guarda un archivo .db con todas tus cuentas donde tú elijas (almacenamiento local, Drive, etc).",
                buttonText = "Exportar",
                buttonColor = MikuTeal,
                onClick = { exportLauncher.launch(viewModel.suggestedFileName) }
            )

            BackupActionCard(
                icon = Icons.Default.Download,
                title = "Restaurar desde archivo",
                description = "Reemplaza tus cuentas actuales con las de un archivo de respaldo previamente exportado.",
                buttonText = "Restaurar",
                buttonColor = MikuPink,
                onClick = { showImportWarning = true }
            )

            when (val state = uiState) {
                is BackupUiState.Loading -> {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircularProgressIndicator(color = MikuTeal, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Text("Procesando...", color = MikuGray, fontSize = 13.sp)
                    }
                }
                is BackupUiState.ExportSuccess -> StatusMessage(state.message, MikuGreen)
                is BackupUiState.ImportSuccess -> StatusMessage(state.message, MikuGreen)
                is BackupUiState.Failure -> StatusMessage(state.message, MikuRed)
                BackupUiState.Idle -> Unit
            }
        }
    }

    if (showImportWarning) {
        AlertDialog(
            onDismissRequest = { showImportWarning = false },
            containerColor = MikuNavyCard,
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MikuRed) },
            title = { Text("Restaurar copia de seguridad", color = MikuWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Esto reemplazará todas las cuentas guardadas actualmente por las del archivo " +
                        "que selecciones. Esta acción no se puede deshacer.",
                    color = MikuGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showImportWarning = false
                        importLauncher.launch(arrayOf("application/octet-stream", "*/*"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MikuRed, contentColor = MikuWhite)
                ) { Text("Continuar") }
            },
            dismissButton = {
                TextButton(onClick = { showImportWarning = false }) { Text("Cancelar", color = MikuTeal) }
            }
        )
    }
}

@Composable
private fun InfoCard(text: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MikuNavyCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, MikuTeal.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = MikuTeal, modifier = Modifier.size(18.dp))
            Text(text, color = MikuGray, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun BackupActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    buttonText: String,
    buttonColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MikuNavyCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, buttonColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(icon, contentDescription = null, tint = buttonColor, modifier = Modifier.size(20.dp))
                Text(title, color = MikuWhite, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Text(description, color = MikuGray, fontSize = 13.sp, lineHeight = 18.sp)
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = MikuNavy)
            ) {
                Text(buttonText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun StatusMessage(message: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            message,
            color = color,
            fontSize = 13.sp,
            modifier = Modifier.padding(12.dp)
        )
    }
}
