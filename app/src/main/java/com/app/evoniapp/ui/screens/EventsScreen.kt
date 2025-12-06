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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.evoniapp.R
import com.app.evoniapp.data.Event
import com.app.evoniapp.navigation.Route
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    val context = LocalContext.current

    // DisposableEffect es la forma correcta de manejar listeners de Firebase
    DisposableEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUserUid = auth.currentUser?.uid

        if (currentUserUid == null) {
            // Si el usuario no está autenticado, no hacer nada.
            onDispose { } // Se requiere un bloque onDispose
        } else {
            val dbRef = FirebaseDatabase.getInstance().getReference("events")
            val eventsQuery = dbRef.orderByChild("ownerUid").equalTo(currentUserUid)

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val eventsList = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                    events = eventsList
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al cargar eventos: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }

            eventsQuery.addValueEventListener(listener)

            // Este bloque se ejecuta automáticamente cuando el usuario abandona la pantalla
            onDispose {
                eventsQuery.removeEventListener(listener)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.events), fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back)) } },
                actions = { IconButton(onClick = { navController.navigate(Route.AddEvent.route) }) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_event)) } }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(events) { event ->
                EventItem(
                    event = event,
                    modifier = Modifier.clickable { 
                        // Navega con el ID de Firebase (String)
                        navController.navigate(Route.EventDetail.route.replace("{eventId}", event.id))
                    }
                )
            }
        }
    }
}

@Composable
fun EventItem(event: Event, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.eventName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.date), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(event.eventDate, fontSize = 14.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(navController: NavController) {
    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_event_title), fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back)) } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = eventName, onValueChange = { eventName = it }, label = { Text(stringResource(R.string.event_name)) }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = eventDate, onValueChange = { eventDate = it }, label = { Text(stringResource(R.string.event_date)) }, placeholder = { Text(stringResource(R.string.date_format_placeholder)) }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                Button(onClick = { 
                    val auth = FirebaseAuth.getInstance()
                    val ownerUid = auth.currentUser?.uid
                    if (ownerUid == null) {
                        Toast.makeText(context, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val dbRef = FirebaseDatabase.getInstance().getReference("events")
                    val eventId = dbRef.push().key
                     if (eventId == null) {
                        Toast.makeText(context, "Error al crear el evento", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val newEvent = Event(
                        id = eventId,
                        ownerUid = ownerUid,
                        eventName = eventName,
                        eventDate = eventDate
                    )

                    dbRef.child(eventId).setValue(newEvent).addOnCompleteListener {
                        if(it.isSuccessful) {
                             navController.popBackStack()
                        } else {
                            Toast.makeText(context, "No se pudo guardar el evento", Toast.LENGTH_SHORT).show()
                        }
                    }
                }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

// --- Funciones de Countdown ---
fun parseEventDate(date: String): Long {
    return try {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        format.parse(date)?.time ?: 0
    } catch (e: Exception) {
        0
    }
}

@Composable
fun CountdownTimer(eventTime: Long) {
    var remainingTime by remember { mutableStateOf("") }

    LaunchedEffect(eventTime) {
        while (true) {
            val currentTime = System.currentTimeMillis()
            val diff = eventTime - currentTime

            if (diff <= 0) {
                remainingTime = "El evento ha comenzado!"
                break
            }

            val days = diff / (1000 * 60 * 60 * 24)
            val hours = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
            val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)
            val seconds = (diff % (1000 * 60)) / 1000

            remainingTime = "$days d, $hours h, $minutes m, $seconds s"
            delay(1000)
        }
    }

    Text(remainingTime, style = MaterialTheme.typography.headlineSmall)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(navController: NavController, eventId: String?) {
    var event by remember { mutableStateOf<Event?>(null) }
    val context = LocalContext.current

    LaunchedEffect(eventId) {
        if (eventId == null) return@LaunchedEffect
        val dbRef = FirebaseDatabase.getInstance().getReference("events").child(eventId)
        
        val listener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                event = snapshot.getValue(Event::class.java)
            }
            override fun onCancelled(error: DatabaseError) {
                 Toast.makeText(context, "Error al cargar el evento", Toast.LENGTH_SHORT).show()
            }
        }

        dbRef.addListenerForSingleValueEvent(listener)
    }

    if (event == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    
    val eventTime = parseEventDate(event!!.eventDate)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event!!.eventName) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") } },
                actions = {
                    IconButton(onClick = { 
                        navController.navigate(Route.EditEvent.route.replace("{eventId}", event!!.id))
                    }) { 
                        Icon(Icons.Default.Edit, contentDescription = "Editar Evento") 
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = event!!.eventName, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = event!!.eventDate)
            Spacer(modifier = Modifier.height(32.dp))
            if (eventTime > 0) {
                CountdownTimer(eventTime = eventTime)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(navController: NavController, eventId: String?) {
     var event by remember { mutableStateOf<Event?>(null) }
    val context = LocalContext.current

    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }

    LaunchedEffect(eventId) {
        if (eventId == null) return@LaunchedEffect
        val dbRef = FirebaseDatabase.getInstance().getReference("events").child(eventId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val loadedEvent = snapshot.getValue(Event::class.java)
                if (loadedEvent != null) {
                    event = loadedEvent
                    eventName = loadedEvent.eventName
                    eventDate = loadedEvent.eventDate
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    if (event == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Evento") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = eventName, onValueChange = { eventName = it }, label = { Text("Nombre del evento") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = eventDate, onValueChange = { eventDate = it }, label = { Text("Fecha del evento") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { 
                if (eventId == null) return@Button

                val updatedEvent = event!!.copy(
                    eventName = eventName,
                    eventDate = eventDate
                )

                FirebaseDatabase.getInstance().getReference("events").child(eventId).setValue(updatedEvent).addOnCompleteListener {
                    if(it.isSuccessful) {
                         navController.popBackStack(Route.Events.route, false)
                    } else {
                         Toast.makeText(context, "No se pudo guardar", Toast.LENGTH_SHORT).show()
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar Cambios")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventsScreenPreview() {
    // Esta preview ahora estará vacía ya que depende de datos de Firebase
    EventsScreen(rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun AddEventScreenPreview() {
    AddEventScreen(rememberNavController())
}
