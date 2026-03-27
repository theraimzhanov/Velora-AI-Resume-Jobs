package com.velora.mobile.presentation.tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.velora.mobile.R
import com.velora.mobile.domain.jobs.ApplicationStatus
import com.velora.mobile.presentation.ui.SoftChip

@Composable
 fun AddApplicationSheet(
    onAdd: (String, String, String) -> Unit,
    onClose: () -> Unit
) {
    var company by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ApplicationStatus.Applied.name) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.add_application), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            TextButton(onClick = onClose) { Text(stringResource(R.string.close)) }
        }

        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = company,
            onValueChange = { company = it },
            label = { Text(stringResource(R.string.company)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = position,
            onValueChange = { position = it },
            label = { Text(stringResource(R.string.position)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(Modifier.height(14.dp))

        Text(
            stringResource(R.string.status),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ApplicationStatus.entries.forEach { s ->
                SoftChip(
                    text = s.name,
                    selected = status == s.name,
                    onClick = { status = s.name }
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = { onAdd(company.trim(), position.trim(), status) },
            enabled = company.isNotBlank() && position.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D63FF),
                contentColor = Color.White
            )
        ) {
            Text(stringResource(R.string.add_application), fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(10.dp))
    }
}