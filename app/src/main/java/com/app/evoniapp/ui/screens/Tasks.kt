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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.app.evoniapp.data.Task
import com.app.evoniapp.navigation.Route
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(navController: NavController) {
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUserUid = auth.currentUser?.uid

        if (currentUserUid == null) {
            onDispose { }
        } else {
            val dbRef = FirebaseDatabase.getInstance().getReference("tasks")
            val query = dbRef.orderByChild("ownerUid").equalTo(currentUserUid)

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tasks = snapshot.children.mapNotNull { it.getValue(Task::class.java) }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al cargar tareas: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
            query.addValueEventListener(listener)

            onDispose {
                query.removeEventListener(listener)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tasks), fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back)) } },
                actions = { IconButton(onClick = { navController.navigate(Route.AddTask.route) }) { Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_task)) } }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = tasks, key = { it.id }) { task ->
                TaskItem(
                    task = task,
                    modifier = Modifier.clickable { 
                        navController.navigate(Route.EditTask.route.replace("{taskId}", task.id)) 
                    }
                )
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = task.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = task.description, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (task.isCompleted) "Completado" else "Pendiente", 
                fontSize = 14.sp, 
                color = if (task.isCompleted) Color.Green else Color.Red
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }

    // Estado para eventos y selección
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Cargar eventos del usuario
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

    Scaffold(
        topBar = { TopAppBar(title = { Text("Añadir Tarea") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dropdown para seleccionar evento
            ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = it }) {
                OutlinedTextField(
                    value = selectedEvent?.eventName ?: "",
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

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = isCompleted, onCheckedChange = { isCompleted = it })
                Text("Completado")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                val ownerUid = FirebaseAuth.getInstance().currentUser?.uid
                if (ownerUid == null) {
                    Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (selectedEvent == null) {
                    Toast.makeText(context, "Por favor, selecciona un evento", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                
                val dbRef = FirebaseDatabase.getInstance().getReference("tasks")
                val taskId = dbRef.push().key ?: return@Button

                val newTask = Task(
                    id = taskId,
                    ownerUid = ownerUid,
                    eventId = selectedEvent!!.id,
                    title = title,
                    description = description,
                    isCompleted = isCompleted
                )
                
                dbRef.child(taskId).setValue(newTask).addOnCompleteListener {
                    if (it.isSuccessful) navController.popBackStack()
                    else Toast.makeText(context, "Error al guardar la tarea", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(navController: NavController, taskId: String?) {
    var task by remember { mutableStateOf<Task?>(null) }
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        if (taskId == null) return@LaunchedEffect
        val dbRef = FirebaseDatabase.getInstance().getReference("tasks").child(taskId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val loadedTask = snapshot.getValue(Task::class.java)
                if (loadedTask != null) {
                    task = loadedTask
                    title = loadedTask.title
                    description = loadedTask.description
                    isCompleted = loadedTask.isCompleted
                }
            }
            override fun onCancelled(error: DatabaseError) { Toast.makeText(context, "Error al cargar tarea: ${error.message}", Toast.LENGTH_SHORT).show() }
        })
    }

    if (task == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Editar Tarea") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, null) } }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = isCompleted, onCheckedChange = { isCompleted = it })
                Text("Completado")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                if (taskId == null) return@Button
                val updatedTask = task!!.copy(title = title, description = description, isCompleted = isCompleted)
                FirebaseDatabase.getInstance().getReference("tasks").child(taskId).setValue(updatedTask).addOnCompleteListener {
                    if (it.isSuccessful) navController.popBackStack()
                    else Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                Text("Guardar Cambios")
            }
        }
    }
}

@Preview
@Composable
fun TasksScreenPreview() {
    TasksScreen(rememberNavController())
}
