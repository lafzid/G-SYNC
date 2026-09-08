package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.WargaRepository
import com.example.data.model.ActivityReportEntity
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.LavenderOnSecondaryContainer
import com.example.ui.theme.LavenderSecondary
import com.example.ui.theme.LavenderSecondaryContainer
import com.example.ui.theme.StatusPaidColor
import com.example.ui.theme.StatusPlanColor
import com.example.ui.theme.StatusProgressColor
import com.example.ui.theme.StatusUnpaidColor

@Composable
fun ReportDetailDialog(
    report: ActivityReportEntity,
    onDismiss: () -> Unit
) {
    val (statusColor, statusBg) = when (report.status) {
        "Selesai" -> Pair(StatusPaidColor, Color(0xFFDCFCE7))
        "Sedang Berjalan" -> Pair(StatusProgressColor, Color(0xFFE0F2FE))
        "Rencana" -> Pair(StatusPlanColor, Color(0xFFFEF3C7))
        else -> Pair(StatusUnpaidColor, Color(0xFFFFDAD6))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(LavenderSecondaryContainer, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = report.category,
                            color = LavenderOnSecondaryContainer,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(statusBg, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = report.status,
                            color = statusColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = report.title,
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Info rows
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "${WargaRepository.formatDate(report.dateMillis)} • ${report.timeString}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                    Text(text = report.location, style = MaterialTheme.typography.bodyMedium)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                    Text(text = "Koordinator: ${report.coordinator}", style = MaterialTheme.typography.bodyMedium)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                    Text(text = "Kehadiran Warga: ${report.attendeesCount} Orang", style = MaterialTheme.typography.bodyMedium)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Biaya Kas Terpakai: ${WargaRepository.formatRupiah(report.budgetSpent)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Text(
                    text = "Laporan & Hasil Kegiatan:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = report.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (report.documentationNote.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Catatan Dokumentasi:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = report.documentationNote,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Tutup")
            }
        }
    )
}
