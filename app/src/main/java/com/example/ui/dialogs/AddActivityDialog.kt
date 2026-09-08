package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.data.model.ActivityReportEntity
import com.example.ui.theme.BluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit,
    onConfirm: (ActivityReportEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }

    val categories = listOf(
        "Kerja Bakti",
        "Ronda Malam",
        "Musyawarah RW",
        "Posyandu",
        "Senam Sehat",
        "Bakti Sosial",
        "Pembangunan"
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var timeString by remember { mutableStateOf("07:30 - 11:00 WIB") }
    var location by remember { mutableStateOf("Balai Warga RW 26") }
    var coordinator by remember { mutableStateOf("Ketua RT / Pengurus RW 26") }
    var description by remember { mutableStateOf("") }
    var attendeesText by remember { mutableStateOf("45") }
    var budgetText by remember { mutableStateOf("250000") }

    val statuses = listOf("Selesai", "Sedang Berjalan", "Rencana")
    var selectedStatus by remember { mutableStateOf(statuses[0]) }
    var documentationNote by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Buat Laporan Kegiatan RW 26",
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
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Kegiatan Lingkungan") },
                    placeholder = { Text("Contoh: Kerja Bakti Bersih Got RT 01-04") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activity_title_input")
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori Kegiatan") },
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
                                }
                            )
                        }
                    }
                }

                // Status Chips
                Text("Status Pelaksanaan", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statuses.forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                            label = { Text(status) }
                        )
                    }
                }

                OutlinedTextField(
                    value = timeString,
                    onValueChange = { timeString = it },
                    label = { Text("Waktu Pelaksanaan") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokasi / Tempat") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = coordinator,
                    onValueChange = { coordinator = it },
                    label = { Text("Koordinator / Penanggung Jawab") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = attendeesText,
                        onValueChange = { attendeesText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Peserta Hadir") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = budgetText,
                        onValueChange = { budgetText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Biaya Kas (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.2f)
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi & Hasil Kegiatan") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = documentationNote,
                    onValueChange = { documentationNote = it },
                    label = { Text("Catatan Tambahan / Dokumentasi") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val report = ActivityReportEntity(
                            title = title,
                            category = selectedCategory,
                            dateMillis = System.currentTimeMillis(),
                            timeString = timeString,
                            location = location,
                            coordinator = coordinator,
                            description = description,
                            budgetSpent = budgetText.toLongOrNull() ?: 0L,
                            attendeesCount = attendeesText.toIntOrNull() ?: 0,
                            status = selectedStatus,
                            documentationNote = documentationNote
                        )
                        onConfirm(report)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("save_activity_button")
            ) {
                Text("Simpan Laporan")
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
