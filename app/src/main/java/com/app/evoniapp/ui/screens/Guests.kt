package com.app.evoniapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.evoniapp.R
import com.app.evoniapp.data.Event
import com.app.evoniapp.data.Guest
import com.app.evoniapp.navigation.Route
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestsScreen(navController: NavController) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var guests by remember { mutableStateOf<List<Guest>>(emptyList()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        FirebaseDatabase.getInstance().getReference("events")
            .orderByChild("ownerUid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    events = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al cargar eventos: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    DisposableEffect(selectedEvent) {
        if (selectedEvent == null) {
            onDispose {}
        } else {
            val dbRef = FirebaseDatabase.getInstance().getReference("guests")
            val query = dbRef.orderByChild("eventId").equalTo(selectedEvent!!.id)

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    guests = snapshot.children.mapNotNull { it.getValue(Guest::class.java) }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al cargar invitados: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
            query.addValueEventListener(listener)

            onDispose {
                query.removeEventListener(listener)
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(
            title = { Text(stringResource(R.string.guests), fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
            actions = { IconButton(onClick = { navController.navigate(Route.AddGuest.route) }) { Icon(Icons.Filled.Add, stringResource(R.string.add_guest)) } }
        ) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp)) {
            ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = it }) {
                OutlinedTextField(
                    value = selectedEvent?.eventName ?: "Selecciona un evento",
                    onValueChange = {}, 
                    readOnly = true,
                    label = { Text("Evento") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                    events.forEach { event ->
                        DropdownMenuItem(text = { Text(event.eventName) }, onClick = { 
                            selectedEvent = event 
                            isDropdownExpanded = false
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(items = guests, key = { it.id }) { guest ->
                    GuestItem(guest = guest, onClick = { navController.navigate(Route.EditGuest.route.replace("{guestId}", guest.id)) })
                }
            }
        }
    }
}

@Composable
private fun GuestItem(guest: Guest, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(guest.guestName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Text("Estado: ${guest.status}")
            Text("Nota: ${guest.notes}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGuestScreen(navController: NavController) {
    var guestName by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        FirebaseDatabase.getInstance().getReference("events")
            .orderByChild("ownerUid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) { events = snapshot.children.mapNotNull { it.getValue(Event::class.java) } }
                override fun onCancelled(error: DatabaseError) { Toast.makeText(context, "Error al cargar eventos: ${error.message}", Toast.LENGTH_SHORT).show() }
            })
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.add_guest)) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = it }) {
                OutlinedTextField(value = selectedEvent?.eventName ?: "", onValueChange = {}, readOnly = true, label = { Text("Evento") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                    events.forEach { event ->
                        DropdownMenuItem(text = { Text(event.eventName) }, onClick = { selectedEvent = event; isDropdownExpanded = false })
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = guestName, onValueChange = { guestName = it }, label = { Text("Nombre del invitado") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Estado (Confirmado, Pendiente...)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                val ownerUid = FirebaseAuth.getInstance().currentUser?.uid
                if (ownerUid == null || selectedEvent == null) {
                    Toast.makeText(context, "Selecciona un evento", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val dbRef = FirebaseDatabase.getInstance().getReference("guests")
                val guestId = dbRef.push().key ?: return@Button
                val newGuest = Guest(guestId, ownerUid, selectedEvent!!.id, guestName, status, notes)
                
                dbRef.child(guestId).setValue(newGuest).addOnCompleteListener {
                    if (it.isSuccessful) navController.popBackStack()
                    else Toast.makeText(context, "Error al guardar invitado", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGuestScreen(navController: NavController, guestId: String?) {
    var guest by remember { mutableStateOf<Guest?>(null) }
    var guestName by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(guestId) {
        if (guestId == null) return@LaunchedEffect
        val dbRef = FirebaseDatabase.getInstance().getReference("guests").child(guestId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val loadedGuest = snapshot.getValue(Guest::class.java)
                if (loadedGuest != null) {
                    guest = loadedGuest
                    guestName = loadedGuest.guestName
                    status = loadedGuest.status
                    notes = loadedGuest.notes
                }
            }
            override fun onCancelled(error: DatabaseError) { Toast.makeText(context, "Error al cargar invitado: ${error.message}", Toast.LENGTH_SHORT).show() }
        })
    }

    if (guest == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Editar Invitado") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            OutlinedTextField(value = guestName, onValueChange = { guestName = it }, label = { Text("Nombre del invitado") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Estado") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                if (guestId == null) return@Button
                val updatedGuest = guest!!.copy(guestName = guestName, status = status, notes = notes)
                FirebaseDatabase.getInstance().getReference("guests").child(guestId).setValue(updatedGuest).addOnCompleteListener {
                    if (it.isSuccessful) navController.popBackStack()
                    else Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar Cambios")
            }
        }
    }
}
