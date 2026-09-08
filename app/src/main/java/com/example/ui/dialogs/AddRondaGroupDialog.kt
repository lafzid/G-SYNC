package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CitizenEntity
import com.example.data.model.RondaGroupEntity
import com.example.ui.theme.BluePrimary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddRondaGroupDialog(
    initialGroup: RondaGroupEntity? = null,
    citizensList: List<CitizenEntity>,
    onDismiss: () -> Unit,
    onSave: (RondaGroupEntity) -> Unit
) {
    val daysOfWeek = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

    var groupName by remember { mutableStateOf(initialGroup?.groupName ?: "") }
    var selectedDay by remember { mutableStateOf(initialGroup?.dayOfWeek ?: "Senin") }
    var shiftHours by remember { mutableStateOf(initialGroup?.shiftHours ?: "22:00 - 04:00 WIB") }
    var postLocation by remember { mutableStateOf(initialGroup?.postLocation ?: "Pos Kamling Utama RW 26") }
    var coordinatorName by remember { mutableStateOf(initialGroup?.coordinatorName ?: "") }
    var coordinatorPhone by remember { mutableStateOf(initialGroup?.coordinatorPhone ?: "") }
    var targetZone by remember { mutableStateOf(initialGroup?.targetZone ?: "Wilayah RW 26") }
    var equipmentNotes by remember { mutableStateOf(initialGroup?.equipmentNotes ?: "Senter patroli, rompi, HT, pentungan, P3K") }

    // Members list management
    val selectedMembers = remember {
        val initial = initialGroup?.members?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
        mutableStateOf(initial.toMutableList())
    }

    var coordinatorExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (initialGroup == null) "Tambah Regu Ronda" else "Edit Regu Ronda",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Nama Regu Ronda *") },
                    placeholder = { Text("Contoh: Regu Elang, Regu Garuda") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ronda_group_name_input")
                )

                // Day of Week Selector
                Text(
                    text = "Jadwal Hari Tugas *",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    daysOfWeek.forEach { day ->
                        FilterChip(
                            selected = selectedDay == day,
                            onClick = { selectedDay = day },
                            shape = RoundedCornerShape(16.dp),
                            label = { Text(day) }
                        )
                    }
                }

                OutlinedTextField(
                    value = shiftHours,
                    onValueChange = { shiftHours = it },
                    label = { Text("Jam Tugas / Shift") },
                    placeholder = { Text("22:00 - 04:00 WIB") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = postLocation,
                    onValueChange = { postLocation = it },
                    label = { Text("Lokasi Pos Pantau") },
                    placeholder = { Text("Pos Kamling Utama RW 26") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Coordinator Selector from Citizens
                ExposedDropdownMenuBox(
                    expanded = coordinatorExpanded,
                    onExpandedChange = { coordinatorExpanded = !coordinatorExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = coordinatorName,
                        onValueChange = { coordinatorName = it },
                        label = { Text("Koordinator / Ketua Regu *") },
                        placeholder = { Text("Pilih warga atau ketik nama") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = coordinatorExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("ronda_coordinator_input")
                    )

                    ExposedDropdownMenu(
                        expanded = coordinatorExpanded,
                        onDismissRequest = { coordinatorExpanded = false }
                    ) {
                        citizensList.forEach { citizen ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(citizen.name, fontWeight = FontWeight.SemiBold)
                                        Text("${citizen.rt} • ${citizen.houseNumber}", style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = {
                                    coordinatorName = citizen.name
                                    coordinatorPhone = citizen.phoneNumber
                                    coordinatorExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = coordinatorPhone,
                    onValueChange = { coordinatorPhone = it },
                    label = { Text("No. HP / WhatsApp Koordinator") },
                    placeholder = { Text("0812-xxxx-xxxx") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Select Members from Registered Citizens
                Text(
                    text = "Pilih Anggota Regu Petugas:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Klik nama warga untuk menambah/mengurangi anggota regu:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    citizensList.forEach { citizen ->
                        val isSelected = selectedMembers.value.contains(citizen.name)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val current = selectedMembers.value.toMutableList()
                                if (isSelected) {
                                    current.remove(citizen.name)
                                } else {
                                    current.add(citizen.name)
                                }
                                selectedMembers.value = current
                            },
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            label = { Text(citizen.name) }
                        )
                    }
                }

                OutlinedTextField(
                    value = targetZone,
                    onValueChange = { targetZone = it },
                    label = { Text("Wilayah Target Patroli") },
                    placeholder = { Text("RT 01, RT 02, Portal Barat") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = equipmentNotes,
                    onValueChange = { equipmentNotes = it },
                    label = { Text("Catatan Perlengkapan / Inventaris Pos") },
                    placeholder = { Text("Senter, HT, rompi, peluit, P3K") },
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalMembers = if (selectedMembers.value.isNotEmpty()) {
                        selectedMembers.value.joinToString(", ")
                    } else {
                        coordinatorName
                    }

                    val group = RondaGroupEntity(
                        id = initialGroup?.id ?: 0L,
                        groupName = groupName.trim(),
                        dayOfWeek = selectedDay,
                        shiftHours = shiftHours.trim(),
                        postLocation = postLocation.trim(),
                        coordinatorName = coordinatorName.trim(),
                        coordinatorPhone = coordinatorPhone.trim(),
                        members = finalMembers,
                        targetZone = targetZone.trim(),
                        equipmentNotes = equipmentNotes.trim()
                    )
                    onSave(group)
                },
                enabled = groupName.isNotBlank() && coordinatorName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("save_ronda_group_button")
            ) {
                Text("Simpan Regu")
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
