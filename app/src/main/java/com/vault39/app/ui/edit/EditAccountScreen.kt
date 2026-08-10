package com.vault39.app.ui.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vault39.app.ui.add.MikuTextField
import com.vault39.app.ui.add.mikuTextFieldColors
import com.vault39.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(
    accountId: Long,
    onNavigateBack: () -> Unit,
    viewModel: EditViewModel = hiltViewModel()
) {
    val originalAccount by viewModel.account.collectAsState()

    var accountName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(accountId) { viewModel.loadAccount(accountId) }
    LaunchedEffect(originalAccount) {
        if (!loaded && originalAccount != null) {
            originalAccount!!.let {
                accountName = it.accountName
                email = it.email
                password = it.password
                isActive = it.isActive
                loaded = true
            }
        }
    }

    Scaffold(
        containerColor = MikuNavy,
        topBar = {
            TopAppBar(
                title = { Text("Editar Cuenta", color = MikuTeal, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Edit header
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape)
                            .background(MikuPink.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null, tint = MikuPink, modifier = Modifier.size(36.dp))
                    }
                    Text("Modificar datos", color = MikuGray, fontSize = 13.sp)
                }
            }

            MikuTextField(
                value = accountName,
                onValueChange = { accountName = it; nameError = false },
                label = "Nombre de la cuenta",
                icon = Icons.Default.Label,
                isError = nameError,
                errorMessage = if (nameError) "El nombre es obligatorio" else null
            )

            MikuTextField(
                value = email,
                onValueChange = { email = it },
                label = "Correo de la cuenta",
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña de la cuenta", color = MikuGray) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MikuTeal) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar/ocultar",
                            tint = MikuTeal
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = mikuTextFieldColors(),
                singleLine = true
            )

            // Active switch
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MikuNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, MikuTeal.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(
                            imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isActive) MikuGreen else MikuGray,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text("Estado de la cuenta", color = MikuWhite, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                            Text(if (isActive) "Activa" else "Inactiva", color = if (isActive) MikuGreen else MikuGray, fontSize = 12.sp)
                        }
                    }
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MikuNavy,
                            checkedTrackColor = MikuTeal,
                            uncheckedThumbColor = MikuGray,
                            uncheckedTrackColor = MikuGray.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MikuGray.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MikuGray)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Cancelar", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        if (accountName.isBlank()) { nameError = true; return@Button }
                        viewModel.updateAccount(accountId, accountName.trim(), email.trim(), password, isActive, onNavigateBack)
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MikuTeal, contentColor = MikuNavy)
                ) {
                    Icon(Icons.Default.Update, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Actualizar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
