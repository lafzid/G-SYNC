package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.WargaRepository
import com.example.data.model.ActivityReportEntity
import com.example.data.model.DuesEntity
import com.example.data.model.RondaGroupEntity
import com.example.ui.FinancialStats
import com.example.ui.ScreenTab
import com.example.ui.components.ActivityItemCard
import com.example.ui.components.DuesItemCard
import com.example.ui.components.StatCard
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.LavenderOnSecondaryContainer
import com.example.ui.theme.LavenderSecondary
import com.example.ui.theme.LavenderSecondaryContainer
import com.example.ui.theme.StatusPaidColor
import com.example.ui.theme.StatusUnpaidColor
import androidx.compose.material.icons.filled.Security

@Composable
fun HomeScreen(
    stats: FinancialStats,
    duesList: List<DuesEntity>,
    activitiesList: List<ActivityReportEntity>,
    todayRondaGroup: RondaGroupEntity? = null,
    onNavigateTab: (ScreenTab) -> Unit,
    onOpenAddDues: () -> Unit,
    onOpenAddActivity: () -> Unit,
    onOpenReceipt: (DuesEntity) -> Unit,
    onQuickPay: (DuesEntity) -> Unit,
    onOpenReportDetail: (ActivityReportEntity) -> Unit,
    onDeleteDues: (DuesEntity) -> Unit,
    onDeleteActivity: (ActivityReportEntity) -> Unit,
    onOpenExportPdf: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pendingDues = duesList.filter { it.status.equals("Belum Lunas", ignoreCase = true) }.take(3)
    val recentActivities = activitiesList.take(3)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card with Community Illustration
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, CardBorderStroke),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.rw_community_hero),
                            contentDescription = "Kegiatan Gotong Royong Warga RW 26",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "WARGA RW 26 DIGITAL",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Transparansi Iuran Kas & Laporan Kegiatan Warga",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    // Quick Action Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = onOpenAddDues,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_add_dues_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Catat Iuran", style = MaterialTheme.typography.labelMedium)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        OutlinedButton(
                            onClick = onOpenAddActivity,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, CardBorderStroke),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_add_activity_btn")
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Lapor Kegiatan", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // Financial Overview Grid
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Ringkasan Kas & Partisipasi Warga",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Row 1: Saldo Kas & Iuran Terkumpul
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Saldo Kas RW 26",
                        value = WargaRepository.formatRupiah(stats.netBalance),
                        subtitle = "Kas operasional aktif",
                        icon = Icons.Default.AccountBalanceWallet,
                        containerColor = Color(0xFFEAF1FB),
                        contentColor = BluePrimary,
                        iconBgColor = Color(0xFFD3E3FD),
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Iuran Terkumpul",
                        value = WargaRepository.formatRupiah(stats.totalCollected),
                        subtitle = "${stats.paidCount} Transaksi Lunas",
                        icon = Icons.Default.CheckCircle,
                        containerColor = Color(0xFFE8F5E9),
                        contentColor = StatusPaidColor,
                        iconBgColor = Color(0xFFC8E6C9),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Biaya Kegiatan & Belum Lunas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Biaya Kegiatan",
                        value = WargaRepository.formatRupiah(stats.totalSpent),
                        subtitle = "Realisasi anggaran warga",
                        icon = Icons.Default.EventAvailable,
                        containerColor = Color(0xFFF3EDF7),
                        contentColor = LavenderSecondary,
                        iconBgColor = LavenderSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Tunggakan Iuran",
                        value = WargaRepository.formatRupiah(stats.totalPending),
                        subtitle = "${stats.pendingCount} Belum Lunas",
                        icon = Icons.Default.HourglassTop,
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = StatusUnpaidColor,
                        iconBgColor = Color(0xFFFFCDD2),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Quick Export PDF Banner for Monthly Reports
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Laporan Bulanan Kas Siap Cetak",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = onOpenExportPdf,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("home_export_pdf_btn")
                        ) {
                            Text("Cetak PDF", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: Jadwal Ronda Malam Ini (Highlighted Card)
        if (todayRondaGroup != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BluePrimary.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, BluePrimary.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(BluePrimary, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Ronda Malam Ini (${todayRondaGroup.dayOfWeek})",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary
                                    )
                                    Text(
                                        text = todayRondaGroup.groupName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            TextButton(onClick = { onNavigateTab(ScreenTab.RONDA) }) {
                                Text("Buka Ronda", fontWeight = FontWeight.Bold, color = BluePrimary)
                            }
                        }

                        Text(
                            text = "Koordinator: ${todayRondaGroup.coordinatorName} • ${todayRondaGroup.shiftHours}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section: Tunggakan / Perlu Bayar
        if (pendingDues.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Iuran Menunggu Pembayaran",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { onNavigateTab(ScreenTab.DUES) }) {
                        Text("Lihat Semua (${stats.pendingCount})", color = BluePrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            items(pendingDues) { dues ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    DuesItemCard(
                        dues = dues,
                        onViewReceipt = onOpenReceipt,
                        onQuickPay = onQuickPay,
                        onDelete = onDeleteDues
                    )
                }
            }
        }

        // Section: Kegiatan Lingkungan Terbaru
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kegiatan Lingkungan Terkini",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { onNavigateTab(ScreenTab.ACTIVITIES) }) {
                    Text("Semua Kegiatan", color = BluePrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        items(recentActivities) { report ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                ActivityItemCard(
                    report = report,
                    onClick = onOpenReportDetail,
                    onDelete = onDeleteActivity
                )
            }
        }
    }
}
