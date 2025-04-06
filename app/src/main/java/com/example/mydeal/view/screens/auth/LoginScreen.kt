package com.example.mydeal.view.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mydeal.R
import com.example.mydeal.model.api.ApiResponse
import com.example.mydeal.model.service.ReminderService
import com.example.mydeal.view.components.CustomButton
import com.example.mydeal.view.components.CustomTextField
import com.example.mydeal.view.navigation.Screen
import com.example.mydeal.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    // Verificar si el usuario ya está autenticado
    LaunchedEffect(Unit) {
        if (viewModel.isAuthenticated()) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val loginResult by viewModel.loginResult.observeAsState()

    // Mostrar SnackBar con mensaje de error o éxito
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // *** CORRECCIÓN AQUÍ ***
    // Obtener el contexto fuera del LaunchedEffect
    val context = LocalContext.current

    // Evaluar resultado de login
    LaunchedEffect(loginResult) {
        when (loginResult) {
            is ApiResponse.Success -> {
                scope.launch { // Usar scope para lanzar la Snackbar si es necesario fuera del LaunchedEffect inmediato
                    snackbarHostState.showSnackbar("Inicio de sesión exitoso")
                }
                // Iniciar el servicio de recordatorios cuando el usuario inicia sesión
                // *** Usar la variable 'context' obtenida antes ***
                ReminderService.startService(context)
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
            is ApiResponse.Error -> {
                val error = (loginResult as ApiResponse.Error).message
                scope.launch { // Usar scope para lanzar la Snackbar
                    snackbarHostState.showSnackbar("Error: $error")
                }
            }
            // Considera agregar un caso para ApiResponse.Loading si no quieres que se muestre nada
            // o simplemente dejarlo así si el indicador de carga es suficiente.
            is ApiResponse.Loading -> {
                // Opcional: Podrías mostrar un mensaje en la Snackbar o no hacer nada
            }
            null -> {
                // Estado inicial, no hacer nada
            }
        }
    }

    // Ya no necesitas obtener el contexto aquí si solo se usaba en el LaunchedEffect
    // val context = LocalContext.current

    val errorEmailEmpty = stringResource(R.string.error_email_empty)
    val errorEmailInvalid = stringResource(R.string.error_email_invalid)
    val errorPasswordEmpty = stringResource(R.string.error_password_empty)
    val errorPasswordShort = stringResource(R.string.error_password_short)

    val validateEmail: () -> Boolean = {
        when {
            email.isEmpty() -> {
                emailError = errorEmailEmpty
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailError = errorEmailInvalid
                false
            }
            else -> {
                emailError = ""
                true
            }
        }
    }

    val validatePassword: () -> Boolean = {
        when {
            password.isEmpty() -> {
                passwordError = errorPasswordEmpty
                false
            }
            password.length < 6 -> {
                passwordError = errorPasswordShort
                false
            }
            else -> {
                passwordError = ""
                true
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplicar padding del Scaffold
            // Quitar el padding extra de 16.dp aquí si el Column ya lo tiene
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // Padding general del contenido
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "MyDeal",
                    style = MaterialTheme.typography.headlineLarge, // Usar estilos del tema
                    color = MaterialTheme.colorScheme.primary, // Usar colores del tema
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Text(
                    text = "Administración de Finanzas",
                    style = MaterialTheme.typography.titleMedium, // Usar estilos del tema
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                CustomTextField(
                    value = email,
                    onValueChange = { email = it; validateEmail() }, // Validar al cambiar texto
                    label = stringResource(R.string.email),
                    leadingIcon = Icons.Default.Email,
                    isError = emailError.isNotEmpty(),
                    errorMessage = emailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth() // Ocupar ancho completo
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = password,
                    onValueChange = { password = it; validatePassword() }, // Validar al cambiar texto
                    label = stringResource(R.string.password),
                    leadingIcon = Icons.Default.Lock,
                    isError = passwordError.isNotEmpty(),
                    errorMessage = passwordError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done // Acción final
                    ),
                    visualTransformation = if (isPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible)
                                    Icons.Filled.VisibilityOff
                                else
                                    Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth() // Ocupar ancho completo
                )

                // El Text para "Olvidaste contraseña" necesita un Clickable
                Text(
                    text = stringResource(R.string.forgot_password),
                    color = MaterialTheme.colorScheme.primary, // Usar colores del tema
                    style = MaterialTheme.typography.bodySmall, // Estilo adecuado
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp, bottom = 24.dp) // Ajustar padding
                        // .clickable { /* TODO: Navegar a pantalla de recuperación */ } // Hacerlo clickeable
                        .fillMaxWidth(), // Ya estaba
                    textAlign = TextAlign.End
                )

                CustomButton(
                    text = stringResource(R.string.login),
                    onClick = {
                        // Re-validar por si acaso antes de enviar
                        val isEmailValid = validateEmail()
                        val isPasswordValid = validatePassword()

                        if (isEmailValid && isPasswordValid) {
                            // Solo intentar login si no está ya cargando
                            if (loginResult !is ApiResponse.Loading) {
                                viewModel.login(email, password)
                            }
                        }
                    },
                    // Deshabilitar si está cargando O si hay errores
                    enabled = loginResult !is ApiResponse.Loading && emailError.isEmpty() && passwordError.isEmpty(),
                    modifier = Modifier.fillMaxWidth() // Ocupar ancho completo
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { navController.navigate(Screen.Register.route) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.no_account),
                        color = MaterialTheme.colorScheme.primary // Usar colores del tema
                    )
                }
            }

            // Indicador de carga centrado
            if (loginResult is ApiResponse.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}