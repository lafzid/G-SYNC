package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
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
import com.example.data.model.RondaGroupEntity
import com.example.data.model.RondaScheduleEntity
import com.example.ui.theme.BluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRondaScheduleDialog(
    groupsList: List<RondaGroupEntity>,
    onDismiss: () -> Unit,
    onSave: (RondaScheduleEntity) -> Unit
) {
    val statuses = listOf("Sedang Bertugas", "Terjadwal", "Selesai")

    var selectedGroup by remember {
        mutableStateOf(groupsList.firstOrNull())
    }
    var groupDropdownExpanded by remember { mutableStateOf(false) }

    var selectedStatus by remember { mutableStateOf("Sedang Bertugas") }
    var securityNotes by remember {
        mutableStateOf("Pos ronda dibuka. Portal utama dan portal lingkungan siap dijaga secara bergantian.")
    }
    var incidentReport by remember { mutableStateOf("Nihil kejadian.") }
    var attendanceSummary by remember {
        mutableStateOf(
            if (selectedGroup != null) "${selectedGroup?.members?.split(",")?.size ?: 0} Petugas Terjadwal" else "Siap di Pos"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Catat Jadwal / Laporan Ronda",
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
                // Group selector
                ExposedDropdownMenuBox(
                    expanded = groupDropdownExpanded,
                    onExpandedChange = { groupDropdownExpanded = !groupDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedGroup?.let { "${it.groupName} (${it.dayOfWeek})" } ?: "Pilih Regu",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Regu Ronda *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupDropdownExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("ronda_schedule_group_dropdown")
                    )

                    ExposedDropdownMenu(
                        expanded = groupDropdownExpanded,
                        onDismissRequest = { groupDropdownExpanded = false }
                    ) {
                        groupsList.forEach { group ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("${group.groupName} • ${group.dayOfWeek}", fontWeight = FontWeight.SemiBold)
                                        Text("Koordinator: ${group.coordinatorName}", style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = {
                                    selectedGroup = group
                                    groupDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Status Selector
                Text(
                    text = "Status Tugas Ronda:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    statuses.forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                            shape = RoundedCornerShape(16.dp),
                            label = { Text(status) }
                        )
                    }
                }

                OutlinedTextField(
                    value = attendanceSummary,
                    onValueChange = { attendanceSummary = it },
                    label = { Text("Ringkasan Kehadiran Petugas") },
                    placeholder = { Text("Contoh: 4 Petugas Hadir di Pos") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = securityNotes,
                    onValueChange = { securityNotes = it },
                    label = { Text("Catatan Situasi Patroli & Keamanan") },
                    placeholder = { Text("Kondisi lingkungan, jam penutupan portal, dll.") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = incidentReport,
                    onValueChange = { incidentReport = it },
                    label = { Text("Laporan Insiden / Kejadian (Bila Ada)") },
                    placeholder = { Text("Nihil kejadian atau rincian laporan warga") },
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val group = selectedGroup
                    if (group != null) {
                        val schedule = RondaScheduleEntity(
                            id = 0L,
                            groupId = group.id,
                            groupName = group.groupName,
                            dateMillis = System.currentTimeMillis(),
                            dayOfWeek = group.dayOfWeek,
                            status = selectedStatus,
                            checkedInMembers = if (selectedStatus == "Sedang Bertugas" || selectedStatus == "Selesai") group.members else "",
                            attendanceSummary = attendanceSummary.trim(),
                            securityNotes = securityNotes.trim(),
                            incidentReport = incidentReport.trim()
                        )
                        onSave(schedule)
                    }
                },
                enabled = selectedGroup != null,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("save_ronda_schedule_button")
            ) {
                Text("Simpan Jadwal")
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
