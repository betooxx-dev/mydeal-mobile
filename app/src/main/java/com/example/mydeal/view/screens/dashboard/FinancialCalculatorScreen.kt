package com.example.mydeal.view.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mydeal.view.components.CustomButton
import com.example.mydeal.view.components.CustomTextField
import com.example.mydeal.viewmodel.FinancialViewModel
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialCalculatorScreen(
    navController: NavController,
    viewModel: FinancialViewModel = viewModel()
) {
    val context = LocalContext.current
    val compoundInterestResult by viewModel.compoundInterestResult.observeAsState("")
    val loanPaymentResult by viewModel.loanPaymentResult.observeAsState("")
    val savingProjectionResult by viewModel.savingProjectionResult.observeAsState("")
    val budgetResult by viewModel.budgetResult.observeAsState(emptyMap())

    val selectedCalculator = remember { mutableStateOf(CalculatorType.COMPOUND_INTEREST) }

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

    // Estados para presupuesto
    var income by remember { mutableStateOf("15000") }

    // Efecto para vincular/desvincular el servicio
    DisposableEffect(key1 = viewModel) {
        viewModel.bindService(context)
        onDispose {
            viewModel.unbindService(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calculadora Financiera",
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Selector de tipo de calculadora
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Selecciona un cálculo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CalculatorTypeButton(
                            text = "Interés",
                            icon = Icons.Default.Timeline,
                            isSelected = selectedCalculator.value == CalculatorType.COMPOUND_INTEREST,
                            onClick = { selectedCalculator.value = CalculatorType.COMPOUND_INTEREST }
                        )

                        CalculatorTypeButton(
                            text = "Préstamo",
                            icon = Icons.Default.CreditCard,
                            isSelected = selectedCalculator.value == CalculatorType.LOAN_PAYMENT,
                            onClick = { selectedCalculator.value = CalculatorType.LOAN_PAYMENT }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CalculatorTypeButton(
                            text = "Ahorro",
                            icon = Icons.Default.Savings,
                            isSelected = selectedCalculator.value == CalculatorType.SAVING_PROJECTION,
                            onClick = { selectedCalculator.value = CalculatorType.SAVING_PROJECTION }
                        )

                        CalculatorTypeButton(
                            text = "Presupuesto",
                            icon = Icons.Default.AccountBalance,
                            isSelected = selectedCalculator.value == CalculatorType.BUDGET,
                            onClick = { selectedCalculator.value = CalculatorType.BUDGET }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calculadora específica según selección
            when (selectedCalculator.value) {
                CalculatorType.COMPOUND_INTEREST -> {
                    CompoundInterestCalculator(
                        principalAmount = principalAmount,
                        onPrincipalChange = { principalAmount = it },
                        interestRate = interestRate,
                        onInterestRateChange = { interestRate = it },
                        years = years,
                        onYearsChange = { years = it },
                        result = compoundInterestResult,
                        onCalculate = {
                            viewModel.calculateCompoundInterest(
                                principalAmount.toDoubleOrNull() ?: 0.0,
                                interestRate.toDoubleOrNull() ?: 0.0,
                                years.toIntOrNull() ?: 0
                            )
                        }
                    )
                }
                CalculatorType.LOAN_PAYMENT -> {
                    LoanPaymentCalculator(
                        loanAmount = loanAmount,
                        onLoanAmountChange = { loanAmount = it },
                        loanRate = loanRate,
                        onLoanRateChange = { loanRate = it },
                        loanYears = loanYears,
                        onLoanYearsChange = { loanYears = it },
                        result = loanPaymentResult,
                        onCalculate = {
                            viewModel.calculateLoanPayment(
                                loanAmount.toDoubleOrNull() ?: 0.0,
                                loanRate.toDoubleOrNull() ?: 0.0,
                                loanYears.toIntOrNull() ?: 0
                            )
                        }
                    )
                }
                CalculatorType.SAVING_PROJECTION -> {
                    SavingProjectionCalculator(
                        monthlySaving = monthlySaving,
                        onMonthlySavingChange = { monthlySaving = it },
                        savingRate = savingRate,
                        onSavingRateChange = { savingRate = it },
                        savingYears = savingYears,
                        onSavingYearsChange = { savingYears = it },
                        result = savingProjectionResult,
                        onCalculate = {
                            viewModel.calculateSavingProjection(
                                monthlySaving.toDoubleOrNull() ?: 0.0,
                                savingRate.toDoubleOrNull() ?: 0.0,
                                savingYears.toIntOrNull() ?: 0
                            )
                        }
                    )
                }
                CalculatorType.BUDGET -> {
                    BudgetCalculator(
                        income = income,
                        onIncomeChange = { income = it },
                        result = budgetResult,
                        onCalculate = {
                            viewModel.calculateBudget(
                                income.toDoubleOrNull() ?: 0.0
                            )
                        }
                    )
                }
            }
        }
    }
}

enum class CalculatorType {
    COMPOUND_INTEREST,
    LOAN_PAYMENT,
    SAVING_PROJECTION,
    BUDGET
}

@Composable
fun CalculatorTypeButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
    val contentColor = if (isSelected) Color.White else Color.Black

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        modifier = Modifier
            .padding(4.dp)
            .width(160.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text)
        }
    }
}

