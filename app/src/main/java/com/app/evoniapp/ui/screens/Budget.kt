package com.app.evoniapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.evoniapp.R
import com.app.evoniapp.data.Budget
import com.app.evoniapp.data.BudgetItem
import com.app.evoniapp.data.Event
import com.app.evoniapp.navigation.Route
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(navController: NavController) {
    var budgets by remember { mutableStateOf<List<Budget>>(emptyList()) }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        
        if (uid == null) {
            onDispose { }
        } else {
            val dbRef = FirebaseDatabase.getInstance().getReference("budgets").orderByChild("ownerUid").equalTo(uid)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    budgets = snapshot.children.mapNotNull { it.getValue(Budget::class.java) }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al cargar presupuestos", Toast.LENGTH_SHORT).show()
                }
            }
            dbRef.addValueEventListener(listener)

            onDispose {
                dbRef.removeEventListener(listener)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.budget), fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Atrás") } },
                actions = { IconButton(onClick = { navController.navigate(Route.AddBudget.route) }) { Icon(Icons.Default.Add, "Añadir Presupuesto") } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(budgets) { budget ->
                Card(modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Route.BudgetDetail.route.replace("{budgetId}", budget.id)) }) {
                    Column(Modifier.padding(16.dp)) {
                        Text(budget.eventName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Presupuesto Total: ${budget.totalBudget}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailScreen(navController: NavController, budgetId: String?) {
    var budget by remember { mutableStateOf<Budget?>(null) }
    var budgetItems by remember { mutableStateOf<List<BudgetItem>>(emptyList()) }
    val totalSpent = remember(budgetItems) { budgetItems.sumOf { it.cost } }
    val context = LocalContext.current

    LaunchedEffect(budgetId) {
        if (budgetId == null) return@LaunchedEffect
        FirebaseDatabase.getInstance().getReference("budgets").child(budgetId).get().addOnSuccessListener {
            budget = it.getValue(Budget::class.java)
        }
    }

    DisposableEffect(budgetId) {
        if (budgetId == null) {
            onDispose { }
        } else {
            val itemsRef = FirebaseDatabase.getInstance().getReference("budgetItems").orderByChild("budgetId").equalTo(budgetId)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    budgetItems = snapshot.children.mapNotNull { it.getValue(BudgetItem::class.java) }
                }
                override fun onCancelled(error: DatabaseError) { Toast.makeText(context, "Error al cargar gastos", Toast.LENGTH_SHORT).show() }
            }
            itemsRef.addValueEventListener(listener)

            onDispose {
                itemsRef.removeEventListener(listener)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(budget?.eventName ?: "Detalle") }, 
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = { 
                         if (budgetId != null) {
                            navController.navigate(Route.EditBudget.route.replace("{budgetId}", budgetId))
                         }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Presupuesto")
                    }
                }
            )
        }
    ) { padding ->
        if (budget == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Presupuesto Total: ${budget?.totalBudget}", style = MaterialTheme.typography.headlineSmall)
            Text("Gastado: $totalSpent", style = MaterialTheme.typography.bodyLarge)
            Text("Restante: ${budget!!.totalBudget - totalSpent}", style = MaterialTheme.typography.bodyLarge)
            Text("Nota: ${budget?.note}")
            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Gastos", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { 
                    navController.navigate(Route.AddBudgetItem.route.replace("{budgetId}", budgetId!!))
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Gasto")
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(budgetItems) { item ->
                    Card(Modifier.fillMaxWidth().clickable { 
                        navController.navigate(Route.EditBudgetItem.route.replace("{budgetItemId}", item.id))
                    }) {
                        Column(Modifier.padding(16.dp)) {
                            Text(item.title, fontWeight = FontWeight.Bold)
                            Text("Costo: ${item.cost}")
                            Text("Categoría: ${item.category}")
                            Text("Estado: ${item.status}")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(navController: NavController) {
    var totalBudget by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        FirebaseDatabase.getInstance().getReference("events").orderByChild("ownerUid").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) { events = snapshot.children.mapNotNull { it.getValue(Event::class.java) } }
                override fun onCancelled(error: DatabaseError) { Toast.makeText(context, "Error al cargar eventos", Toast.LENGTH_SHORT).show() }
            })
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Añadir Presupuesto") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }) }) {
        Column(Modifier.padding(it).padding(16.dp).verticalScroll(rememberScrollState())) {
            ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }) {
                OutlinedTextField(value = selectedEvent?.eventName ?: "", onValueChange = {}, readOnly = true, label = { Text("Evento") }, modifier = Modifier.fillMaxWidth().menuAnchor())
                ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                    events.forEach { event ->
                        DropdownMenuItem(text = { Text(event.eventName) }, onClick = { selectedEvent = event; isDropdownExpanded = false })
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = totalBudget, onValueChange = { totalBudget = it }, label = { Text("Presupuesto Total") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Nota") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                val ownerUid = FirebaseAuth.getInstance().currentUser?.uid
                if (ownerUid == null || selectedEvent == null) {
                    Toast.makeText(context, "Selecciona un evento", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val dbRef = FirebaseDatabase.getInstance().getReference("budgets")
                val budgetId = dbRef.push().key ?: return@Button
                val newBudget = Budget(budgetId, ownerUid, selectedEvent!!.id, selectedEvent!!.eventName, totalBudget.toDoubleOrNull() ?: 0.0, note)

                dbRef.child(budgetId).setValue(newBudget).addOnCompleteListener {
                    if (it.isSuccessful) navController.popBackStack()
                    else Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar Presupuesto")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetScreen(navController: NavController, budgetId: String?) {
    var budget by remember { mutableStateOf<Budget?>(null) }
    var totalBudget by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(budgetId) {
        if (budgetId == null) return@LaunchedEffect
        FirebaseDatabase.getInstance().getReference("budgets").child(budgetId).get().addOnSuccessListener {
            val b = it.getValue(Budget::class.java)
            if (b != null) {
                budget = b
                totalBudget = b.totalBudget.toString()
                note = b.note
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Editar Presupuesto") }, navigationIcon = { IconButton(onClick = { navController.popBackStack()}) { Icon(Icons.Default.ArrowBack, null) } }) }) {
         if (budget == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        Column(Modifier.padding(it).padding(16.dp).verticalScroll(rememberScrollState())) {
            OutlinedTextField(value = totalBudget, onValueChange = { totalBudget = it }, label = { Text("Presupuesto Total") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Nota") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                 if (budgetId == null) return@Button
                 val updatedBudget = budget!!.copy(totalBudget = totalBudget.toDoubleOrNull() ?: 0.0, note = note)
                 FirebaseDatabase.getInstance().getReference("budgets").child(budgetId).setValue(updatedBudget).addOnCompleteListener {
                     if(it.isSuccessful) navController.popBackStack()
                     else Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                 }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar Cambios")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetItemScreen(navController: NavController, budgetId: String?) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text("Añadir Gasto") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }) }) {
        Column(Modifier.padding(it).padding(16.dp).verticalScroll(rememberScrollState())) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título del Gasto") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Costo") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Estado (Pagado, Pendiente...)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                val ownerUid = FirebaseAuth.getInstance().currentUser?.uid
                if (budgetId == null || ownerUid == null) {
                     Toast.makeText(context, "Error: no se pudo asociar el gasto.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val dbRef = FirebaseDatabase.getInstance().getReference("budgetItems")
                val budgetItemId = dbRef.push().key ?: return@Button
                val newBudgetItem = BudgetItem(budgetItemId, ownerUid, budgetId, title, description, category, cost.toDoubleOrNull() ?: 0.0, status)

                dbRef.child(budgetItemId).setValue(newBudgetItem).addOnCompleteListener {
                    if (it.isSuccessful) navController.popBackStack()
                    else Toast.makeText(context, "Error al guardar el gasto", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar Gasto")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetItemScreen(navController: NavController, budgetItemId: String?) {
    var item by remember { mutableStateOf<BudgetItem?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(budgetItemId) {
        if (budgetItemId == null) return@LaunchedEffect
        FirebaseDatabase.getInstance().getReference("budgetItems").child(budgetItemId).get().addOnSuccessListener {
            val loadedItem = it.getValue(BudgetItem::class.java)
            if (loadedItem != null) {
                item = loadedItem
                title = loadedItem.title
                description = loadedItem.description
                category = loadedItem.category
                cost = loadedItem.cost.toString()
                status = loadedItem.status
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Editar Gasto") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }) }) {
        if (item == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        Column(Modifier.padding(it).padding(16.dp).verticalScroll(rememberScrollState())) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título del Gasto") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Costo") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Estado") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                if (budgetItemId == null) return@Button
                val updatedItem = item!!.copy(title=title, description=description, category=category, cost=cost.toDoubleOrNull() ?: 0.0, status=status)

                FirebaseDatabase.getInstance().getReference("budgetItems").child(budgetItemId).setValue(updatedItem).addOnCompleteListener {
                    if(it.isSuccessful) navController.popBackStack()
                    else Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar Cambios")
            }
        }
    }
}
