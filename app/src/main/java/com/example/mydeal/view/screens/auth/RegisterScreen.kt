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
import com.example.mydeal.view.components.CustomButton
import com.example.mydeal.view.components.CustomTextField
import com.example.mydeal.view.navigation.Screen
import com.example.mydeal.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        if (viewModel.isAuthenticated()) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    val registerResult by viewModel.registerResult.observeAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(registerResult) {
        when (registerResult) {
            is ApiResponse.Success -> {
                snackbarHostState.showSnackbar("Registro exitoso")
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
            is ApiResponse.Error -> {
                val error = (registerResult as ApiResponse.Error).message
                snackbarHostState.showSnackbar("Error: $error")
            }
            else -> {}
        }
    }

    val context = LocalContext.current

    val errorNameEmpty = "El nombre es obligatorio"
    val errorNameShort = "El nombre debe tener al menos 3 caracteres"
    val errorEmailEmpty = stringResource(R.string.error_email_empty)
    val errorEmailInvalid = stringResource(R.string.error_email_invalid)
    val errorPasswordEmpty = stringResource(R.string.error_password_empty)
    val errorPasswordShort = stringResource(R.string.error_password_short)
    val errorPasswordsNotMatch = "Las contraseñas no coinciden"

    val validateName: () -> Boolean = {
        when {
            name.isEmpty() -> {
                nameError = errorNameEmpty
                false
            }
            name.length < 3 -> {
                nameError = errorNameShort
                false
            }
            else -> {
                nameError = ""
                true
            }
        }
    }

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

    val validateConfirmPassword: () -> Boolean = {
        when {
            confirmPassword.isEmpty() -> {
                confirmPasswordError = errorPasswordEmpty
                false
            }
            confirmPassword != password -> {
                confirmPasswordError = errorPasswordsNotMatch
                false
            }
            else -> {
                confirmPasswordError = ""
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
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "MyDeal",
                    fontSize = 36.sp,
                    color = Color(0xFF1976D2),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = "Registro de nueva cuenta",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Campo de nombre
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre completo",
                    leadingIcon = Icons.Default.Person,
                    isError = nameError.isNotEmpty(),
                    errorMessage = nameError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = stringResource(R.string.email),
                    leadingIcon = Icons.Default.Email,
                    isError = emailError.isNotEmpty(),
                    errorMessage = emailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = stringResource(R.string.password),
                    leadingIcon = Icons.Default.Lock,
                    isError = passwordError.isNotEmpty(),
                    errorMessage = passwordError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = if (isPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible)
                                    Icons.Default.VisibilityOff
                                else
                                    Icons.Default.Visibility,
                                contentDescription = if (isPasswordVisible)
                                    "Ocultar contraseña"
                                else
                                    "Mostrar contraseña"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirmar contraseña",
                    leadingIcon = Icons.Default.Lock,
                    isError = confirmPasswordError.isNotEmpty(),
                    errorMessage = confirmPasswordError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = if (isConfirmPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible)
                                    Icons.Default.VisibilityOff
                                else
                                    Icons.Default.Visibility,
                                contentDescription = if (isConfirmPasswordVisible)
                                    "Ocultar contraseña"
                                else
                                    "Mostrar contraseña"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                CustomButton(
                    text = "Registrarse",
                    onClick = {
                        val isNameValid = validateName()
                        val isEmailValid = validateEmail()
                        val isPasswordValid = validatePassword()
                        val isConfirmPasswordValid = validateConfirmPassword()

                        if (isNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid) {
                            viewModel.register(name, email, password)
                        }
                    },
                    enabled = registerResult !is ApiResponse.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { navController.navigate(Screen.Login.route) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? Inicia sesión",
                        color = Color(0xFF1976D2)
                    )
                }
            }

            if (registerResult is ApiResponse.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}