@Composable
fun CompoundInterestCalculator(
    principalAmount: String,
    onPrincipalChange: (String) -> Unit,
    interestRate: String,
    onInterestRateChange: (String) -> Unit,
    years: String,
    onYearsChange: (String) -> Unit,
    result: String,
    onCalculate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Calculadora de Interés Compuesto",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomTextField(
                value = principalAmount,
                onValueChange = onPrincipalChange,
                label = "Capital inicial",
                leadingIcon = Icons.Default.AttachMoney,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomTextField(
                value = interestRate,
                onValueChange = onInterestRateChange,
                label = "Tasa de interés (%)",
                leadingIcon = Icons.Default.Percent,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomTextField(
                value = years,
                onValueChange = onYearsChange,
                label = "Años",
                leadingIcon = Icons.Default.DateRange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                text = "Calcular",
                onClick = onCalculate
            )

            if (result.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                ResultCard(
                    title = "Monto final",
                    value = result
                )
            }
        }
    }
}

@Composable
fun LoanPaymentCalculator(
    loanAmount: String,
    onLoanAmountChange: (String) -> Unit,
    loanRate: String,
    onLoanRateChange: (String) -> Unit,
    loanYears: String,
    onLoanYearsChange: (String) -> Unit,
    result: String,
    onCalculate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Calculadora de Préstamos",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomTextField(
                value = loanAmount,
                onValueChange = onLoanAmountChange,
                label = "Monto del préstamo",
                leadingIcon = Icons.Default.AttachMoney,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomTextField(
                value = loanRate,
                onValueChange = onLoanRateChange,
                label = "Tasa de interés (%)",
                leadingIcon = Icons.Default.Percent,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomTextField(
                value = loanYears,
                onValueChange = onLoanYearsChange,
                label = "Plazo (años)",
                leadingIcon = Icons.Default.DateRange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                text = "Calcular",
                onClick = onCalculate
            )

            if (result.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                ResultCard(
                    title = "Pago mensual",
                    value = result
                )
            }
        }
    }
}

@Composable
fun SavingProjectionCalculator(
    monthlySaving: String,
    onMonthlySavingChange: (String) -> Unit,
    savingRate: String,
    onSavingRateChange: (String) -> Unit,
    savingYears: String,
    onSavingYearsChange: (String) -> Unit,
    result: String,
    onCalculate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Proyección de Ahorro",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomTextField(
                value = monthlySaving,
                onValueChange = onMonthlySavingChange,
                label = "Ahorro mensual",
                leadingIcon = Icons.Default.AttachMoney,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomTextField(
                value = savingRate,
                onValueChange = onSavingRateChange,
                label = "Tasa de interés (%)",
                leadingIcon = Icons.Default.Percent,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomTextField(
                value = savingYears,
                onValueChange = onSavingYearsChange,
                label = "Años de ahorro",
                leadingIcon = Icons.Default.DateRange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                text = "Calcular",
                onClick = onCalculate
            )

            if (result.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                ResultCard(
                    title = "Total ahorrado",
                    value = result
                )
            }
        }
    }
}

@Composable
fun BudgetCalculator(
    income: String,
    onIncomeChange: (String) -> Unit,
    result: Map<String, String>,
    onCalculate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Calculadora de Presupuesto",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomTextField(
                value = income,
                onValueChange = onIncomeChange,
                label = "Ingreso mensual",
                leadingIcon = Icons.Default.AttachMoney,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                text = "Calcular",
                onClick = onCalculate
            )

            if (result.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Text(
                        text = "Presupuesto sugerido (50/30/20)",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    result.forEach { (category, amount) ->
                        ResultItem(
                            category = category,
                            amount = amount
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ResultItem(
    category: String,
    amount: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = amount,
            fontWeight = FontWeight.Bold
        )
    }
}