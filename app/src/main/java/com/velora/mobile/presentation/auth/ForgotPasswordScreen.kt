package com.velora.mobile.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.velora.mobile.R

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()

    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        vm.events.flow.collect { e ->
            when (e) {
                is AuthEvent.Error -> {
                    message = e.msg
                    isError = true
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
                is AuthEvent.Success -> {
                    message = e.msg
                    isError = false
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                AuthTitle(
                    title = stringResource(R.string.forgot_password),
                    subtitle = stringResource(R.string.enter_email)
                )

                Spacer(Modifier.height(14.dp))

                PillTextField(
                    value = ui.email,
                    onValueChange = vm::setEmail,
                    placeholder = stringResource(R.string.email),
                    leading = { Icon(Icons.Rounded.Email, contentDescription = null) }
                )

                message?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = it,
                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(16.dp))

                PrimaryAuthButton(
                    text = if (ui.loading) stringResource(R.string.sending) else stringResource(R.string.send_reset_link),
                    enabled = ui.canResetPassword
                ) {
                    message = null
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    vm.sendPasswordReset()
                }

                Spacer(Modifier.height(10.dp))
            }
        }
    }
}