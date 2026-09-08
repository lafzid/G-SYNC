package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import com.example.data.model.RondaGroupEntity
import com.example.data.model.RondaScheduleEntity
import com.example.ui.RondaSubTab
import com.example.ui.components.RondaGroupCard
import com.example.ui.components.RondaScheduleCard
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.StatusCompletedBg
import com.example.ui.theme.StatusCompletedColor
import com.example.ui.theme.StatusPendingBg
import com.example.ui.theme.StatusPendingColor
import com.example.ui.theme.StatusProgressBg
import com.example.ui.theme.StatusProgressColor

@Composable
fun RondaScreen(
    currentSubTab: RondaSubTab,
    onSubTabChange: (RondaSubTab) -> Unit,
    todayGroup: RondaGroupEntity?,
    groupsList: List<RondaGroupEntity>,
    schedulesList: List<RondaScheduleEntity>,
    dayFilter: String,
    statusFilter: String,
    searchQuery: String,
    onDayFilterChange: (String) -> Unit,
    onStatusFilterChange: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenAddGroup: () -> Unit,
    onOpenAddSchedule: () -> Unit,
    onOpenGroupDetail: (RondaGroupEntity) -> Unit,
    onOpenScheduleDetail: (RondaScheduleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf("Semua", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    val statuses = listOf("Semua", "Sedang Bertugas", "Terjadwal", "Selesai")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (currentSubTab == RondaSubTab.GROUPS) {
                        onOpenAddGroup()
                    } else {
                        onOpenAddSchedule()
                    }
                },
                containerColor = BluePrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("ronda_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (currentSubTab == RondaSubTab.GROUPS) "Tambah Regu Ronda" else "Catat Jadwal Ronda"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Banner Card with Illustration
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_ronda_banner),
                                contentDescription = "Siskamling & Ronda RW 26",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.65f)
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(14.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BluePrimary
                                ) {
                                    Text(
                                        text = "SISKAMLING & KEAMANAN",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Jadwal Ronda & Regu Patroli RW 26",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Summary mini-stats
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${groupsList.size}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary
                                )
                                Text(
                                    text = "Regu Ronda",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(30.dp)
                                    .background(CardBorderStroke)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val totalOfficers = groupsList.flatMap { it.members.split(",") }.map { it.trim() }.filter { it.isNotBlank() }.distinct().size
                                Text(
                                    text = "$totalOfficers",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Petugas Aktif",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(30.dp)
                                    .background(CardBorderStroke)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val activeCount = schedulesList.count { it.status == "Sedang Bertugas" }
                                Text(
                                    text = if (activeCount > 0) "$activeCount Aktif" else "Siaga",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeCount > 0) StatusProgressColor else StatusCompletedColor
                                )
                                Text(
                                    text = "Status Pos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Today's Patrol Highlight Card
            if (todayGroup != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BluePrimary.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, BluePrimary.copy(alpha = 0.4f))
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
                                    Icon(
                                        Icons.Default.Security,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Bertugas Malam Ini (${todayGroup.dayOfWeek}):",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = BluePrimary
                                ) {
                                    Text(
                                        text = todayGroup.groupName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Text(
                                text = "👤 Koordinator: ${todayGroup.coordinatorName} ${if (todayGroup.coordinatorPhone.isNotBlank()) "(${todayGroup.coordinatorPhone})" else ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "📍 ${todayGroup.postLocation} • ${todayGroup.shiftHours}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = onOpenAddSchedule,
                                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Buka Laporan Pos", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // SubTab Row (Jadwal & Laporan vs Regu Ronda)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    TabRow(
                        selectedTabIndex = if (currentSubTab == RondaSubTab.SCHEDULES) 0 else 1,
                        containerColor = Color.Transparent,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[if (currentSubTab == RondaSubTab.SCHEDULES) 0 else 1]),
                                color = BluePrimary
                            )
                        }
                    ) {
                        Tab(
                            selected = currentSubTab == RondaSubTab.SCHEDULES,
                            onClick = { onSubTabChange(RondaSubTab.SCHEDULES) },
                            text = {
                                Text(
                                    text = "Jadwal & Laporan (${schedulesList.size})",
                                    fontWeight = if (currentSubTab == RondaSubTab.SCHEDULES) FontWeight.Bold else FontWeight.Normal,
                                    color = if (currentSubTab == RondaSubTab.SCHEDULES) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.testTag("ronda_tab_schedules")
                        )
                        Tab(
                            selected = currentSubTab == RondaSubTab.GROUPS,
                            onClick = { onSubTabChange(RondaSubTab.GROUPS) },
                            text = {
                                Text(
                                    text = "Daftar Regu (${groupsList.size})",
                                    fontWeight = if (currentSubTab == RondaSubTab.GROUPS) FontWeight.Bold else FontWeight.Normal,
                                    color = if (currentSubTab == RondaSubTab.GROUPS) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.testTag("ronda_tab_groups")
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("ronda_search_input"),
                    placeholder = {
                        Text(
                            if (currentSubTab == RondaSubTab.SCHEDULES) "Cari nama regu, catatan patroli..." else "Cari nama regu, koordinator, anggota..."
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Cari Ronda", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Hapus")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            // Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Day of Week Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        daysOfWeek.forEach { day ->
                            FilterChip(
                                selected = dayFilter == day,
                                onClick = { onDayFilterChange(day) },
                                shape = RoundedCornerShape(20.dp),
                                label = { Text(day) }
                            )
                        }
                    }

                    // Status Filter Chips (only in Schedules tab)
                    if (currentSubTab == RondaSubTab.SCHEDULES) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            statuses.forEach { status ->
                                FilterChip(
                                    selected = statusFilter == status,
                                    onClick = { onStatusFilterChange(status) },
                                    shape = RoundedCornerShape(20.dp),
                                    label = { Text(status) }
                                )
                            }
                        }
                    }
                }
            }

            // Content List depending on SubTab
            if (currentSubTab == RondaSubTab.SCHEDULES) {
                if (schedulesList.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Belum Ada Jadwal / Laporan Ronda",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Klik tombol '+' di bawah untuk mencatat jadwal atau laporan pos ronda malam ini.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(schedulesList, key = { it.id }) { schedule ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            RondaScheduleCard(
                                schedule = schedule,
                                onClick = { onOpenScheduleDetail(schedule) }
                            )
                        }
                    }
                }
            } else {
                // Groups Tab
                if (groupsList.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Groups,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Belum Ada Regu Ronda",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Klik tombol '+' untuk menambah pembagian regu patroli warga.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(groupsList, key = { it.id }) { group ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            RondaGroupCard(
                                group = group,
                                isTodayGroup = todayGroup?.id == group.id,
                                onClick = { onOpenGroupDetail(group) }
                            )
                        }
                    }
                }
            }
        }
    }
}
