package com.app.evoniapp.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000) // Espera 2 segundos
        onFinished()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("EVONI", style = MaterialTheme.typography.headlineLarge)
    }
}


@Composable
fun LoginScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo o número de celular") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar/Ocultar contraseña")
                }
            }
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Iniciar sesión")
        }
        TextButton(onClick = onRegister) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}

@Composable
fun RegisterScreen(onRegistered: () -> Unit, onNavigateBackToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    val isPasswordValid = pass.length >= 8
    val doPasswordsMatch = pass == pass2
    val canRegister = isPasswordValid && doPasswordsMatch && email.isNotBlank() && name.isNotBlank() && username.isNotBlank()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(email, { email = it }, label = { Text("Correo o número de celular") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(pass, { pass = it }, label = { Text("Contraseña (mínimo 8 caracteres)") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, isError = !isPasswordValid)
        OutlinedTextField(pass2, { pass2 = it }, label = { Text("Vuelve a introducir la contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, isError = !doPasswordsMatch)
        OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(username, { username = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRegistered,
            enabled = canRegister,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Crear cuenta") }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onNavigateBackToLogin) {
            Text("Ya tengo cuenta, Iniciar sesión")
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onOpenTasks: () -> Unit,
    onOpenGuests: () -> Unit, // This parameter is kept in case it's used elsewhere
    onOpenBudget: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenProfile: () -> Unit,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    val targetDateTime = remember { LocalDateTime.now().plusDays(58).plusHours(21).plusMinutes(30) }
    var remaining by remember { mutableStateOf(Duration.ZERO) }

    LaunchedEffect(targetDateTime) {
        while (true) {
            val now = LocalDateTime.now()
            remaining = if (now.isBefore(targetDateTime)) Duration.between(now, targetDateTime) else Duration.ZERO
            delay(1000)
        }
    }

    val totalSeconds = remaining.seconds
    val days = totalSeconds / (24 * 3600)
    val hours = (totalSeconds % (24 * 3600)) / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Inicio", style = MaterialTheme.typography.headlineMedium)
            Box {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Cuenta") },
                        onClick = {
                            onOpenAccount()
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Modo Oscuro") },
                        onClick = {
                            onToggleTheme()
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Ayuda") },
                        onClick = {
                            // TODO: Handle help
                            showMenu = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Faltan: $days d : $hours h : $minutes m : $seconds s")
        Spacer(Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Tareas Button
                Button(
                    onClick = onOpenTasks,
                    modifier = Modifier.weight(1f).height(120.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Check, contentDescription = "Tareas", modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Tareas")
                    }
                }
                // Invitados Button
                Button(
                    onClick = {
                        context.startActivity(Intent(context, GuestListActivity::class.java))
                    },
                    modifier = Modifier.weight(1f).height(120.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.People, contentDescription = "Invitados", modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Invitados")
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Presupuesto Button
                Button(onClick = onOpenBudget, modifier = Modifier.weight(1f).height(120.dp), shape = RoundedCornerShape(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AttachMoney, contentDescription = "Presupuesto", modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Presupuesto")
                    }
                }
                // Evento Button
                Button(onClick = onOpenEvents, modifier = Modifier.weight(1f).height(120.dp), shape = RoundedCornerShape(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "Eventos", modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Eventos")
                    }
                }
            }
        }
    }
}
