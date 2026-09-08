package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RondaGroupEntity
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.StatusPlanBg
import com.example.ui.theme.StatusPlanColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RondaGroupDetailDialog(
    group: RondaGroupEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val membersList = group.members.split(",").map { it.trim() }.filter { it.isNotBlank() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(BluePrimary.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = group.groupName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Jadwal Tugas: ${group.dayOfWeek}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StatusPlanBg
                ) {
                    Text(
                        text = group.dayOfWeek,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StatusPlanColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Info Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("🕒 Jam Shift: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text(group.shiftHours, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("📍 Pos Pantau: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text(group.postLocation, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("🗺️ Wilayah Target: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text(group.targetZone, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // Koordinator Card with Call button
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BluePrimary.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(BluePrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Ketua Regu / Koordinator", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(group.coordinatorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                if (group.coordinatorPhone.isNotBlank()) {
                                    Text(group.coordinatorPhone, style = MaterialTheme.typography.bodySmall, color = BluePrimary)
                                }
                            }
                        }

                        if (group.coordinatorPhone.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${group.coordinatorPhone}")
                                    }
                                    context.startActivity(dialIntent)
                                }
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Hubungi Koordinator", tint = BluePrimary)
                            }
                        }
                    }
                }

                // Member list
                Column {
                    Text(
                        text = "Daftar Anggota Petugas (${membersList.size} Warga):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        membersList.forEach { member ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Text(
                                    text = member,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

                // Equipment Notes
                Column {
                    Text(
                        text = "Inventaris & Perlengkapan Pos:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = group.equipmentNotes.ifEmpty { "Perlengkapan standar pos kamling (senter, rompi, pentungan)." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("edit_ronda_group_button")
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit Regu")
            }
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Regu",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Tutup")
                }
            }
        }
    )
}
