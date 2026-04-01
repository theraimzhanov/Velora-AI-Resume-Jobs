package com.velora.mobile.presentation.auth

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.velora.mobile.R

@Composable
fun LoginScreen(
    onGoRegister: () -> Unit,
    onGoForgotPassword: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()
    var error by remember { mutableStateOf<String?>(null) }
    var showPw by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.events.flow.collect { e ->
            when (e) {
                is AuthEvent.Error -> error = e.msg
                is AuthEvent.Success -> Unit
            }
        }
    }

    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.Center
        ) {
            AuthCard(Modifier.fillMaxWidth()) {
                Hero(R.drawable.login)

                Spacer(Modifier.height(10.dp))

                AuthTitle(
                    title = stringResource(R.string.login),
                    subtitle = stringResource(R.string.please_sign)
                )

                Spacer(Modifier.height(14.dp))

                PillTextField(
                    value = ui.email,
                    onValueChange = vm::setEmail,
                    placeholder = stringResource(R.string.email),
                    leading = { Icon(Icons.Rounded.Email, contentDescription = null) }
                )

                Spacer(Modifier.height(10.dp))

                PillTextField(
                    value = ui.password,
                    onValueChange = vm::setPassword,
                    placeholder = stringResource(R.string.password),
                    leading = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                    trailing = {
                        IconButton(onClick = { showPw = !showPw }) {
                            Icon(
                                imageVector = if (showPw) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visual = if (showPw) VisualTransformation.None else PasswordVisualTransformation()
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onGoForgotPassword) {
                        Text(
                            text = stringResource(R.string.forgot_password),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                error?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(10.dp))

                PrimaryAuthButton(
                    text = if (ui.loading) stringResource(R.string.signing_in) else stringResource(R.string.sign_in),
                    enabled = ui.canSubmit
                ) {
                    error = null
                    vm.login()
                }

                Spacer(Modifier.height(10.dp))

                TextButton(
                    onClick = onGoRegister,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.don_t_have),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}