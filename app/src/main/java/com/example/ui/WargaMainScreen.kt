package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.dialogs.AddActivityDialog
import com.example.ui.dialogs.AddCitizenDialog
import com.example.ui.dialogs.AddDuesDialog
import com.example.ui.dialogs.AddRondaGroupDialog
import com.example.ui.dialogs.AddRondaScheduleDialog
import com.example.ui.dialogs.ExportDuesPdfDialog
import com.example.ui.dialogs.QuickPayDialog
import com.example.ui.dialogs.ReceiptDialog
import com.example.ui.dialogs.ReportDetailDialog
import com.example.ui.dialogs.RondaGroupDetailDialog
import com.example.ui.dialogs.RondaScheduleDetailDialog
import com.example.ui.screens.ActivitiesScreen
import com.example.ui.screens.CitizensScreen
import com.example.ui.screens.DuesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RondaScreen
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.NavIndicatorColor
import com.example.ui.theme.NavSelectedColor
import com.example.ui.theme.NavUnselectedColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WargaMainScreen(
    viewModel: WargaViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val stats by viewModel.financialStats.collectAsStateWithLifecycle()
    val allCitizens by viewModel.allCitizens.collectAsStateWithLifecycle()
    val allDues by viewModel.allDues.collectAsStateWithLifecycle()

    val filteredDues by viewModel.filteredDues.collectAsStateWithLifecycle()
    val filteredReports by viewModel.filteredReports.collectAsStateWithLifecycle()
    val filteredCitizens by viewModel.filteredCitizens.collectAsStateWithLifecycle()

    val duesSearchQuery by viewModel.duesSearchQuery.collectAsStateWithLifecycle()
    val duesStatusFilter by viewModel.duesStatusFilter.collectAsStateWithLifecycle()
    val duesCategoryFilter by viewModel.duesCategoryFilter.collectAsStateWithLifecycle()

    val citizenRtFilter by viewModel.citizenRtFilter.collectAsStateWithLifecycle()
    val citizenLockFilter by viewModel.citizenLockFilter.collectAsStateWithLifecycle()
    val activityCategoryFilter by viewModel.activityCategoryFilter.collectAsStateWithLifecycle()
    val activityStatusFilter by viewModel.activityStatusFilter.collectAsStateWithLifecycle()

    // Ronda State
    val rondaSubTab by viewModel.rondaSubTab.collectAsStateWithLifecycle()
    val todayRondaGroup by viewModel.todayRondaGroup.collectAsStateWithLifecycle()
    val allRondaGroups by viewModel.allRondaGroups.collectAsStateWithLifecycle()
    val filteredRondaGroups by viewModel.filteredRondaGroups.collectAsStateWithLifecycle()
    val filteredRondaSchedules by viewModel.filteredRondaSchedules.collectAsStateWithLifecycle()
    val rondaDayFilter by viewModel.rondaDayFilter.collectAsStateWithLifecycle()
    val rondaStatusFilter by viewModel.rondaStatusFilter.collectAsStateWithLifecycle()
    val rondaSearchQuery by viewModel.rondaSearchQuery.collectAsStateWithLifecycle()

    val showAddDues by viewModel.showAddDuesDialog.collectAsStateWithLifecycle()
    val showAddActivity by viewModel.showAddActivityDialog.collectAsStateWithLifecycle()
    val showAddCitizen by viewModel.showAddCitizenDialog.collectAsStateWithLifecycle()
    val showAddRondaGroup by viewModel.showAddRondaGroupDialog.collectAsStateWithLifecycle()
    val showAddRondaSchedule by viewModel.showAddRondaScheduleDialog.collectAsStateWithLifecycle()
    val showExportPdf by viewModel.showExportPdfDialog.collectAsStateWithLifecycle()

    val editingRondaGroup by viewModel.editingRondaGroup.collectAsStateWithLifecycle()
    val selectedRondaSchedule by viewModel.selectedRondaSchedule.collectAsStateWithLifecycle()
    val selectedRondaGroup by viewModel.selectedRondaGroup.collectAsStateWithLifecycle()

    val selectedReceipt by viewModel.selectedReceipt.collectAsStateWithLifecycle()
    val selectedReportDetail by viewModel.selectedReportDetail.collectAsStateWithLifecycle()
    val quickPayTarget by viewModel.quickPayTarget.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .shadow(1.dp, CircleShape)
                                .background(BluePrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (currentTab) {
                                    ScreenTab.HOME -> Icons.Filled.Home
                                    ScreenTab.DUES -> Icons.Filled.ReceiptLong
                                    ScreenTab.RONDA -> Icons.Filled.Security
                                    ScreenTab.ACTIVITIES -> Icons.Filled.Campaign
                                    ScreenTab.CITIZENS -> Icons.Filled.People
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = when (currentTab) {
                                    ScreenTab.HOME -> "G-SYNC RW 26"
                                    ScreenTab.DUES -> "Iuran Warga RW 26"
                                    ScreenTab.RONDA -> "Jadwal Ronda & Regu"
                                    ScreenTab.ACTIVITIES -> "Kegiatan Lingkungan"
                                    ScreenTab.CITIZENS -> "Data Warga & RT"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when (currentTab) {
                                    ScreenTab.HOME -> "KP.COPONG SEJAHTERA"
                                    ScreenTab.DUES -> "Transparansi Kas Digital"
                                    ScreenTab.RONDA -> "Siskamling & Keamanan RW 26"
                                    ScreenTab.ACTIVITIES -> "Jadwal & Laporan Warga"
                                    ScreenTab.CITIZENS -> "Direktori Rukun Tetangga"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (currentTab == ScreenTab.DUES || currentTab == ScreenTab.HOME) {
                        IconButton(
                            onClick = { viewModel.openExportPdfDialog() },
                            modifier = Modifier.testTag("topbar_export_pdf_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "Cetak Laporan PDF",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { /* notification action */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifikasi",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar")
            ) {
                val navItemColors = NavigationBarItemDefaults.colors(
                    indicatorColor = NavIndicatorColor,
                    selectedIconColor = NavSelectedColor,
                    selectedTextColor = NavSelectedColor,
                    unselectedIconColor = NavUnselectedColor,
                    unselectedTextColor = NavUnselectedColor
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.HOME,
                    onClick = { viewModel.setTab(ScreenTab.HOME) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Beranda"
                        )
                    },
                    label = { Text("Beranda", fontWeight = if (currentTab == ScreenTab.HOME) FontWeight.Bold else FontWeight.Medium) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_tab_home")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.DUES,
                    onClick = { viewModel.setTab(ScreenTab.DUES) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.DUES) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                            contentDescription = "Iuran"
                        )
                    },
                    label = { Text("Iuran", fontWeight = if (currentTab == ScreenTab.DUES) FontWeight.Bold else FontWeight.Medium) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_tab_dues")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.RONDA,
                    onClick = { viewModel.setTab(ScreenTab.RONDA) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.RONDA) Icons.Filled.Security else Icons.Outlined.Security,
                            contentDescription = "Ronda"
                        )
                    },
                    label = { Text("Ronda", fontWeight = if (currentTab == ScreenTab.RONDA) FontWeight.Bold else FontWeight.Medium) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_tab_ronda")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.ACTIVITIES,
                    onClick = { viewModel.setTab(ScreenTab.ACTIVITIES) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.ACTIVITIES) Icons.Filled.Campaign else Icons.Outlined.Campaign,
                            contentDescription = "Kegiatan"
                        )
                    },
                    label = { Text("Kegiatan", fontWeight = if (currentTab == ScreenTab.ACTIVITIES) FontWeight.Bold else FontWeight.Medium) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_tab_activities")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.CITIZENS,
                    onClick = { viewModel.setTab(ScreenTab.CITIZENS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.CITIZENS) Icons.Filled.People else Icons.Outlined.People,
                            contentDescription = "Warga"
                        )
                    },
                    label = { Text("Warga", fontWeight = if (currentTab == ScreenTab.CITIZENS) FontWeight.Bold else FontWeight.Medium) },
                    colors = navItemColors,
                    modifier = Modifier.testTag("nav_tab_citizens")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.HOME -> {
                    HomeScreen(
                        stats = stats,
                        duesList = filteredDues,
                        activitiesList = filteredReports,
                        todayRondaGroup = todayRondaGroup,
                        onNavigateTab = { tab -> viewModel.setTab(tab) },
                        onOpenAddDues = { viewModel.openAddDuesDialog() },
                        onOpenAddActivity = { viewModel.openAddActivityDialog() },
                        onOpenReceipt = { dues -> viewModel.openReceipt(dues) },
                        onQuickPay = { dues -> viewModel.openQuickPay(dues) },
                        onOpenReportDetail = { report -> viewModel.openReportDetail(report) },
                        onDeleteDues = { dues -> viewModel.deleteDues(dues) },
                        onDeleteActivity = { report -> viewModel.deleteActivityReport(report) },
                        onOpenExportPdf = { viewModel.openExportPdfDialog() }
                    )
                }

                ScreenTab.DUES -> {
                    DuesScreen(
                        duesList = filteredDues,
                        searchQuery = duesSearchQuery,
                        statusFilter = duesStatusFilter,
                        categoryFilter = duesCategoryFilter,
                        onSearchChange = { query -> viewModel.setDuesSearchQuery(query) },
                        onStatusFilterChange = { status -> viewModel.setDuesStatusFilter(status) },
                        onCategoryFilterChange = { cat -> viewModel.setDuesCategoryFilter(cat) },
                        onOpenAddDues = { viewModel.openAddDuesDialog() },
                        onOpenReceipt = { dues -> viewModel.openReceipt(dues) },
                        onQuickPay = { dues -> viewModel.openQuickPay(dues) },
                        onDeleteDues = { dues -> viewModel.deleteDues(dues) },
                        onExportPdf = { viewModel.openExportPdfDialog() }
                    )
                }

                ScreenTab.RONDA -> {
                    RondaScreen(
                        currentSubTab = rondaSubTab,
                        onSubTabChange = { tab -> viewModel.setRondaSubTab(tab) },
                        todayGroup = todayRondaGroup,
                        groupsList = filteredRondaGroups,
                        schedulesList = filteredRondaSchedules,
                        dayFilter = rondaDayFilter,
                        statusFilter = rondaStatusFilter,
                        searchQuery = rondaSearchQuery,
                        onDayFilterChange = { day -> viewModel.setRondaDayFilter(day) },
                        onStatusFilterChange = { status -> viewModel.setRondaStatusFilter(status) },
                        onSearchQueryChange = { query -> viewModel.setRondaSearchQuery(query) },
                        onOpenAddGroup = { viewModel.openAddRondaGroupDialog() },
                        onOpenAddSchedule = { viewModel.openAddRondaScheduleDialog() },
                        onOpenGroupDetail = { group -> viewModel.openRondaGroupDetail(group) },
                        onOpenScheduleDetail = { schedule -> viewModel.openRondaScheduleDetail(schedule) }
                    )
                }

                ScreenTab.ACTIVITIES -> {
                    ActivitiesScreen(
                        reportsList = filteredReports,
                        categoryFilter = activityCategoryFilter,
                        statusFilter = activityStatusFilter,
                        onCategoryFilterChange = { cat -> viewModel.setActivityCategoryFilter(cat) },
                        onStatusFilterChange = { status -> viewModel.setActivityStatusFilter(status) },
                        onOpenAddActivity = { viewModel.openAddActivityDialog() },
                        onOpenReportDetail = { report -> viewModel.openReportDetail(report) },
                        onDeleteReport = { report -> viewModel.deleteActivityReport(report) }
                    )
                }

                ScreenTab.CITIZENS -> {
                    CitizensScreen(
                        citizensList = filteredCitizens,
                        rtFilter = citizenRtFilter,
                        lockFilter = citizenLockFilter,
                        onRtFilterChange = { rt -> viewModel.setCitizenRtFilter(rt) },
                        onLockFilterChange = { filter -> viewModel.setCitizenLockFilter(filter) },
                        onToggleLockCitizen = { citizen -> viewModel.toggleCitizenLock(citizen) },
                        onOpenAddCitizen = { viewModel.openAddCitizenDialog() },
                        onDeleteCitizen = { citizen -> viewModel.deleteCitizen(citizen) }
                    )
                }
            }
        }

        // Dialogs
        if (showAddDues) {
            AddDuesDialog(
                citizens = allCitizens,
                onDismiss = { viewModel.closeAddDuesDialog() },
                onConfirm = { dues ->
                    viewModel.addDues(dues)
                    viewModel.closeAddDuesDialog()
                }
            )
        }

        if (showAddActivity) {
            AddActivityDialog(
                onDismiss = { viewModel.closeAddActivityDialog() },
                onConfirm = { report ->
                    viewModel.addActivityReport(report)
                    viewModel.closeAddActivityDialog()
                }
            )
        }

        if (showAddCitizen) {
            AddCitizenDialog(
                onDismiss = { viewModel.closeAddCitizenDialog() },
                onConfirm = { citizen ->
                    viewModel.addCitizen(citizen)
                    viewModel.closeAddCitizenDialog()
                }
            )
        }

        if (showAddRondaGroup) {
            AddRondaGroupDialog(
                initialGroup = editingRondaGroup,
                citizensList = allCitizens,
                onDismiss = { viewModel.closeAddRondaGroupDialog() },
                onSave = { group ->
                    viewModel.saveRondaGroup(group)
                }
            )
        }

        if (showAddRondaSchedule) {
            AddRondaScheduleDialog(
                groupsList = allRondaGroups,
                onDismiss = { viewModel.closeAddRondaScheduleDialog() },
                onSave = { schedule ->
                    viewModel.saveRondaSchedule(schedule)
                }
            )
        }

        selectedRondaSchedule?.let { schedule ->
            val scheduleGroup = allRondaGroups.firstOrNull { it.id == schedule.groupId }
                ?: allRondaGroups.firstOrNull { it.groupName.equals(schedule.groupName, ignoreCase = true) }
            RondaScheduleDetailDialog(
                schedule = schedule,
                group = scheduleGroup,
                onDismiss = { viewModel.closeRondaScheduleDetail() },
                onStatusChange = { newStatus ->
                    viewModel.updateRondaScheduleStatus(schedule, newStatus)
                },
                onToggleMemberCheckIn = { member ->
                    viewModel.toggleRondaMemberCheckIn(schedule, member)
                },
                onDelete = {
                    viewModel.deleteRondaSchedule(schedule)
                }
            )
        }

        selectedRondaGroup?.let { group ->
            RondaGroupDetailDialog(
                group = group,
                onDismiss = { viewModel.closeRondaGroupDetail() },
                onEdit = {
                    viewModel.closeRondaGroupDetail()
                    viewModel.openAddRondaGroupDialog(group)
                },
                onDelete = {
                    viewModel.deleteRondaGroup(group)
                }
            )
        }

        selectedReceipt?.let { dues ->
            ReceiptDialog(
                dues = dues,
                onDismiss = { viewModel.closeReceipt() }
            )
        }

        selectedReportDetail?.let { report ->
            ReportDetailDialog(
                report = report,
                onDismiss = { viewModel.closeReportDetail() }
            )
        }

        quickPayTarget?.let { dues ->
            QuickPayDialog(
                dues = dues,
                onDismiss = { viewModel.closeQuickPay() },
                onConfirm = { method ->
                    viewModel.payDues(dues, method)
                }
            )
        }

        if (showExportPdf) {
            ExportDuesPdfDialog(
                duesList = allDues,
                onDismiss = { viewModel.closeExportPdfDialog() }
            )
        }
    }
}
