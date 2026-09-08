package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.WargaRepository
import com.example.data.model.DuesEntity
import com.example.ui.theme.BluePrimary

@Composable
fun QuickPayDialog(
    dues: DuesEntity,
    onDismiss: () -> Unit,
    onConfirm: (paymentMethod: String) -> Unit
) {
    val paymentMethods = listOf("Tunai", "Transfer Bank", "QRIS RW")
    var selectedMethod by remember { mutableStateOf(paymentMethods[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Konfirmasi Pembayaran Iuran",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Konfirmasi pelunasan iuran untuk:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${dues.citizenName} (${dues.rt} - ${dues.houseNumber})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${dues.category} • ${dues.periodMonth}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Nominal: ${WargaRepository.formatRupiah(dues.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = BluePrimary
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pilih Metode Pembayaran:",
                    style = MaterialTheme.typography.labelMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    paymentMethods.forEach { method ->
                        FilterChip(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            shape = RoundedCornerShape(20.dp),
                            label = { Text(method) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMethod) },
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("confirm_pay_button")
            ) {
                Text("Tandai Lunas")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Batal")
            }
        }
    )
}
