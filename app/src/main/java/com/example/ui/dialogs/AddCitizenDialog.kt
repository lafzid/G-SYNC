package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.CitizenEntity
import com.example.ui.theme.BluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCitizenDialog(
    onDismiss: () -> Unit,
    onConfirm: (CitizenEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }

    val rtList = listOf("RT 01", "RT 02", "RT 03", "RT 04", "RT 05")
    var selectedRt by remember { mutableStateOf(rtList[0]) }
    var rtExpanded by remember { mutableStateOf(false) }

    var houseNumber by remember { mutableStateOf("Blok A1 No. ") }
    var phoneNumber by remember { mutableStateOf("0812-") }
    var membersCountText by remember { mutableStateOf("4") }

    val domisiliOptions = listOf("Tetap", "Kontrak", "Kost")
    var selectedDomisili by remember { mutableStateOf(domisiliOptions[0]) }
    var isLocked by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tambah Data Warga RW 26",
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Kepala Keluarga / Warga") },
                    placeholder = { Text("Contoh: Bpk. Bambang Sutrisno") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("citizen_name_input")
                )

                ExposedDropdownMenuBox(
                    expanded = rtExpanded,
                    onExpandedChange = { rtExpanded = !rtExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedRt,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Rukun Tetangga (RT)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rtExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = rtExpanded,
                        onDismissRequest = { rtExpanded = false }
                    ) {
                        rtList.forEach { rt ->
                            DropdownMenuItem(
                                text = { Text(rt) },
                                onClick = {
                                    selectedRt = rt
                                    rtExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = houseNumber,
                    onValueChange = { houseNumber = it },
                    label = { Text("Nomor Rumah / Blok") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Nomor HP / WhatsApp") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = membersCountText,
                    onValueChange = { membersCountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Jumlah Anggota Keluarga (Jiwa)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Status Domisili", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    domisiliOptions.forEach { dom ->
                        FilterChip(
                            selected = selectedDomisili == dom,
                            onClick = { selectedDomisili = dom },
                            label = { Text(dom) }
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLocked) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, if (isLocked) Color(0xFF93C5FD) else Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (isLocked) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "Kunci Nama Warga",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Proteksi data dari penghapusan tidak sengaja",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isLocked,
                            onCheckedChange = { isLocked = it },
                            modifier = Modifier.testTag("lock_citizen_switch")
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val citizen = CitizenEntity(
                            name = name,
                            rt = selectedRt,
                            houseNumber = houseNumber,
                            phoneNumber = phoneNumber,
                            familyMembersCount = membersCountText.toIntOrNull() ?: 1,
                            statusDomisili = selectedDomisili,
                            isLocked = isLocked
                        )
                        onConfirm(citizen)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("save_citizen_button")
            ) {
                Text("Simpan Data")
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
