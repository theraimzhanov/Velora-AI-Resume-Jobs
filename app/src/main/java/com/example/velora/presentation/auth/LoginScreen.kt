package com.example.velora.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.velora.R

@Composable
fun LoginScreen(
    onGoRegister: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var showPw by remember { mutableStateOf(false) }

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

                // Use your actual drawable name:
                Hero(R.drawable.login) // or Hero(R.drawable.login_hero)

                Spacer(Modifier.height(10.dp))

                AuthTitle(
                    title = "Login",
                    subtitle = "Please sign in to continue."
                )

                Spacer(Modifier.height(14.dp))

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
                            Icon(
                                if (showPw) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visual = if (showPw) VisualTransformation.None else PasswordVisualTransformation()
                )

                error?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(14.dp))

                // Email/Password login
                PrimaryAuthButton(
                    text = if (ui.loading) "Signing in..." else "Sign In",
                    enabled = ui.canSubmit
                ) {
                    error = null
                    vm.login()
                }

                // Google login
                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        error = null
                        vm.loginWithGoogle(context)
                    },
                    enabled = !ui.loading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFF2F2F2),
                        contentColor = Color(0xFF2E5E73)
                    ),
                    border = null
                ) {
                    Box(
                        Modifier
                            .size(28.dp)
                            .background(Color.White, RoundedCornerShape(999.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", color = Color(0xFF2E5E73))
                    }
                    Spacer(Modifier.width(10.dp))
                    Text("Continue with Google")
                }

                Spacer(Modifier.height(10.dp))

                TextButton(
                    onClick = onGoRegister,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Don’t have an account? Sign Up", color = Color(0xFF2E5E73))
                }
            }
        }
    }
}