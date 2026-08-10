package com.vault39.app.ui.login

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vault39.app.data.security.PinManager
import com.vault39.app.theme.*

// ── Estados del flujo de PIN ──────────────────────────────────────────────
private enum class PinStep {
    CREATE,    // Primera vez: crear PIN
    CONFIRM,   // Primera vez: confirmar PIN
    ENTER      // Ya existe PIN: ingresar para acceder
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val pinManager = remember { PinManager(context) }
    val hasPin = remember { pinManager.hasPin() }

    var step by remember {
        mutableStateOf(if (!hasPin) PinStep.CREATE else PinStep.ENTER)
    }
    var pin         by remember { mutableStateOf("") }
    var firstPin    by remember { mutableStateOf("") }   // guarda el PIN en CREATE
    var errorMsg    by remember { mutableStateOf("") }
    var successMsg  by remember { mutableStateOf("") }

    val bgBrush = remember {
        Brush.verticalGradient(colors = listOf(MikuNavy, MikuNavySurface, MikuNavyCard))
    }

    // Título e instrucción según el paso
    val (title, subtitle, dotColor) = when (step) {
        PinStep.CREATE  -> Triple("Vault39",  "Crea tu PIN de 6 dígitos",      MikuTeal)
        PinStep.CONFIRM -> Triple("Confirmar",  "Repite tu PIN para confirmar",   MikuPink)
        PinStep.ENTER   -> Triple("Vault39",  "Ingresa tu PIN para acceder",    MikuTeal)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(brush = bgBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Ícono
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape)
                    .background(dotColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (step == PinStep.ENTER) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = dotColor,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(title, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = MikuTeal, letterSpacing = 3.sp)
            Text(subtitle, fontSize = 14.sp, color = MikuGray, textAlign = TextAlign.Center)

            // Puntos del PIN
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(6) { index ->
                    val filled = index < pin.length
                    val color by animateColorAsState(
                        targetValue = if (filled) dotColor else MikuGray.copy(alpha = 0.3f),
                        animationSpec = tween(200), label = "dot$index"
                    )
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(color))
                }
            }

            // Mensajes
            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MikuRed, fontSize = 13.sp, textAlign = TextAlign.Center)
            }
            if (successMsg.isNotEmpty()) {
                Text(successMsg, color = MikuGreen, fontSize = 13.sp, textAlign = TextAlign.Center)
            }

            // Teclado
            MikuKeypad(
                onDigit = { digit ->
                    if (pin.length < 6) {
                        pin += digit
                        errorMsg = ""
                        successMsg = ""

                        if (pin.length == 6) {
                            when (step) {
                                PinStep.CREATE -> {
                                    firstPin = pin
                                    pin = ""
                                    step = PinStep.CONFIRM
                                    successMsg = "Ahora confírmalo 🎵"
                                }
                                PinStep.CONFIRM -> {
                                    if (pin == firstPin) {
                                        pinManager.savePin(pin)
                                        successMsg = "¡PIN creado! Accediendo..."
                                        onLoginSuccess()
                                    } else {
                                        errorMsg = "Los PINs no coinciden. Intenta de nuevo."
                                        pin = ""
                                        firstPin = ""
                                        step = PinStep.CREATE
                                    }
                                }
                                PinStep.ENTER -> {
                                    if (pinManager.verifyPin(pin)) {
                                        onLoginSuccess()
                                    } else {
                                        errorMsg = "PIN incorrecto. Intenta de nuevo."
                                        pin = ""
                                    }
                                }
                            }
                        }
                    }
                },
                onDelete = {
                    if (pin.isNotEmpty()) pin = pin.dropLast(1)
                    errorMsg = ""
                }
            )
        }
    }
}

@Composable
private fun MikuKeypad(onDigit: (String) -> Unit, onDelete: () -> Unit) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "DEL")
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { key ->
                    when (key) {
                        ""    -> Spacer(Modifier.size(72.dp))
                        "DEL" -> KeypadButton(icon = true, onClick = onDelete)
                        else  -> KeypadButton(label = key, onClick = { onDigit(key) })
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(label: String = "", icon: Boolean = false, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = tween(80), label = "keyScale"
    )
    Surface(
        onClick = { pressed = true; onClick(); pressed = false },
        modifier = Modifier.size(72.dp).scale(scale),
        shape = CircleShape,
        color = MikuNavyCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, MikuTeal.copy(alpha = 0.4f)),
        tonalElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (icon) {
                Icon(Icons.Default.Backspace, contentDescription = "Borrar", tint = MikuPink, modifier = Modifier.size(22.dp))
            } else {
                Text(label, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = MikuWhite)
            }
        }
    }
}
