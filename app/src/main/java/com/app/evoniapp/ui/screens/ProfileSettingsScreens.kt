package com.app.evoniapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.evoniapp.R
import com.app.evoniapp.data.Event
import com.app.evoniapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// --- Pantallas de Perfil y Configuración ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenAccount: () -> Unit
) {
    var userData by remember { mutableStateOf<UserData?>(null) }
    var userEvents by remember { mutableStateOf<List<Event>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect

        // Cargar datos del usuario
        FirebaseDatabase.getInstance().getReference("users").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userData = snapshot.getValue(UserData::class.java)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al cargar perfil", Toast.LENGTH_SHORT).show()
                }
            })

        // Cargar eventos del usuario
        FirebaseDatabase.getInstance().getReference("events").orderByChild("ownerUid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userEvents = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                }
                override fun onCancelled(error: DatabaseError) { /* Silently fail */ }
            })
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.profile)) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } }
        )
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            item {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(name = userData?.name)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(userData?.name ?: "Cargando...", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(userData?.email ?: "", color = Color.Gray)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = onEdit, label = { Text(stringResource(R.string.edit_profile)) })
                    AssistChip(onClick = onOpenAccount, label = { Text(stringResource(R.string.account)) })
                }
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.upcoming_events), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
            }
            items(userEvents) { event ->
                EventCardPreview(title = event.eventName, date = event.eventDate, hour = "")
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var userData by remember { mutableStateOf<UserData?>(null) }
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect
        FirebaseDatabase.getInstance().getReference("users").child(uid).get().addOnSuccessListener {
            val user = it.getValue(UserData::class.java)
            if (user != null) {
                userData = user
                name = user.name
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.edit_profile)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") } }) }
    ) { padding ->
        if (userData == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        Column(Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = userData?.email ?: "", onValueChange = {}, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                if (uid == null) return@Button
                val updatedData = mapOf("name" to name)
                FirebaseDatabase.getInstance().getReference("users").child(uid).updateChildren(updatedData).addOnCompleteListener {
                    if(it.isSuccessful) onSaved()
                    else Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save_changes))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountConfigScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit = {},
    onLogout: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect
        FirebaseDatabase.getInstance().getReference("users").child(uid).get().addOnSuccessListener {
            val user = it.getValue(UserData::class.java)
            if (user != null) {
                username = user.username
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.configure_account)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") } }) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Nombre de usuario") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                 if (uid == null) return@Button
                val updatedData = mapOf("username" to username) // Asumiendo que quieres guardar el teléfono también
                FirebaseDatabase.getInstance().getReference("users").child(uid).updateChildren(updatedData).addOnCompleteListener {
                    if(it.isSuccessful) onSaved()
                    else Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save_changes))
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Cerrar Sesión")
            }
        }
    }
}

// --- COMPONENTES REUTILIZABLES ---

@Composable
private fun Avatar(modifier: Modifier = Modifier, name: String?) {
    val initials = name?.split(' ')?.mapNotNull { it.firstOrNull()?.uppercase() }?.take(2)?.joinToString("") ?: ""
    Box(
        modifier.size(68.dp).clip(CircleShape).background(Color(0xFFEDE7F6)),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5E35B1))
    }
}

@Composable
private fun EventCardPreview(title: String, date: String, hour: String, onOpen: () -> Unit = {}) {
    ElevatedCard(onClick = onOpen, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().height(120.dp).background(Color.Gray))
            Column(Modifier.padding(12.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text("$date ${stringResource(R.string.date_hour_separator)} $hour", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}
