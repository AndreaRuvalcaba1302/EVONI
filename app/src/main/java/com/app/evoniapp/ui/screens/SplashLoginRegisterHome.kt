package com.app.evoniapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.evoniapp.R
import com.app.evoniapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Texto "EVONI" modificado
            Text(
                text = "EVONI",
                fontSize = 48.sp, // Aumentamos el tamaño para mayor impacto
                fontWeight = FontWeight.Bold // Lo ponemos en negrita
            )
            Spacer(modifier = Modifier.height(8.dp)) // Espacio entre los textos
            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                letterSpacing = 1.5.sp // Añadimos un poco de espacio entre letras como en la imagen
            )
        }
    }
}

@Composable
fun LoginScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start // Alineación a la izquierda para las etiquetas
    ) {
        Text(
            stringResource(R.string.welcome),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(48.dp))

        // Etiqueta para el correo
        Text(stringResource(R.string.email_or_phone), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(Modifier.height(4.dp))

        // Campo de texto para el correo
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text(stringResource(R.string.email_placeholder)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedContainerColor = Color(0xFFF0F0F0)
            )
        )
        Spacer(Modifier.height(16.dp))

        // Etiqueta para la contraseña
        Text(stringResource(R.string.password), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(Modifier.height(4.dp))

        // Campo de texto para la contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedContainerColor = Color(0xFFF0F0F0)
            ),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = stringResource(R.string.toggle_password_visibility))
                }
            }
        )
        Spacer(Modifier.height(32.dp))

        // Botón de Iniciar Sesión
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onLogin()
                            } else {
                                val error = task.exception?.message ?: "Error de autenticación."
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Por favor, rellena todos los campos.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50), // Bordes completamente redondeados
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
        ) {
            Text(stringResource(R.string.login), color = Color.White)
        }
        Spacer(Modifier.height(16.dp))

        // Botón de Registrarse
        Button(
            onClick = onRegister,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50), // Bordes completamente redondeados
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0D7FF))
        ) {
            Text(stringResource(R.string.no_account_register), color = Color(0xFF007AFF))
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
    var passVisible by remember { mutableStateOf(false) }
    var pass2Visible by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    val isPasswordLongEnough = pass.length >= 8
    val doPasswordsMatch = pass == pass2
    val canRegister = isPasswordLongEnough && doPasswordsMatch && email.isNotBlank() && name.isNotBlank() && username.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.register),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(48.dp))

        AuthTextField(label = stringResource(R.string.email_or_phone), value = email, onValueChange = { email = it }, placeholder = stringResource(R.string.email_placeholder))
        Spacer(Modifier.height(16.dp))
        AuthTextField(label = stringResource(R.string.password), value = pass, onValueChange = { pass = it }, placeholder = stringResource(R.string.password_placeholder), isPassword = true, passwordVisible = passVisible, onPasswordToggle = { passVisible = !passVisible })
        Spacer(Modifier.height(16.dp))
        AuthTextField(label = stringResource(R.string.confirm_password), value = pass2, onValueChange = { pass2 = it }, placeholder = stringResource(R.string.confirm_password), isPassword = true, passwordVisible = pass2Visible, onPasswordToggle = { pass2Visible = !pass2Visible })

        if (!isPasswordLongEnough && pass.isNotEmpty()) {
            Text("La contraseña debe tener al menos 8 caracteres", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        } else if (!doPasswordsMatch && pass2.isNotEmpty()) {
            Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))
        AuthTextField(label = stringResource(R.string.name), value = name, onValueChange = { name = it })
        Spacer(Modifier.height(16.dp))
        AuthTextField(label = stringResource(R.string.username), value = username, onValueChange = { username = it }, placeholder = stringResource(R.string.username_placeholder))
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if(canRegister) {
                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser = auth.currentUser
                                if (firebaseUser != null) {
                                    val uid = firebaseUser.uid
                                    val userData = UserData(name = name, username = username, email = email)
                                    val database = FirebaseDatabase.getInstance().getReference("users")
                                    database.child(uid).setValue(userData).addOnCompleteListener { dbTask ->
                                        if(dbTask.isSuccessful) {
                                            onRegistered()
                                        } else {
                                            Toast.makeText(context, dbTask.exception?.message ?: "Error al guardar datos.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            } else {
                                val error = task.exception?.message ?: "Error en el registro."
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        }
                }
            },
            enabled = canRegister,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
        ) {
            Text(stringResource(R.string.create_account), color = Color.White)
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onNavigateBackToLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0D7FF))
        ) {
            Text(stringResource(R.string.already_have_account_login), color = Color(0xFF007AFF))
        }
    }
}

