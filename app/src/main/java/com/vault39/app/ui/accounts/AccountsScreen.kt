package com.vault39.app.ui.accounts

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vault39.app.data.model.Account
import com.vault39.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    onAccountClick: (Long) -> Unit,
    onAddAccount: () -> Unit,
    onOpenBackup: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        containerColor = MikuNavy,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Cuentas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MikuTeal,
                        letterSpacing = 1.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MikuNavySurface
                ),
                actions = {
                    IconButton(onClick = onOpenBackup) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MikuTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Copia de seguridad",
                                tint = MikuTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAccount,
                containerColor = MikuPink,
                contentColor = MikuNavy,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar cuenta", modifier = Modifier.size(28.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MikuNavy)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Buscar por nombre...", color = MikuGray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MikuTeal)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = MikuGray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MikuTeal,
                    unfocusedBorderColor = MikuGray.copy(alpha = 0.4f),
                    focusedTextColor = MikuWhite,
                    unfocusedTextColor = MikuWhite,
                    cursorColor = MikuTeal,
                    focusedContainerColor = MikuNavyCard,
                    unfocusedContainerColor = MikuNavyCard
                )
            )

            if (accounts.isEmpty()) {
                EmptyAccountsMessage()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(accounts, key = { it.id }) { account ->
                        AccountCard(account = account, onClick = { onAccountClick(account.id) })
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EmptyAccountsMessage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                imageVector = Icons.Default.FolderOff,
                contentDescription = null,
                tint = MikuGray.copy(alpha = 0.4f),
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = "No hay cuentas.",
                fontSize = 18.sp,
                color = MikuGray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Toca + para agregar una.",
                fontSize = 14.sp,
                color = MikuTeal.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun AccountCard(account: Account, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MikuNavyCard,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (account.isActive) MikuTeal.copy(alpha = 0.5f) else MikuGray.copy(alpha = 0.25f)
        ),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar circle with first letter
            val avatarBrush = remember {
                Brush.radialGradient(
                    colors = listOf(MikuTeal.copy(alpha = 0.3f), MikuTealDark.copy(alpha = 0.1f))
                )
            }
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(avatarBrush),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = account.accountName.firstOrNull()?.uppercase() ?: "?",
                    color = MikuTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = account.accountName,
                    color = MikuWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = account.email,
                    color = MikuGray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Active badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (account.isActive) MikuGreen.copy(alpha = 0.15f) else MikuGray.copy(alpha = 0.1f)
            ) {
                Text(
                    text = if (account.isActive) "Activa" else "Inactiva",
                    color = if (account.isActive) MikuGreen else MikuGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MikuTeal.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
