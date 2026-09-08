package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.CitizenEntity
import com.example.data.model.DuesEntity
import com.example.ui.theme.BluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDuesDialog(
    citizens: List<CitizenEntity>,
    onDismiss: () -> Unit,
    onConfirm: (DuesEntity) -> Unit
) {
    var selectedCitizen by remember { mutableStateOf<CitizenEntity?>(citizens.firstOrNull()) }
    var citizenExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Iuran Kebersihan & Sampah",
        "Keamanan & Ronda",
        "Kas RW 26",
        "Dana Sosial & Kematian",
        "Pembangunan Fasum"
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var amountText by remember { mutableStateOf("50000") }
    var periodMonth by remember { mutableStateOf("September 2026") }
    var isPaid by remember { mutableStateOf(true) }

    val paymentMethods = listOf("Tunai", "Transfer Bank", "QRIS RW")
    var selectedMethod by remember { mutableStateOf(paymentMethods[0]) }
    var notes by remember { mutableStateOf("") }

    var pendingReviewDues by remember { mutableStateOf<DuesEntity?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Catat Iuran Warga RW 26",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Citizen Selector
                ExposedDropdownMenuBox(
                    expanded = citizenExpanded,
                    onExpandedChange = { citizenExpanded = !citizenExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCitizen?.let { "${it.name} (${it.rt} - ${it.houseNumber})" } ?: "Pilih Warga / KK",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Nama Warga / Kepala Keluarga") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = citizenExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("citizen_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = citizenExpanded,
                        onDismissRequest = { citizenExpanded = false }
                    ) {
                        citizens.forEach { citizen ->
                            DropdownMenuItem(
                                text = { Text("${citizen.name} • ${citizen.rt} (${citizen.houseNumber})") },
                                onClick = {
                                    selectedCitizen = citizen
                                    citizenExpanded = false
                                }
                            )
                        }
                    }
                }

                // Category Selector
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jenis Iuran") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryExpanded = false
                                    // Pre-fill standard nominal based on category
                                    when (cat) {
                                        "Iuran Kebersihan & Sampah" -> amountText = "50000"
                                        "Keamanan & Ronda" -> amountText = "40000"
                                        "Kas RW 26" -> amountText = "30000"
                                        "Dana Sosial & Kematian" -> amountText = "25000"
                                        "Pembangunan Fasum" -> amountText = "100000"
                                    }
                                }
                            )
                        }
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Nominal Iuran (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input")
                )

                // Quick Nominal Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("25000", "35000", "50000", "100000").forEach { quickAmount ->
                        val label = when (quickAmount) {
                            "25000" -> "25 rb"
                            "35000" -> "35 rb"
                            "50000" -> "50 rb"
                            else -> "100 rb"
                        }
                        FilterChip(
                            selected = amountText == quickAmount,
                            onClick = { amountText = quickAmount },
                            label = { Text(label) }
                        )
                    }
                }

                // Period Month
                OutlinedTextField(
                    value = periodMonth,
                    onValueChange = { periodMonth = it },
                    label = { Text("Periode Bulan / Tahun") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Payment Status Chips
                Text("Status Pembayaran", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isPaid,
                        onClick = { isPaid = true },
                        label = { Text("Lunas") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isPaid,
                        onClick = { isPaid = false },
                        label = { Text("Belum Lunas") },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isPaid) {
                    Text("Metode Pembayaran", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        paymentMethods.forEach { method ->
                            FilterChip(
                                selected = selectedMethod == method,
                                onClick = { selectedMethod = method },
                                label = { Text(method) }
                            )
                        }
                    }
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan / Keterangan (Opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val citizen = selectedCitizen
                    val amount = amountText.toLongOrNull() ?: 0L
                    if (citizen != null && amount > 0L) {
                        val dues = DuesEntity(
                            citizenId = citizen.id,
                            citizenName = citizen.name,
                            rt = citizen.rt,
                            houseNumber = citizen.houseNumber,
                            category = selectedCategory,
                            amount = amount,
                            periodMonth = periodMonth,
                            status = if (isPaid) "Lunas" else "Belum Lunas",
                            paymentMethod = if (isPaid) selectedMethod else "-",
                            notes = notes
                        )
                        pendingReviewDues = dues
                    }
                },
                enabled = selectedCitizen != null && amountText.isNotBlank() && (amountText.toLongOrNull() ?: 0L) > 0L,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("save_dues_button")
            ) {
                Text("Tinjau & Simpan")
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

    // Summary Review Confirmation Dialog
    pendingReviewDues?.let { duesToReview ->
        DuesReviewConfirmationDialog(
            dues = duesToReview,
            onDismiss = {
                // Return to edit without discarding form state
                pendingReviewDues = null
            },
            onConfirm = {
                pendingReviewDues = null
                onConfirm(duesToReview)
            }
        )
    }
}