/**
 * Un campo de texto reutilizable para las pantallas de autenticación.
 */
@Composable
fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {}
) {
    Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(placeholder) },
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = onPasswordToggle) {
                    Icon(imageVector = image, contentDescription = stringResource(R.string.toggle_visibility))
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            unfocusedContainerColor = Color(0xFFF0F0F0),
            focusedContainerColor = Color(0xFFF0F0F0)
        )
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onOpenTasks: () -> Unit,
    onOpenGuests: () -> Unit,
    onOpenBudget: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenProfile: () -> Unit,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    // Fecha objetivo del temporizador
    val targetDateTime = remember { LocalDateTime.of(2025, 11, 8, 0, 0, 0) }
    var remaining by remember { mutableStateOf(Duration.ZERO) }

    // Formateador para el texto sobre el temporizador
    val formatter = DateTimeFormatter.ofPattern(stringResource(R.string.event_date_pattern), Locale.getDefault())
    val targetDateText = targetDateTime.format(formatter)

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

    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        // --- BARRA SUPERIOR CON TÍTULO Y MENÚ ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.home), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Box {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings),
                        tint = Color(0xFFF0AAB8) // Tinte rosa para el ícono
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.account)) }, onClick = { onOpenAccount(); showMenu = false })
                    DropdownMenuItem(text = { Text(stringResource(R.string.dark_mode)) }, onClick = { onToggleTheme(); showMenu = false })
                    DropdownMenuItem(text = { Text(stringResource(R.string.help)) }, onClick = { /* TODO */ showMenu = false })
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        // --- NUEVO DISEÑO DEL TEMPORIZADOR ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFEADED8) // Color de fondo del contenedor del temporizador
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = targetDateText,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CountdownUnit(value = days.toString(), label = stringResource(R.string.countdown_days))
                    CountdownUnit(value = hours.toString(), label = stringResource(R.string.countdown_hours))
                    CountdownUnit(value = minutes.toString(), label = stringResource(R.string.countdown_minutes))
                    CountdownUnit(value = seconds.toString(), label = stringResource(R.string.countdown_seconds))
                }
            }
        }
        // --- FIN DEL NUEVO DISEÑO DEL TEMPORIZADOR ---

        Spacer(Modifier.height(24.dp))

        // --- BOTONES DE CATEGORÍAS ---
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Tareas Button
                Button(
                    onClick = onOpenTasks,
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA98E78)) // Marrón
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Check, contentDescription = stringResource(R.string.tasks), modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.tasks))
                    }
                }
                // Invitados Button
                Button(
                    onClick = onOpenGuests,
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3A4D8)) // Morado
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.People, contentDescription = stringResource(R.string.guests), modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.guests))
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Presupuesto Button
                Button(
                    onClick = onOpenBudget,
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6B5B5)) // Rosa
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AttachMoney, contentDescription = stringResource(R.string.budget), modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.budget))
                    }
                }
                // Evento Button
                Button(
                    onClick = onOpenEvents,
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90A98E)) // Verde
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = stringResource(R.string.events), modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.events))
                    }
                }
            }
        }
    }
}

/**
 * Un bloque visual para una unidad del temporizador (días, horas, etc.).
 */
@Composable
fun CountdownUnit(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            modifier = Modifier
                .width(64.dp)
                .height(80.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = Color.Black.copy(alpha = 0.8f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = value.padStart(2, '0'),
                    color = Color(0xFFFBC4D0), // Color rosa claro para los números
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
