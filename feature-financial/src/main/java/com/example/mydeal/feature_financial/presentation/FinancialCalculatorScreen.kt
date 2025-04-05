package com.example.mydeal.feature_financial.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialCalculatorScreen(
    viewModel: FinancialCalculatorViewModel = koinViewModel()
) {
    val context = LocalContext.current

    // Vincular y desvincular el servicio
    DisposableEffect(viewModel) {
        viewModel.bindService(context)
        onDispose {
            viewModel.unbindService(context)
        }
    }

    // Estados para diferentes tipos de cálculos
    var calculatorType by remember { mutableStateOf(CalculatorType.COMPOUND_INTEREST) }

    // Estados para interés compuesto
    var principalAmount by remember { mutableStateOf("10000") }
    var interestRate by remember { mutableStateOf("5.0") }
    var years by remember { mutableStateOf("5") }

    // Estados para préstamo
    var loanAmount by remember { mutableStateOf("100000") }
    var loanRate by remember { mutableStateOf("7.5") }
    var loanYears by remember { mutableStateOf("10") }

    // Estados para proyección de ahorro
    var monthlySaving by remember { mutableStateOf("1000") }
    var savingRate by remember { mutableStateOf("4.0") }
    var savingYears by remember { mutableStateOf("10") }

    // Resultados de los cálculos
    val compoundInterestResult by viewModel.compoundInterestResult.observeAsState("")
    val loanPaymentResult by viewModel.loanPaymentResult.observeAsState("")
    val savingProjectionResult by viewModel.savingProjectionResult.observeAsState("")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calculadora Financiera",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Selector de tipo de calculadora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CalculatorTypeButton(
                    text = "Interés",
                    icon = Icons.Default.Timeline,
                    isSelected = calculatorType == CalculatorType.COMPOUND_INTEREST,
                    onClick = { calculatorType = CalculatorType.COMPOUND_INTEREST }
                )
                CalculatorTypeButton(
                    text = "Préstamo",
                    icon = Icons.Default.CreditCard,
                    isSelected = calculatorType == CalculatorType.LOAN_PAYMENT,
                    onClick = { calculatorType = CalculatorType.LOAN_PAYMENT }
                )
                CalculatorTypeButton(
                    text = "Ahorro",
                    icon = Icons.Default.Savings,
                    isSelected = calculatorType == CalculatorType.SAVING_PROJECTION,
                    onClick = { calculatorType = CalculatorType.SAVING_PROJECTION }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido dinámico según el tipo de calculadora
            when (calculatorType) {
                CalculatorType.COMPOUND_INTEREST -> {
                    FinancialInputField(
                        value = principalAmount,
                        onValueChange = { principalAmount = it },
                        label = "Capital inicial",
                        leadingIcon = Icons.Default.AttachMoney
                    )
                    FinancialInputField(
                        value = interestRate,
                        onValueChange = { interestRate = it },
                        label = "Tasa de interés (%)",
                        leadingIcon = Icons.Default.Percent
                    )
                    FinancialInputField(
                        value = years,
                        onValueChange = { years = it },
                        label = "Años",
                        leadingIcon = Icons.Default.DateRange
                    )

                    Button(
                        onClick = {
                            viewModel.calculateCompoundInterest(
                                principalAmount.toDoubleOrNull() ?: 0.0,
                                interestRate.toDoubleOrNull() ?: 0.0,
                                years.toIntOrNull() ?: 0
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Calcular Interés Compuesto")
                    }

                    if (compoundInterestResult.isNotEmpty()) {
                        ResultCard(title = "Monto Final", value = compoundInterestResult)
                    }
                }
                CalculatorType.LOAN_PAYMENT -> {
                    FinancialInputField(
                        value = loanAmount,
                        onValueChange = { loanAmount = it },
                        label = "Monto del préstamo",
                        leadingIcon = Icons.Default.AttachMoney
                    )
                    FinancialInputField(
                        value = loanRate,
                        onValueChange = { loanRate = it },
                        label = "Tasa de interés (%)",
                        leadingIcon = Icons.Default.Percent
                    )
                    FinancialInputField(
                        value = loanYears,
                        onValueChange = { loanYears = it },
                        label = "Plazo (años)",
                        leadingIcon = Icons.Default.DateRange
                    )

                    Button(
                        onClick = {
                            viewModel.calculateLoanPayment(
                                loanAmount.toDoubleOrNull() ?: 0.0,
                                loanRate.toDoubleOrNull() ?: 0.0,
                                loanYears.toIntOrNull() ?: 0
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Calcular Pago de Préstamo")
                    }

                    if (loanPaymentResult.isNotEmpty()) {
                        ResultCard(title = "Pago Mensual", value = loanPaymentResult)
                    }
                }
                CalculatorType.SAVING_PROJECTION -> {
                    FinancialInputField(
                        value = monthlySaving,
                        onValueChange = { monthlySaving = it },
                        label = "Ahorro mensual",
                        leadingIcon = Icons.Default.AttachMoney
                    )
                    FinancialInputField(
                        value = savingRate,
                        onValueChange = { savingRate = it },
                        label = "Tasa de interés (%)",
                        leadingIcon = Icons.Default.Percent
                    )
                    FinancialInputField(
                        value = savingYears,
                        onValueChange = { savingYears = it },
                        label = "Años de ahorro",
                        leadingIcon = Icons.Default.DateRange
                    )

                    Button(
                        onClick = {
                            viewModel.calculateSavingProjection(
                                monthlySaving.toDoubleOrNull() ?: 0.0,
                                savingRate.toDoubleOrNull() ?: 0.0,
                                savingYears.toIntOrNull() ?: 0
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Calcular Proyección de Ahorro")
                    }

                    if (savingProjectionResult.isNotEmpty()) {
                        ResultCard(title = "Total Ahorrado", value = savingProjectionResult)
                    }
                }
            }
        }
    }
}

@Composable
fun FinancialInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun CalculatorTypeButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Text(text, fontSize = 12.sp)
        }
    }
}

@Composable
fun ResultCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

enum class CalculatorType {
    COMPOUND_INTEREST,
    LOAN_PAYMENT,
    SAVING_PROJECTION
}