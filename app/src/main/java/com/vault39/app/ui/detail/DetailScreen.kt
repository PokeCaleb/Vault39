package com.vault39.app.ui.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vault39.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    accountId: Long,
    onNavigateBack: () -> Unit,
    onEditAccount: (Long) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val account by viewModel.account.collectAsState()
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(accountId) { viewModel.loadAccount(accountId) }

    Scaffold(
        containerColor = MikuNavy,
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Cuenta", color = MikuTeal, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = MikuTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MikuNavySurface)
            )
        }
    ) { paddingValues ->
        account?.let { acc ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MikuNavy)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Account avatar header
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val avatarBrush = remember {
                            Brush.radialGradient(colors = listOf(MikuTeal.copy(0.3f), MikuTealDark.copy(0.1f)))
                        }
                        Box(
                            modifier = Modifier.size(80.dp).clip(CircleShape)
                                .background(avatarBrush),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = acc.accountName.firstOrNull()?.uppercase() ?: "?",
                                color = MikuTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                        }
                        Text(acc.accountName, color = MikuWhite, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (acc.isActive) MikuGreen.copy(alpha = 0.15f) else MikuGray.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = if (acc.isActive) "✦ Activa" else "○ Inactiva",
                                color = if (acc.isActive) MikuGreen else MikuGray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Data cards
                DetailCard(
                    label = "Correo de la cuenta",
                    value = acc.email,
                    icon = Icons.Default.Email
                )

                // Password card with visibility toggle and copy
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MikuNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MikuTeal.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MikuTeal, modifier = Modifier.size(18.dp))
                            Text("Contraseña", color = MikuGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = if (passwordVisible) acc.password else "••••••••••",
                                color = MikuWhite,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Row {
                                // Visibility toggle
                                IconButton(
                                    onClick = { passwordVisible = !passwordVisible },
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Mostrar/ocultar",
                                        tint = MikuTeal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                // Copy button
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("password", acc.password))
                                        Toast.makeText(context, "Contraseña copiada 🎵", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copiar contraseña", tint = MikuPink, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Visibilidad de contraseña", color = MikuGray, fontSize = 13.sp)
                            Switch(
                                checked = passwordVisible,
                                onCheckedChange = { passwordVisible = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MikuNavy,
                                    checkedTrackColor = MikuTeal,
                                    uncheckedThumbColor = MikuGray,
                                    uncheckedTrackColor = MikuGray.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Action buttons
                Button(
                    onClick = { onEditAccount(acc.id) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MikuTeal, contentColor = MikuNavy)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Editar cuenta", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MikuRed.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MikuRed)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar cuenta", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }
        } ?: run {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MikuTeal)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MikuNavyCard,
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MikuRed) },
            title = { Text("Eliminar cuenta", color = MikuWhite, fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar esta cuenta? Esta acción no se puede deshacer.", color = MikuGray) },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false; viewModel.deleteAccount(onNavigateBack) },
                    colors = ButtonDefaults.buttonColors(containerColor = MikuRed, contentColor = MikuWhite)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar", color = MikuTeal) }
            }
        )
    }
}

@Composable
private fun DetailCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MikuNavyCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, MikuTeal.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, contentDescription = null, tint = MikuTeal, modifier = Modifier.size(18.dp))
                Text(label, color = MikuGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Text(value.ifBlank { "—" }, color = MikuWhite, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}
