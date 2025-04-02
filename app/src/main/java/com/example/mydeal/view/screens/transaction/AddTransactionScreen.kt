package com.example.mydeal.view.screens.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydeal.model.data.Transaction
import com.example.mydeal.view.components.CustomButton
import com.example.mydeal.view.components.CustomTextField
import com.example.mydeal.view.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    transactionId: String = "",
    isEditing: Boolean = false
) {
    // Estados para el formulario
    var amount by remember { mutableStateOf(if (isEditing) "1250.00" else "") }
    var description by remember { mutableStateOf(if (isEditing) "Compras del supermercado" else "") }
    var date by remember { mutableStateOf(if (isEditing) "2025-03-26" else SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var category by remember { mutableStateOf(if (isEditing) "Alimentación" else "") }
    var isExpense by remember { mutableStateOf(true) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategorySelector by remember { mutableStateOf(false) }

    // Estados para transacciones programadas
    var isRecurring by remember { mutableStateOf(false) }
    var recurringPeriod by remember { mutableStateOf(Transaction.RecurringPeriod.NONE) }
    var showRecurringOptions by remember { mutableStateOf(false) }
    var recurringEndDate by remember { mutableStateOf<String?>(null) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Para validación
    var amountError by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf("") }

    // Valores para el selector de categorías
    val expenseCategories = listOf(
        "Alimentación" to Icons.Filled.Restaurant,
        "Transporte" to Icons.Filled.DirectionsCar,
        "Entretenimiento" to Icons.Filled.TheaterComedy,
        "Servicios" to Icons.Filled.ReceiptLong,
        "Compras" to Icons.Filled.ShoppingBag,
        "Vivienda" to Icons.Filled.House,
        "Salud" to Icons.Filled.HealthAndSafety,
        "Educación" to Icons.Filled.School,
        "Otros" to Icons.Filled.Category
    )

    val incomeCategories = listOf(
        "Salario" to Icons.Filled.Payments,
        "Inversiones" to Icons.Filled.TrendingUp,
        "Regalos" to Icons.Filled.CardGiftcard,
        "Reembolsos" to Icons.Filled.SyncAlt,
        "Otros" to Icons.Filled.Category
    )

    // Opciones de recurrencia
    val recurringOptions = listOf(
        "No se repite" to Transaction.RecurringPeriod.NONE,
        "Diariamente" to Transaction.RecurringPeriod.DAILY,
        "Semanalmente" to Transaction.RecurringPeriod.WEEKLY,
        "Mensualmente" to Transaction.RecurringPeriod.MONTHLY,
        "Anualmente" to Transaction.RecurringPeriod.YEARLY
    )

    // Funciones de validación
    val validateAmount: () -> Boolean = {
        when {
            amount.isEmpty() -> {
                amountError = "El monto es obligatorio"
                false
            }
            amount.toDoubleOrNull() == null -> {
                amountError = "Monto inválido"
                false
            }
            amount.toDouble() <= 0 -> {
                amountError = "El monto debe ser mayor a cero"
                false
            }
            else -> {
                amountError = ""
                true
            }
        }
    }

    val validateDescription: () -> Boolean = {
        when {
            description.isEmpty() -> {
                descriptionError = "La descripción es obligatoria"
                false
            }
            description.length < 3 -> {
                descriptionError = "La descripción es demasiado corta"
                false
            }
            else -> {
                descriptionError = ""
                true
            }
        }
    }

    // Selección de fecha
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val selectedDate = remember { mutableStateOf(dateFormat.parse(date) ?: Date()) }
    val selectedEndDate = remember { mutableStateOf(recurringEndDate?.let { dateFormat.parse(it) } ?: Date()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Editar Transacción" else "Nueva Transacción",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Selector de tipo de transacción (Gasto/Ingreso)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TransactionTypeButton(
                        text = "Gasto",
                        icon = Icons.Default.ArrowDownward,
                        isSelected = isExpense,
                        onClick = { isExpense = true }
                    )

                    TransactionTypeButton(
                        text = "Ingreso",
                        icon = Icons.Default.ArrowUpward,
                        isSelected = !isExpense,
                        onClick = { isExpense = false }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de monto
            CustomTextField(
                value = amount,
                onValueChange = { amount = it },
                label = "Monto",
                leadingIcon = Icons.Default.AttachMoney,
                isError = amountError.isNotEmpty(),
                errorMessage = amountError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                )
            )

            // Selector de Categoría
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { showCategorySelector = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Categoría",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Text(
                            text = if (category.isEmpty()) "Seleccionar categoría" else category,
                            color = if (category.isEmpty()) Color.Gray else Color.Black
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }

            // Selector de categoría expandido
            AnimatedVisibility(visible = showCategorySelector) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Selecciona una categoría",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Lista de categorías según tipo de transacción
                        val categories = if (isExpense) expenseCategories else incomeCategories

                        categories.forEach { (cat, icon) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        category = cat
                                        showCategorySelector = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (category == cat) MaterialTheme.colorScheme.primary else Color.Gray
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = cat,
                                    color = if (category == cat) MaterialTheme.colorScheme.primary else Color.Black
                                )

                                if (category == cat) {
                                    Spacer(modifier = Modifier.weight(1f))

                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { showCategorySelector = false },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Cerrar")
                        }
                    }
                }
            }

            // Campo de descripción
            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descripción",
                leadingIcon = Icons.Default.Description,
                isError = descriptionError.isNotEmpty(),
                errorMessage = descriptionError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            // Selector de fecha
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { showDatePicker = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fecha",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Text(
                            text = date,
                            color = Color.Black
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }

            // DatePicker Dialog
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        Button(onClick = {
                            date = dateFormat.format(selectedDate.value)
                            showDatePicker = false
                        }) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(
                        state = rememberDatePickerState(
                            initialSelectedDateMillis = selectedDate.value.time
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // NUEVA SECCIÓN: Opciones de recurrencia
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = null,
                            tint = if (isRecurring) MaterialTheme.colorScheme.primary else Color.Gray
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Transacción recurrente",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "Programar esta transacción para que se repita",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Switch(
                            checked = isRecurring,
                            onCheckedChange = {
                                isRecurring = it
                                if (it) {
                                    showRecurringOptions = true
                                } else {
                                    recurringPeriod = Transaction.RecurringPeriod.NONE
                                    recurringEndDate = null
                                }
                            }
                        )
                    }

                    AnimatedVisibility(visible = isRecurring) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text(
                                text = "Opciones de recurrencia",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Selector de período
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showRecurringOptions = true }
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Frecuencia",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )

                                        Text(
                                            text = recurringOptions.find { it.second == recurringPeriod }?.first
                                                ?: "Seleccionar frecuencia"
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }
                            }

                            // Fecha de finalización (opcional)
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showEndDatePicker = true }
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EventBusy,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Fecha de finalización (opcional)",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )

                                        Text(
                                            text = recurringEndDate ?: "Sin fecha de finalización"
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Opciones de recurrencia expandidas
            if (showRecurringOptions) {
                AlertDialog(
                    onDismissRequest = { showRecurringOptions = false },
                    title = { Text("Seleccionar frecuencia") },
                    text = {
                        Column {
                            recurringOptions.forEach { (label, period) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            recurringPeriod = period
                                            showRecurringOptions = false
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = recurringPeriod == period,
                                        onClick = {
                                            recurringPeriod = period
                                            showRecurringOptions = false
                                        }
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(text = label)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showRecurringOptions = false }) {
                            Text("Cerrar")
                        }
                    }
                )
            }

            // DatePicker para fecha de finalización
            if (showEndDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showEndDatePicker = false },
                    confirmButton = {
                        Button(onClick = {
                            recurringEndDate = dateFormat.format(selectedEndDate.value)
                            showEndDatePicker = false
                        }) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showEndDatePicker = false
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(
                        state = rememberDatePickerState(
                            initialSelectedDateMillis = selectedEndDate.value.time
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de foto de comprobante
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable {
                        // En una implementación real, navegaríamos a la pantalla de captura de comprobante
                        if (isEditing) {
                            navController.navigate(Screen.ReceiptCapture.createRoute(transactionId))
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color(0xFF1976D2)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = "Comprobante",
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = if (isEditing) "Imagen adjunta" else "Agregar imagen de comprobante",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    if (!isEditing) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de ubicación
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF43A047)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = "Ubicación",
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = if (isEditing) "Plaza Las Américas, Chiapas" else "Usar mi ubicación actual",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Switch(
                        checked = isEditing,
                        onCheckedChange = { /* Aquí implementaríamos la lógica para obtener la ubicación */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón guardar
            CustomButton(
                text = if (isEditing) "Actualizar Transacción" else "Guardar Transacción",
                onClick = {
                    val isAmountValid = validateAmount()
                    val isDescriptionValid = validateDescription()

                    if (isAmountValid && isDescriptionValid && category.isNotEmpty()) {
                        // Aquí creamos o actualizamos la transacción con los datos del formulario
                        val transaction = Transaction().apply {
                            id = if (isEditing) transactionId else UUID.randomUUID().toString()
                            amount = amount  // No convertir a Double, mantener como String
                            description = description
                            category = category
                            date = date
                            isExpense = isExpense
                            isRecurring = isRecurring
                            recurringPeriod = recurringPeriod
                            recurringEndDate = recurringEndDate
                        }

                        // En una implementación real, llamaríamos al ViewModel para guardar los datos
                        if (isEditing) {
                            navController.navigate(Screen.TransactionDetail.createRoute(transactionId)) {
                                popUpTo(Screen.TransactionList.route)
                            }
                        } else {
                            navController.navigate(Screen.TransactionList.route)
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isEditing) {
                // Botón de eliminar (solo en modo edición)
                CustomButton(
                    text = "Eliminar Transacción",
                    onClick = {
                        // Aquí implementaríamos la lógica para eliminar la transacción
                        navController.navigate(Screen.TransactionList.route)
                    },
                    isSecondary = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TransactionTypeButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        if (text == "Gasto") Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    } else {
        Color.Transparent
    }

    val contentColor = if (isSelected) {
        if (text == "Gasto") Color(0xFFE53935) else Color(0xFF43A047)
    } else {
        Color.Gray
    }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}