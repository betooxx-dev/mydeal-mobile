package com.example.mydeal.view.screens.transaction

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mydeal.model.api.ApiResponse
import com.example.mydeal.model.data.RecurringPeriod
import com.example.mydeal.view.components.CustomButton
import com.example.mydeal.view.components.CustomTextField
import com.example.mydeal.view.navigation.Screen
import com.example.mydeal.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.os.Handler
import com.example.mydeal.ui.theme.LightGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    transactionId: String = "",
    isEditing: Boolean = false,
    viewModel: TransactionViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(transactionId) {
        if (isEditing && transactionId.isNotEmpty()) {
            viewModel.getTransactionById(transactionId)
        }
    }

    val transactionDetailResult by viewModel.transactionDetail.observeAsState()
    val uploadReceiptResult by viewModel.uploadReceiptResult.observeAsState()
    val createTransactionResult by viewModel.createTransactionResult.observeAsState()

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var category by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategorySelector by remember { mutableStateOf(false) }

    var isRecurring by remember { mutableStateOf(false) }
    var recurringPeriod by remember { mutableStateOf(RecurringPeriod.NONE) }
    var showRecurringOptions by remember { mutableStateOf(false) }
    var recurringEndDate by remember { mutableStateOf<String?>(null) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var receiptImageUri by remember { mutableStateOf<Uri?>(null) }
    var receiptUrl by remember { mutableStateOf<String?>(null) }

    var useLocation by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<String?>(null) }

    var amountError by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf("") }

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

    val recurringOptions = listOf(
        "No se repite" to RecurringPeriod.NONE,
        "Diariamente" to RecurringPeriod.DAILY,
        "Semanalmente" to RecurringPeriod.WEEKLY,
        "Mensualmente" to RecurringPeriod.MONTHLY,
        "Anualmente" to RecurringPeriod.YEARLY
    )

    fun createTempImageUri(): Uri {
        val tempFile = File.createTempFile(
            "receipt_",
            ".jpg",
            context.cacheDir
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    val getCurrentLocationWithPermissions = {
        getCurrentLocation(context) { location ->
            currentLocation = location
        }
    }


    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Permiso concedido, obtener ubicación
                getCurrentLocationWithPermissions()
            }
            else -> {
                // Permiso denegado
                useLocation = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Permiso de ubicación denegado. No se puede obtener la ubicación.")
                }
            }
        }
    }

    val getLocation = {
        if (useLocation) {
            // Comprobar y solicitar permisos primero
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED -> {
                    // Ya tenemos permiso, obtener ubicación
                    getCurrentLocationWithPermissions()
                }
                else -> {
                    // Solicitar permisos
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        } else {
            currentLocation = null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            receiptImageUri?.let { uri ->
                viewModel.uploadReceipt(uri)
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Captura de foto cancelada.")
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            receiptImageUri = uri
            viewModel.uploadReceipt(uri)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                val uri = createTempImageUri()
                receiptImageUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Error al preparar la cámara: ${e.message}")
                }
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Permiso de cámara denegado. No se puede tomar foto.")
            }
        }
    }


    LaunchedEffect(transactionDetailResult) {
        if (isEditing && transactionDetailResult is ApiResponse.Success) {
            val transaction = (transactionDetailResult as ApiResponse.Success).data
            amount = transaction.amount.toString()
            description = transaction.description
            category = transaction.category
            isExpense = transaction.isExpense
            date = transaction.date.substring(0, 10)
            isRecurring = transaction.isRecurring
            recurringPeriod = transaction.recurringPeriod
            receiptUrl = transaction.receiptUrl
            currentLocation = transaction.location
            useLocation = transaction.location != null
        }
    }

    LaunchedEffect(uploadReceiptResult) {
        when (uploadReceiptResult) {
            is ApiResponse.Success -> {
                receiptUrl = (uploadReceiptResult as ApiResponse.Success<String>).data
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Imagen subida correctamente")
                }
            }
            is ApiResponse.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Error: ${(uploadReceiptResult as ApiResponse.Error).message}")
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(createTransactionResult) {
        when (createTransactionResult) {
            is ApiResponse.Success -> {
                val transaction = (createTransactionResult as ApiResponse.Success).data
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        if (isEditing) "Transacción actualizada" else "Transacción creada"
                    )
                }
                navController.navigate(
                    if (isEditing) Screen.TransactionDetail.createRoute(transaction.id)
                    else Screen.TransactionList.route
                ) {
                    popUpTo(Screen.TransactionList.route)
                }
            }
            is ApiResponse.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Error: ${(createTransactionResult as ApiResponse.Error).message}")
                }
            }
            else -> {}
        }
    }

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
                    containerColor = LightGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                                    recurringPeriod = RecurringPeriod.NONE
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

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable {
                        showDialog(
                            context = context,
                            onCamera = {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) -> {
                                        try {
                                            val uri = createTempImageUri()
                                            receiptImageUri = uri
                                            cameraLauncher.launch(uri)
                                        } catch (e: Exception) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Error al abrir la cámara: ${e.message}")
                                            }
                                        }
                                    }
                                    else -> {
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            },
                            onGallery = {
                                galleryLauncher.launch("image/*")
                            }
                        )
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
                            text = when {
                                uploadReceiptResult is ApiResponse.Loading -> "Subiendo imagen..."
                                receiptUrl != null -> "Imagen subida correctamente"
                                receiptImageUri != null -> "Imagen seleccionada"
                                else -> "Agregar imagen de comprobante"
                            },
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    if (receiptUrl == null && uploadReceiptResult !is ApiResponse.Loading) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    } else if (receiptUrl != null) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF43A047)
                        )
                    } else if (uploadReceiptResult is ApiResponse.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                            text = if (currentLocation != null) currentLocation!! else "Usar mi ubicación actual",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Switch(
                        checked = useLocation,
                        onCheckedChange = {
                            useLocation = it
                            if (it) {
                                getLocation()
                            } else {
                                currentLocation = null
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            CustomButton(
                text = if (isEditing) "Actualizar Transacción" else "Guardar Transacción",
                onClick = {
                    val isAmountValid = validateAmount()
                    val isDescriptionValid = validateDescription()

                    if (isAmountValid && isDescriptionValid && category.isNotEmpty()) {
                        viewModel.createTransaction(
                            amount = amount.toDouble(),
                            isExpense = isExpense,
                            description = description,
                            category = category,
                            date = date,
                            isRecurring = isRecurring,
                            recurringPeriod = if (isRecurring) recurringPeriod else RecurringPeriod.NONE,
                            receiptUrl = receiptUrl,
                            location = currentLocation
                        )
                    } else {
                        if (category.isEmpty()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Por favor selecciona una categoría")
                            }
                        }
                    }
                },
                enabled = createTransactionResult !is ApiResponse.Loading
            )

            if (createTransactionResult is ApiResponse.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isEditing) {
                CustomButton(
                    text = "Eliminar Transacción",
                    onClick = {
                        // TODO: Implement delete logic in ViewModel and call it here
                        // viewModel.deleteTransaction(transactionId)
                        // For now, just navigate back
                        navController.navigate(Screen.TransactionList.route) {
                            popUpTo(Screen.TransactionList.route) { inclusive = true }
                        }
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

fun showDialog(context: Context, onCamera: () -> Unit, onGallery: () -> Unit) {
    val builder = android.app.AlertDialog.Builder(context)
    builder.setTitle("Seleccionar imagen")
    val options = arrayOf("Tomar foto", "Elegir de la galería")

    builder.setItems(options) { dialog, which ->
        when (which) {
            0 -> onCamera()
            1 -> onGallery()
        }
        dialog.dismiss()
    }

    builder.show()
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, onLocationObtained: (String) -> Unit) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Verificar si hay algún proveedor disponible
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
            !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            onLocationObtained("Ubicación desactivada en el dispositivo")
            return
        }

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val geocoder = Geocoder(context, Locale.getDefault())
                try {
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val addressString = StringBuilder()

                        if (address.locality != null) {
                            addressString.append(address.locality)
                        }
                        if (address.adminArea != null) {
                            if (addressString.isNotEmpty()) addressString.append(", ")
                            addressString.append(address.adminArea)
                        }
                        if (address.countryName != null) {
                            if (addressString.isNotEmpty()) addressString.append(", ")
                            addressString.append(address.countryName)
                        }

                        if (addressString.isNotEmpty()) {
                            onLocationObtained(addressString.toString())
                        } else {
                            onLocationObtained("${location.latitude}, ${location.longitude}")
                        }
                    } else {
                        onLocationObtained("${location.latitude}, ${location.longitude}")
                    }
                } catch (e: IOException) {
                    onLocationObtained("${location.latitude}, ${location.longitude}")
                }

                locationManager.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {
                onLocationObtained("Proveedor de ubicación desactivado")
            }
        }

        // Determinar el mejor proveedor disponible
        val provider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> null
        }

        if (provider == null) {
            onLocationObtained("No hay proveedores de ubicación disponibles")
            return
        }

        // Intentar obtener la última ubicación conocida primero
        val lastLocation = locationManager.getLastKnownLocation(provider)
        if (lastLocation != null) {
            // Si tenemos una ubicación reciente, usarla inmediatamente
            locationListener.onLocationChanged(lastLocation)
        } else {
            // De lo contrario, solicitar actualización
            locationManager.requestLocationUpdates(
                provider,
                0,
                0f,
                locationListener,
                Looper.getMainLooper()
            )

            // Establecer un timeout para la solicitud de ubicación
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    locationManager.removeUpdates(locationListener)
                    onLocationObtained("Tiempo de espera agotado al obtener ubicación")
                } catch (e: Exception) {
                    // Ignorar, ya podría haberse cancelado
                }
            }, 30000) // 30 segundos de timeout
        }
    } catch (e: Exception) {
        onLocationObtained("Error al obtener ubicación: ${e.message}")
    }
}