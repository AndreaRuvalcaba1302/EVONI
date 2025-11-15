package com.app.evoniapp.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.evoniapp.ui.theme.EVONIAPPTheme // Asegúrate que el nombre del tema sea el correcto

// --- Definición de colores ---
private val listPurple = Color(0xFFC8B6E2)
private val listPink = Color(0xFFE6AACE)
private val listCardBackground = Color(0xFFF5F5F5)
private val listTextColor = Color(0xFF4A4A4A)
private val listIconColor = Color(0xFF8B8B8B)

class GuestListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EVONIAPPTheme { // Usa el tema de tu app
                GuestListScreen(
                    onBackPressed = { finish() },
                    onAddGuestPressed = {
                        startActivity(Intent(this, AddGuestActivity::class.java))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestListScreen(onBackPressed: () -> Unit, onAddGuestPressed: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invitados Boda", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackPressed) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = listPink)
                        }
                        Text("Back", color = listPink, fontSize = 16.sp)
                    }
                },
                actions = {
                    IconButton(onClick = onAddGuestPressed) {
                        Icon(Icons.Filled.Add, "Agregar Invitado", modifier = Modifier.size(32.dp), tint = listPink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { GuestCard(guestNumber = 1, isFull = true) }
            item { GuestCard(guestNumber = 2, isFull = false) }
        }
    }
}

@Composable
fun GuestCard(guestNumber: Int, isFull: Boolean) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = listCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Evento: Boda.", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = listTextColor)
            Text("Invitado $guestNumber", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = listTextColor)
            Spacer(modifier = Modifier.height(16.dp))
            GuestInfoRow(icon = Icons.Default.Person, text = "Nombre: Pedro Julian Perez")
            GuestInfoRow(text = "Nota: Amigo de la novia.")
            GuestInfoRow(text = "Adulto")
            GuestInfoRow(text = "♂ Sexo: Masculino.")
            if (isFull) {
                GuestInfoRow(text = "Mesa: Redonda.")
                GuestInfoRow(text = "Menu: Adulto")
                GuestInfoRow(text = "Contacto: 449 123 45 67")
                GuestInfoRow(text = "Invitacion: Enviada.")
                GuestInfoRow(icon = Icons.Default.WatchLater, text = "Fecha: 15 Jul 2026, 19:00 hrs")
                GuestInfoRow(text = "Confirmacion: Confirmada")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, EditGuestActivity::class.java))
                },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = listPurple)
            ) {
                Text("Editar", color = Color.White)
            }
        }
    }
}

@Composable
fun GuestInfoRow(icon: ImageVector? = null, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = listIconColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(24.dp))
        }
        Text(text, color = listTextColor, fontSize = 14.sp)
    }
}

@Preview(showBackground = true, device = "spec:shape=Normal,width=360,height=800,unit=dp,dpi=480")
@Composable
fun GuestListScreenPreview() {
    EVONIAPPTheme {
        GuestListScreen(onBackPressed = {}, onAddGuestPressed = {})
    }
}
