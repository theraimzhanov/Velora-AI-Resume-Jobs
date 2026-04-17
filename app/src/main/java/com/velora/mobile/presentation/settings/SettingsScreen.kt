package com.velora.mobile.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.velora.mobile.R
import com.velora.mobile.presentation.ui.SoftBackground
import com.velora.mobile.presentation.ui.SoftCard

@Composable
fun SettingsScreen(
    selectedLanguageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    val context = LocalContext.current

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val supportEmail = "raimjanovnursultan@gmail.com"
    val privacyPolicyUrl = "https://github.com/theraimzhanov/velora-privacy-policy"

    val currentLanguageText = when (selectedLanguageCode) {
        "ru" -> "Русский"
        "es" -> "Español"
        else -> "English"
    }

    SoftBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            SettingsHeader()

            Spacer(modifier = Modifier.height(20.dp))

            SettingsSectionTitle(stringResource(R.string.appearance))
            Spacer(modifier = Modifier.height(8.dp))

            SoftCard(modifier = Modifier.fillMaxWidth()) {
                SettingClickableItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.DarkMode,
                            contentDescription = null
                        )
                    },
                    title = stringResource(R.string.app_theme),
                    subtitle = stringResource(R.string.follows_phone_mode),
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            SettingsSectionTitle(stringResource(R.string.preferences))
            Spacer(modifier = Modifier.height(8.dp))

            SoftCard(modifier = Modifier.fillMaxWidth()) {
                SettingClickableItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Language,
                            contentDescription = null
                        )
                    },
                    title = stringResource(R.string.language),
                    subtitle = stringResource(R.string.current, currentLanguageText),
                    onClick = { showLanguageDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            SettingsSectionTitle(stringResource(R.string.information))
            Spacer(modifier = Modifier.height(8.dp))

            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Column {

                    SettingClickableItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null
                            )
                        },
                        title = stringResource(R.string.about_velora),
                        subtitle = stringResource(R.string.learn_more_about_velora),
                        onClick = { showAboutDialog = true }
                    )

                    SettingClickableItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null
                            )
                        },
                        title = "Privacy Policy",
                        subtitle = "Open privacy policy",
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/theraimzhanov/velora-privacy-policy")
                            )
                            runCatching {
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            SettingsSectionTitle(stringResource(R.string.help))
            Spacer(modifier = Modifier.height(8.dp))

            SoftCard(modifier = Modifier.fillMaxWidth()) {
                SettingClickableItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Email,
                            contentDescription = null
                        )
                    },
                    title = stringResource(R.string.support),
                    subtitle = supportEmail,
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_SENDTO,
                            Uri.parse("mailto:$supportEmail")
                        ).apply {
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                context.getString(R.string.velora_support)
                            )
                        }

                        runCatching {
                            context.startActivity(intent)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (showLanguageDialog) {
            LanguageDialog(
                selectedLanguageCode = selectedLanguageCode,
                onSelect = { code ->
                    showLanguageDialog = false
                    onLanguageSelected(code)
                },
                onDismiss = { showLanguageDialog = false }
            )
        }

        if (showAboutDialog) {
            AboutDialog(
                onDismiss = { showAboutDialog = false }
            )
        }

        if (showPrivacyDialog) {
            PrivacyPolicyDialog(
                onDismiss = { showPrivacyDialog = false },
                onOpenLink = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(privacyPolicyUrl)
                    )
                    runCatching {
                        context.startActivity(intent)
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsHeader() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun SettingSwitchItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ) {
            Box(
                modifier = Modifier.size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingClickableItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ) {
                Box(
                    modifier = Modifier.size(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }

            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun LanguageDialog(
    selectedLanguageCode: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf(
        "English" to "en",
        "Русский" to "ru",
        "Español" to "es"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text(stringResource(R.string.choose_language)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                languages.forEach { (name, code) ->
                    Surface(
                        onClick = { onSelect(code) },
                        shape = MaterialTheme.shapes.large,
                        color = if (selectedLanguageCode == code) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                modifier = Modifier.weight(1f)
                            )

                            if (selectedLanguageCode == code) {
                                Text(
                                    text = stringResource(R.string.selected),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun AboutDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            Text(stringResource(R.string.about_velora))
        },
        text = {
            Text(
                text = stringResource(R.string.info_app)
            )
        }
    )
}

@Composable
private fun PrivacyPolicyDialog(
    onDismiss: () -> Unit,
    onOpenLink: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                TextButton(onClick = onOpenLink) {
                    Text("Open Link")
                }
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        },
        title = {
            Text("Privacy Policy")
        },
        text = {
            Text(
                text =
                    "Velora AI respects your privacy. We collect limited data such as your email, resume files, and app usage information to provide job tracking and AI resume analysis features.\n\n" +
                            "Your data is securely stored using Firebase services.\n\n" +
                            "We may use Google Firebase and AI services to improve functionality.\n\n" +
                            "You can request account and data deletion at any time by contacting:\n" +
                            "raimjanovnursultan@gmail.com\n\n" +
                            "By using Velora AI, you agree to this Privacy Policy."
            )
        }
    )
}