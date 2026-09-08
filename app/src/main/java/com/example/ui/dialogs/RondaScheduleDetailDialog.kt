package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.CheckCircleOutline
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WargaRepository
import com.example.data.model.RondaGroupEntity
import com.example.data.model.RondaScheduleEntity
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.StatusCompletedBg
import com.example.ui.theme.StatusCompletedColor
import com.example.ui.theme.StatusPendingBg
import com.example.ui.theme.StatusPendingColor
import com.example.ui.theme.StatusProgressBg
import com.example.ui.theme.StatusProgressColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RondaScheduleDetailDialog(
    schedule: RondaScheduleEntity,
    group: RondaGroupEntity?,
    onDismiss: () -> Unit,
    onStatusChange: (String) -> Unit,
    onToggleMemberCheckIn: (String) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val formattedDate = WargaRepository.formatDate(schedule.dateMillis)

    val (statusColor, statusBg) = when (schedule.status) {
        "Selesai" -> StatusCompletedColor to StatusCompletedBg
        "Sedang Bertugas" -> StatusProgressColor to StatusProgressBg
        else -> StatusPendingColor to StatusPendingBg
    }

    val membersList = (group?.members ?: schedule.checkedInMembers)
        .split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }

    val checkedInList = schedule.checkedInMembers
        .split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }

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
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = schedule.groupName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${schedule.dayOfWeek}, $formattedDate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusBg
                ) {
                    Text(
                        text = schedule.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
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
                            Text("📍 Pos Pantau: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text(group?.postLocation ?: "Pos Kamling RW 26", style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("👤 Koordinator: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text(group?.coordinatorName ?: "-", style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("🕒 Jam Tugas: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text(group?.shiftHours ?: "22:00 - 04:00 WIB", style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("🗺️ Zona: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text(group?.targetZone ?: "Wilayah RW 26", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // Interactive Absensi Petugas
                Text(
                    text = "Absensi & Kehadiran Petugas Ronda:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Klik nama petugas untuk menandai kehadiran di pos kamling:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    membersList.forEach { member ->
                        val isPresent = checkedInList.contains(member)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isPresent) StatusCompletedBg else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isPresent) StatusCompletedColor else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.clickable {
                                onToggleMemberCheckIn(member)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPresent) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = if (isPresent) StatusCompletedColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = member,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isPresent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isPresent) StatusCompletedColor else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

                // Notes
                Column {
                    Text(
                        text = "Catatan Situasi Patroli:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = schedule.securityNotes.ifEmpty { "Belum ada catatan keamanan." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Incident Report
                Column {
                    Text(
                        text = "Laporan Insiden & Kejadian:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = schedule.incidentReport.ifEmpty { "Nihil kejadian mencurigakan." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Fast Status Switcher
                Text(
                    text = "Perbarui Status Ronda:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onStatusChange("Sedang Bertugas") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mulai Bertugas", fontSize = 11.sp)
                    }
                    Button(
                        onClick = { onStatusChange("Selesai") },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusCompletedColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tandai Selesai", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val reportText = WargaRepository.generateRondaReportText(schedule, group)
                    // Share via Intent
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, reportText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Bagikan Laporan Ronda RW 26")
                    context.startActivity(shareIntent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("share_ronda_report_button")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Kirim ke WA")
            }
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Jadwal",
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
