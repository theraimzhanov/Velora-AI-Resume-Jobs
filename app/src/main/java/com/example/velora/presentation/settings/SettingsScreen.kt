package com.example.velora.presentation.settings

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
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
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
import com.example.velora.R
import com.example.velora.presentation.ui.SoftBackground
import com.example.velora.presentation.ui.SoftCard

@Composable
fun SettingsScreen(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    selectedLanguage: String,
    onLanguageSelected: (String, String) -> Unit
) {
    val context = LocalContext.current

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val supportEmail = "raimjanovnursultan@gmail.com"

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

            SoftCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                SettingSwitchItem(
                    icon = {
                        Icon(
                            imageVector = if (darkMode) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                            contentDescription = null
                        )
                    },
                    title = stringResource(R.string.dark_mode),
                    subtitle = if (darkMode) {
                        stringResource(R.string.extr1)
                    } else {
                        stringResource(R.string.extr2)
                    },
                    checked = darkMode,
                    onCheckedChange = onDarkModeChange
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            SettingsSectionTitle(stringResource(R.string.preferences))
            Spacer(modifier = Modifier.height(8.dp))

            SoftCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                SettingClickableItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Language,
                            contentDescription = null
                        )
                    },
                    title = stringResource(R.string.language),
                    subtitle = stringResource(R.string.current, selectedLanguage),
                    onClick = { showLanguageDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            SettingsSectionTitle(stringResource(R.string.information))
            Spacer(modifier = Modifier.height(8.dp))

            SoftCard(
                modifier = Modifier.fillMaxWidth()
            ) {
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
            }

            Spacer(modifier = Modifier.height(18.dp))

            SettingsSectionTitle(stringResource(R.string.help))
            Spacer(modifier = Modifier.height(8.dp))

            SoftCard(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                            putExtra(Intent.EXTRA_SUBJECT,
                                context.getString(R.string.velora_support))
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
                selectedLanguage = selectedLanguage,
                onSelect = { name, code ->
                    onLanguageSelected(name, code)
                    showLanguageDialog = false
                },
                onDismiss = { showLanguageDialog = false }
            )
        }

        if (showAboutDialog) {
            AboutDialog(
                onDismiss = { showAboutDialog = false }
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
private fun DividerSpacer() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 6.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
    )
}

@Composable
private fun LanguageDialog(
    selectedLanguage: String,
    onSelect: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf(
        "English" to "en",
        "Russian" to "ru",
        "Spanish" to "es"
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
                        onClick = { onSelect(name, code) },
                        shape = MaterialTheme.shapes.large,
                        color = if (selectedLanguage == name) {
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

                            if (selectedLanguage == name) {
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