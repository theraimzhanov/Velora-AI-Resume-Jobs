package com.example.velora.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.velora.R

@Composable
fun RegisterScreen(
    onGoLogin: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()
    var error by remember { mutableStateOf<String?>(null) }
    var showPw by remember { mutableStateOf(false) }

    // If you don’t have name in state, you can remove this field
    var name by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        vm.events.flow.collect { e ->
            if (e is AuthEvent.Error) error = e.msg
        }
    }

    AuthBackground {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.Center
        ) {
            AuthCard(Modifier.fillMaxWidth()) {

                Hero(R.drawable.register)

                Spacer(Modifier.height(10.dp))

                AuthTitle(
                    title = "Register",
                    subtitle = "Please register to continue."
                )

                Spacer(Modifier.height(14.dp))

                // Optional name field (remove if you don't want)
                PillTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Full name",
                    leading = { Icon(Icons.Rounded.Person, contentDescription = null) }
                )

                Spacer(Modifier.height(10.dp))

                PillTextField(
                    value = ui.email,
                    onValueChange = vm::setEmail,
                    placeholder = "Email",
                    leading = { Icon(Icons.Rounded.Email, contentDescription = null) }
                )

                Spacer(Modifier.height(10.dp))

                PillTextField(
                    value = ui.password,
                    onValueChange = vm::setPassword,
                    placeholder = "Password",
                    leading = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                    trailing = {
                        IconButton(onClick = { showPw = !showPw }) {
                            Icon(if (showPw) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, null)
                        }
                    },
                    visual = if (showPw) VisualTransformation.None else PasswordVisualTransformation()
                )

                error?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(14.dp))

                PrimaryAuthButton(
                    text = if (ui.loading) "Creating..." else "Sign Up",
                    enabled = ui.canSubmit
                ) {
                    error = null
                    vm.register()
                }

                Spacer(Modifier.height(10.dp))

                TextButton(
                    onClick = onGoLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Already have an account? Sign In", color = Color(0xFF2E5E73))
                }
            }
        }
    }
}