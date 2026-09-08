package com.example.ui.dialogs

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.WargaRepository
import com.example.data.model.DuesEntity
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.StatusPaidColor
import com.example.ui.theme.StatusUnpaidColor
import com.example.util.DuesPdfGenerator

@Composable
fun ExportDuesPdfDialog(
    duesList: List<DuesEntity>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Discover distinct periods in duesList
    val discoveredPeriods = remember(duesList) {
        listOf("Semua Periode") + duesList.map { it.periodMonth }.distinct().filter { it.isNotBlank() }
    }
    var selectedPeriod by remember { mutableStateOf(discoveredPeriods.getOrElse(1) { "Semua Periode" }) }

    val rtOptions = listOf("Semua RT", "RT 01", "RT 02", "RT 03", "RT 04", "RT 05")
    var selectedRt by remember { mutableStateOf(rtOptions[0]) }

    val statusOptions = listOf("Semua Status", "Lunas", "Belum Lunas")
    var selectedStatus by remember { mutableStateOf(statusOptions[0]) }

    var ketuaName by remember { mutableStateOf("H. Didin jaenudin") }
    var bendaharaName by remember { mutableStateOf("Lahri supriatna") }

    // Live preview of matching dues
    val matchingDues = remember(duesList, selectedPeriod, selectedRt, selectedStatus) {
        duesList.filter { dues ->
            val matchPeriod = selectedPeriod == "Semua Periode" || dues.periodMonth.contains(selectedPeriod, ignoreCase = true)
            val matchRt = selectedRt == "Semua RT" || dues.rt.contains(selectedRt, ignoreCase = true)
            val matchStatus = selectedStatus == "Semua Status" || dues.status.equals(selectedStatus, ignoreCase = true)
            matchPeriod && matchRt && matchStatus
        }
    }

    val totalKas = matchingDues.filter { it.status.equals("Lunas", ignoreCase = true) }.sumOf { it.amount }
    val totalTunggakan = matchingDues.filter { it.status.equals("Belum Lunas", ignoreCase = true) }.sumOf { it.amount }

    fun doExport(action: String) {
        try {
            val config = DuesPdfGenerator.PdfConfig(
                periodFilter = selectedPeriod,
                rtFilter = selectedRt,
                statusFilter = selectedStatus,
                ketuaName = ketuaName.ifBlank { "H. Didin jaenudin" },
                bendaharaName = bendaharaName.ifBlank { "Lahri supriatna" }
            )
            val pdfFile = DuesPdfGenerator.generateDuesPdf(context, duesList, config)
            DuesPdfGenerator.openOrSharePdf(context, pdfFile, action)
            Toast.makeText(context, "Dokumen PDF berhasil dibuat!", Toast.LENGTH_SHORT).show()
            onDismiss()
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membuat PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("export_pdf_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE53935).copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Cetak & Ekspor Laporan PDF",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Format resmi kas & iuran RW 26",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                // Matching Stats Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Ringkasan Dokumen yang Akan Dicetak:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${matchingDues.size} Transaksi Iuran",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Terkumpul: ${WargaRepository.formatRupiah(totalKas)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = StatusPaidColor
                            )
                        }
                    }
                }

                // Period Filter
                Text("Periode Pembayaran", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    discoveredPeriods.forEach { period ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { selectedPeriod = period },
                            label = { Text(period, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // RT Filter
                Text("Wilayah Rukun Tetangga (RT)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rtOptions.forEach { rt ->
                        FilterChip(
                            selected = selectedRt == rt,
                            onClick = { selectedRt = rt },
                            label = { Text(rt, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Status Filter
                Text("Status Pembayaran", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statusOptions.forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                            label = { Text(status, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Signatures configuration
                Text("Penandatangan Dokumen Resmi", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = ketuaName,
                    onValueChange = { ketuaName = it },
                    label = { Text("Nama Ketua RW 26") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bendaharaName,
                    onValueChange = { bendaharaName = it },
                    label = { Text("Nama Bendahara RW 26") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { doExport("share") },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_pdf_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bagikan")
                }

                Button(
                    onClick = { doExport("view") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("print_pdf_button")
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cetak / Buka", fontWeight = FontWeight.Bold)
                }
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
