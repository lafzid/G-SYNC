package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.WargaRepository
import com.example.data.model.ActivityReportEntity
import com.example.ui.components.ActivityItemCard
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.LavenderSecondary

@Composable
fun ActivitiesScreen(
    reportsList: List<ActivityReportEntity>,
    categoryFilter: String,
    statusFilter: String,
    onCategoryFilterChange: (String) -> Unit,
    onStatusFilterChange: (String) -> Unit,
    onOpenAddActivity: () -> Unit,
    onOpenReportDetail: (ActivityReportEntity) -> Unit,
    onDeleteReport: (ActivityReportEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalAttendees = reportsList.sumOf { it.attendeesCount }
    val totalBudget = reportsList.sumOf { it.budgetSpent }

    val categories = listOf(
        "Semua",
        "Kerja Bakti",
        "Ronda Malam",
        "Musyawarah RW",
        "Posyandu",
        "Senam Sehat",
        "Bakti Sosial",
        "Pembangunan"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddActivity,
                containerColor = BluePrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("add_activity_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Buat Laporan Baru")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Stats Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Laporan Kegiatan Lingkungan RW 26",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LavenderSecondary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Total Partisipasi Warga",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$totalAttendees Warga Hadir",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Total Biaya Kas Terpakai",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = WargaRepository.formatRupiah(totalBudget),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Status Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Semua", "Selesai", "Sedang Berjalan", "Rencana").forEach { status ->
                        FilterChip(
                            selected = statusFilter == status,
                            onClick = { onStatusFilterChange(status) },
                            shape = RoundedCornerShape(20.dp),
                            label = { Text(status, style = MaterialTheme.typography.labelSmall, fontWeight = if (statusFilter == status) FontWeight.Bold else FontWeight.Normal) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Category Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = categoryFilter == cat,
                            onClick = { onCategoryFilterChange(cat) },
                            shape = RoundedCornerShape(20.dp),
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // List of Activities
            if (reportsList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Belum ada laporan kegiatan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Ketuk tombol + untuk menambahkan dokumentasi kegiatan RW 26",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                items(reportsList) { report ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ActivityItemCard(
                            report = report,
                            onClick = onOpenReportDetail,
                            onDelete = onDeleteReport
                        )
                    }
                }
            }
        }
    }
}
