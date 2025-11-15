package com.app.evoniapp.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.evoniapp.ui.theme.EVONIAPPTheme // Asegúrate que el nombre del tema sea el correcto

private val editLightPurple = Color(0xFFBCA8D6)
private val editDarkPurple = Color(0xFF6750A4)
private val editLightGreen = Color(0xFFC4D1B5)
private val editLightBrown = Color(0xFFD4C3BC)
private val editLightGray = Color(0xFFF3EDF7)
private val editPink = Color(0xFFE6AACE)
private val editTextColor = Color(0xFF49454F)
private val editInactiveChip = Color(0xFFE8DEF8)
private val editInactiveText = Color(0xFF1D1B20)

class EditGuestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EVONIAPPTheme { // Usa el tema de tu app
                EditGuestScreen(onBackPressed = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGuestScreen(onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles:", fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = editPink) }
                },
                actions = {
                    Button(onClick = { /* Lógica de Guardar */ }, colors = ButtonDefaults.buttonColors(containerColor = editLightPurple), shape = RoundedCornerShape(50)) {
                        Icon(Icons.Default.Check, "Guardar")
                        Spacer(Modifier.width(4.dp))
                        Text("Guardar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { EditBottomNavigationBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EditFormContent()
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun EditFormContent() {
    var nombre by remember { mutableStateOf("Pedro Julian Perez") }
    var nota by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("4491234567") }

    Text("Nombre", fontWeight = FontWeight.Medium)
    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = editLightGray, unfocusedBorderColor = Color.Transparent))
    Text("Nota", fontWeight = FontWeight.Medium)
    OutlinedTextField(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = editLightGray, unfocusedBorderColor = Color.Transparent))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        EditToggleButton(text = "Adulto", selected = true)
        EditToggleButton(text = "Niño", selected = false)
        EditToggleButton(text = "Bebé", selected = false)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        EditToggleButton(text = "Hombre", selected = true, color = editLightBrown)
        EditToggleButton(text = "Mujer", selected = false, color = editLightBrown)
    }
    EditDropdownField(label = "Mesa:", initialText = "Mesa Cuadrada (0-10)")
    EditDropdownField(label = "Grupo:", initialText = "Familia")
    EditDropdownField(label = "Menú:", initialText = "Adulto")
    Text("Contacto:", fontWeight = FontWeight.Medium)
    OutlinedTextField(value = telefono, label = { Text("Telefono") }, onValueChange = { telefono = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = editLightGray, unfocusedBorderColor = Color.Transparent))
    EditSelectionGroup(label = "Invitación:", options = listOf("Enviada", "No Enviada"), selected = "Enviada")
    EditSelectionGroup(label = "Confirmación:", options = listOf("Confirmada", "En espera", "Rechazada"), selected = "En espera")
}

@Composable
fun EditToggleButton(text: String, selected: Boolean, color: Color = editLightGreen) {
    Button(onClick = { /* Lógica de selección */ }, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = if (selected) color else editInactiveChip), elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)) {
        Text(text, color = if (selected) Color.Black else editInactiveText)
    }
}

@Composable
fun EditSelectionGroup(label: String, options: List<String>, selected: String) {
    Text(label, fontWeight = FontWeight.Medium)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            Button(onClick = { /* Lógica de selección */ }, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = if (option == selected) editDarkPurple else editInactiveChip), elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)) {
                Text(option, color = if (option == selected) Color.White else editInactiveText)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDropdownField(label: String, initialText: String) {
    Text(label, fontWeight = FontWeight.Medium)
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = editLightGray), onClick = { /* Lógica para mostrar menú desplegable */ }) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(initialText, color = editTextColor, fontWeight = FontWeight.Medium)
            Icon(Icons.Filled.ExpandMore, "Desplegar", tint = editTextColor)
        }
    }
}

@Composable
fun EditBottomNavigationBar() {
    var selectedItem by remember { mutableStateOf("Eventos") }
    val items = listOf("Inicio", "Eventos", "Perfil")
    val icons = listOf(Icons.Default.Home, Icons.Default.CalendarToday, Icons.Default.Person)

    NavigationBar(containerColor = Color.White) {
        items.forEachIndexed { index, screen ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = screen) },
                label = { Text(screen) },
                selected = selectedItem == screen,
                onClick = { selectedItem = screen },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = editPink,
                    selectedTextColor = editPink,
                    indicatorColor = if (selectedItem == "Eventos") Color(0xFFFDEBEE) else Color.Transparent,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:shape=Normal,width=360,height=800,unit=dp,dpi=480")
@Composable
fun EditGuestScreenPreview() {
    EVONIAPPTheme {
        EditGuestScreen(onBackPressed = {})
    }
}
