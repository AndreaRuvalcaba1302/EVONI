package com.app.evoniapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Person


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onDeleteAccount: () -> Unit = {}
) {
    // Estados para los campos de texto
    var name by remember { mutableStateOf("shynkwanchi") }
    var email by remember { mutableStateOf("dunygueynguang412@gmail.com") }
    var phone by remember { mutableStateOf("449 234 0934") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            // Avatar (foto de perfil)
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color.Gray) // Placeholder para la imagen
            )
            Spacer(Modifier.height(24.dp))

            // Botones de Guardar y Borrar
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Botón Guardar
                Button(
                    onClick = onSaved,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
                // Botón Borrar (con color rojo)
                Button(
                    onClick = onDeleteAccount,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("Borrar")
                }
            }

            Spacer(Modifier.height(32.dp))
            Text(
                "Basic Information",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("No. de teléfono") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))
            Text(
                "Borrar Cuenta",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))
            // Botón inferior para Borrar Cuenta
            Button(
                onClick = onDeleteAccount,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text("Borrar cuenta")
            }
        }
    }
}

// ======================================================================
// 2. PANTALLA DE CONFIGURAR CUENTA (ACTUALIZADA CON TU DISEÑO)
// ======================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountConfigScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            // Icono de usuario
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Avatar de usuario",
                modifier = Modifier.size(96.dp),
                tint = Color.Gray
            )
            Spacer(Modifier.height(32.dp))

            // Campos de texto
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Cambiar Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Cambiar Usuario") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Cambiar no. de teléfono") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
            // He añadido un botón de guardar que no estaba en tu diseño, pero es necesario
            Button(
                onClick = onSaved,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cambios")
            }
        }
    }
}


// ======================================================================
// EL RESTO DEL ARCHIVO (se mantiene igual)
// ======================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenAccount: () -> Unit
) {
    val fakeEvents = remember {
        listOf(
            Triple("Boda de x", "20/12/2025", "20:00"),
            Triple("Graduación U", "10/01/2026", "18:00"),
            Triple("Cumpleaños de x", "05/03/2026", "17:30"),
        )
    }
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Perfil") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                }
            }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar()
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Nombre Apellido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("nombre@gmail.com", color = Color.Gray)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = onEdit, label = { Text("Editar Perfil") })
                    AssistChip(onClick = onOpenAccount, label = { Text("Cuenta") })
                }
                Spacer(Modifier.height(16.dp))
                Text("Próximos eventos", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
            }
            items(fakeEvents) { (t, d, h) ->
                EventCardPreview(title = t, date = d, hour = h)
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}


@Composable
private fun Avatar(modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(Color(0xFFEDE7F6)),
        contentAlignment = Alignment.Center
    ) {
        Text("EV", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5E35B1))
    }
}

@Composable
private fun EventCardPreview(title: String, date: String, hour: String, onOpen: () -> Unit = {}) {
    ElevatedCard(onClick = onOpen, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFFF3E5F5)))
            Column(Modifier.padding(12.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text("$date  •  $hour", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}

@Composable
fun HomeHeaderWithSettings(onOpenAccount: () -> Unit, onToggleDarkMode: () -> Unit, onHelp: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Inicio", style = MaterialTheme.typography.titleLarge)
        Box {
            IconButton(onClick = { showMenu = true }) { Icon(Icons.Filled.MoreVert, "Ajustes") }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text("Cuenta") }, onClick = { showMenu = false; onOpenAccount() })
                DropdownMenuItem(text = { Text("Modo Oscuro") }, onClick = { showMenu = false; onToggleDarkMode() })
                DropdownMenuItem(text = { Text("Ayuda") }, onClick = { showMenu = false; onHelp() })
            }
        }
    }
}
