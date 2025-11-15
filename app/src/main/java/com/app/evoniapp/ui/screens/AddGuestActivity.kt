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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
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
import com.app.evoniapp.ui.theme.EVONIAPPTheme // Asegúrate que el nombre del tema sea el correcto

private val lightPurple = Color(0xFFC8B6E2)
private val onLightPurple = Color(0xFF332D41)
private val lightGray = Color(0xFFE7E0EC)
private val darkGray = Color(0xFF79747E)
private val brown = Color(0xFFA98D80)
private val green = Color(0xFF9CAF88)

class AddGuestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EVONIAPPTheme { // Usa el tema de tu app
                AddGuestScreen(onBackPressed = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGuestScreen(onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Invitado", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color(0xFFE6AACE))
                        Text("Back", color = Color(0xFFE6AACE))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                GuestFormContent()
            }
            ActionButtons()
        }
    }
}

@Composable
fun GuestFormContent() {
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Nombre", fontWeight = FontWeight.Medium)
        OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("Agregar nombre", color = darkGray) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = lightGray, focusedContainerColor = lightGray, unfocusedBorderColor = Color.Transparent, focusedBorderColor = lightPurple))
        Text("Nota", fontWeight = FontWeight.Medium)
        OutlinedTextField(value = note, onValueChange = { note = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = lightGray, focusedContainerColor = lightGray, unfocusedBorderColor = Color.Transparent, focusedBorderColor = lightPurple))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToggleButton(text = "Adulto", color = green)
            ToggleButton(text = "Niño", color = green)
            ToggleButton(text = "Bebé", color = green)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToggleButton(text = "Hombre", color = brown)
            ToggleButton(text = "Mujer", color = brown)
        }
        AddDropdownField(label = "Mesa:", initialText = "Mesa no indicada")
        AddDropdownField(label = "Grupo:", initialText = "Grupo no indicado")
        AddDropdownField(label = "Menú:", initialText = "No indicado")
        Text("Contacto:", fontWeight = FontWeight.Medium)
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefono") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = lightGray, focusedContainerColor = lightGray, unfocusedBorderColor = Color.Transparent, focusedBorderColor = lightPurple))
        AddSelectionGroup(label = "Invitación:", options = listOf("Enviada", "No Enviada"))
        AddSelectionGroup(label = "Confirmación:", options = listOf("Confirmada", "En espera", "Rechazada"))
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ActionButtons() {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(onClick = { /* Acción para descartar */ }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = lightGray)) {
            Icon(Icons.Default.Close, "Descartar", tint = Color.Black)
            Spacer(Modifier.width(4.dp))
            Text("Descartar", color = Color.Black)
        }
        Button(onClick = { /* Acción para guardar */ }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = lightPurple)) {
            Icon(Icons.Default.Check, "Guardar", tint = onLightPurple)
            Spacer(Modifier.width(4.dp))
            Text("Guardar", color = onLightPurple)
        }
    }
}

@Composable
fun ToggleButton(text: String, color: Color) {
    Button(onClick = { /* TODO: Lógica de selección */ }, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = color), elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)) {
        Text(text, color = Color.White)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDropdownField(label: String, initialText: String) {
    Text(label, fontWeight = FontWeight.Medium)
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = lightGray), onClick = { /* TODO: Lógica para mostrar menú desplegable */ }) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(initialText, color = darkGray)
            Icon(Icons.Filled.ExpandMore, "Desplegar", tint = darkGray)
        }
    }
}

@Composable
fun AddSelectionGroup(label: String, options: List<String>) {
    Text(label, fontWeight = FontWeight.Medium)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            Button(onClick = { /* TODO: Lógica de selección */ }, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = lightGray), elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)) {
                Text(option, color = darkGray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddGuestScreenPreview() {
    EVONIAPPTheme {
        AddGuestScreen(onBackPressed = {})
    }
}
