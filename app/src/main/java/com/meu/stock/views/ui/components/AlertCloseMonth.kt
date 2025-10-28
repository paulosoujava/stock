package com.meu.stock.views.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import java.time.Month

@Composable
fun AlertCloseMonth(
    onDismissRequest: () -> Unit,
    onConfirmCloseMonth: () -> Unit,
    onDismissCloseMonthDialog: () -> Unit,
    month: String
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { Icon(Icons.Default.Warning, contentDescription = null) },
        title = { Text("Fechar o Mês") },
        text = { Text("Você tem certeza que deseja fechar ${month}? Esta ação não pode ser desfeita.") },
        confirmButton = {
            Button(onClick = onConfirmCloseMonth) {
                Text("Sim, Fechar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissCloseMonthDialog) {
                Text("Cancelar")
            }
        }
    )
}