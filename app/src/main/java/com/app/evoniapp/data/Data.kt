package com.app.evoniapp.data

// --- Clases de Datos para Firebase ---

// Es importante que las clases que se usarán con Firebase tengan valores por defecto
// para sus propiedades, para que el SDK pueda crearlas a partir del JSON.

data class UserData(
    val name: String = "",
    val username: String = "",
    val email: String = ""
)

data class Event(
    val id: String = "",
    val ownerUid: String = "",
    val eventName: String = "",
    val eventDate: String = ""
)

data class Task(
    val id: String = "",
    val ownerUid: String = "",
    val eventId: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false
)

data class Guest(
    val id: String = "",
    val ownerUid: String = "",
    val eventId: String = "",
    val guestName: String = "",
    val status: String = "", // E.g., "Confirmado", "Pendiente", "Rechazado"
    val notes: String = ""
)

data class Budget(
    val id: String = "",
    val ownerUid: String = "",
    val eventId: String = "",
    val eventName: String = "",
    val totalBudget: Double = 0.0,
    val note: String = ""
)

// Renombrado de ExpenseItem para mayor claridad
data class BudgetItem(
    val id: String = "",
    val ownerUid: String = "",
    val budgetId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val cost: Double = 0.0,
    val status: String = "" // E.g., "Pagado", "Pendiente"
)
