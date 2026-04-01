package com.velora.mobile.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    val context = LocalContext.current

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
                .verticalScroll(rememberScrollState())
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
                                imageVector = if (showPw) {
                                    Icons.Rounded.VisibilityOff
                                } else {
                                    Icons.Rounded.Visibility
                                },
                                contentDescription = null
                            )
                        }
                    },
                    visual = if (showPw) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    }
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
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(10.dp))

                PrimaryAuthButton(
                    text = if (ui.loading) {
                        stringResource(R.string.signing_in)
                    } else {
                        stringResource(R.string.sign_in)
                    },
                    enabled = ui.canSubmit
                ) {
                    error = null
                    vm.login()
                }

                Spacer(Modifier.height(14.dp))

                AuthDivider(text = stringResource(R.string.or_continue_with))

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    GoogleIconButton(
                        loading = ui.loading,
                        onClick = {
                            error = null
                            vm.loginWithGoogle(context)
                        }
                    )
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

@Composable
private fun AuthDivider(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )

        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Divider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    }
}

@Composable
private fun GoogleIconButton(
    loading: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable(
                enabled = !loading,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google sign in",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}