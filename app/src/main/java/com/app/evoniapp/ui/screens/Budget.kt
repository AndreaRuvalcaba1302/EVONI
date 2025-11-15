package com.app.evoniapp.ui.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

data class BudgetItemData(
    val eventName: String,
    val budget: String,
    val totalSpent: String,
    val toSpend: String,
    val note: String,
    val details: List<String>
)

val sampleBudgets = listOf(
    BudgetItemData(
        eventName = "Boda.",
        budget = "$ 24, 500",
        totalSpent = "$4, 300",
        toSpend = "$20, 200",
        note = "En caso de sobrar, utilizarlo para pequeños detalles",
        details = listOf("Renta del lugar", "Musica")
    ),
    BudgetItemData(
        eventName = "XV años",
        budget = "$ 15, 000",
        totalSpent = "$7, 300",
        toSpend = "$7, 700",
        note = "En caso de sobrar, utilizarlo para pequeños detalles",
        details = listOf("Renta del lugar", "Musica")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Presupuesto", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("add_budget") }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir presupuesto")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleBudgets) { budget ->
                BudgetItem(item = budget)
            }
        }
    }
}

@Composable
fun BudgetItem(item: BudgetItemData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Evento: ${item.eventName}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Presupuesto: ${item.budget}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Total Gastado: ${item.totalSpent}", fontSize = 14.sp, color = Color.Gray)
            Text("Por Gastar: ${item.toSpend}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nota: ${item.note}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            item.details.forEach {
                Text("→ $it", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var spent by remember { mutableStateOf("") }
    var remaining by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var expenseTitle by remember { mutableStateOf("") }
    var expenseDescription by remember { mutableStateOf("") }
    var expenseCategory by remember { mutableStateOf("") }
    var expenseTotal by remember { mutableStateOf("") }
    var isPaid by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Presupuesto", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back", color = Color.Gray)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = budget, onValueChange = { budget = it }, label = { Text("Presupuesto") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = spent, onValueChange = { spent = it }, label = { Text("Gasto:") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = remaining, onValueChange = { remaining = it }, label = { Text("Falta por pagar") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Nota:") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /*TODO*/ }) {
                Text("Agregar")
            }
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            Text("Agregar Gasto:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = expenseTitle, onValueChange = { expenseTitle = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = expenseDescription, onValueChange = { expenseDescription = it }, label = { Text("Descripcion:") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = expenseCategory, onValueChange = { expenseCategory = it }, label = { Text("Categoria:") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = expenseTotal, onValueChange = { expenseTotal = it }, label = { Text("Total:") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = { isPaid = true }, colors = ButtonDefaults.buttonColors(containerColor = if (isPaid) Color.LightGray else Color.Transparent)) {
                    Text("Pagado")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { isPaid = false }, colors = ButtonDefaults.buttonColors(containerColor = if (!isPaid) MaterialTheme.colorScheme.primary else Color.Transparent)) {
                    Text("Pendiente")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetScreenPreview() {
    BudgetScreen(rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun AddBudgetScreenPreview() {
    AddBudgetScreen(rememberNavController())
}
