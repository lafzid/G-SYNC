package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.WargaDatabase
import com.example.data.WargaRepository
import com.example.data.model.ActivityReportEntity
import com.example.data.model.CitizenEntity
import com.example.data.model.DuesEntity
import com.example.data.model.RondaGroupEntity
import com.example.data.model.RondaScheduleEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class FinancialStats(
    val totalCollected: Long = 0,
    val totalPending: Long = 0,
    val totalSpent: Long = 0,
    val netBalance: Long = 0,
    val paidCount: Int = 0,
    val pendingCount: Int = 0,
    val citizenCount: Int = 0
)

enum class ScreenTab {
    HOME, DUES, RONDA, ACTIVITIES, CITIZENS
}

enum class RondaSubTab {
    SCHEDULES, GROUPS
}

class WargaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WargaRepository

    init {
        val db = WargaDatabase.getDatabase(application)
        repository = WargaRepository(
            db.citizenDao(),
            db.duesDao(),
            db.activityReportDao(),
            db.rondaDao()
        )
    }

    // Active Navigation Tab
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    fun setTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    // Search and Filters
    private val _duesStatusFilter = MutableStateFlow("Semua")
    val duesStatusFilter: StateFlow<String> = _duesStatusFilter.asStateFlow()

    private val _duesCategoryFilter = MutableStateFlow("Semua")
    val duesCategoryFilter: StateFlow<String> = _duesCategoryFilter.asStateFlow()

    private val _duesSearchQuery = MutableStateFlow("")
    val duesSearchQuery: StateFlow<String> = _duesSearchQuery.asStateFlow()

    private val _citizenRtFilter = MutableStateFlow("Semua")
    val citizenRtFilter: StateFlow<String> = _citizenRtFilter.asStateFlow()

    private val _activityCategoryFilter = MutableStateFlow("Semua")
    val activityCategoryFilter: StateFlow<String> = _activityCategoryFilter.asStateFlow()

    private val _activityStatusFilter = MutableStateFlow("Semua")
    val activityStatusFilter: StateFlow<String> = _activityStatusFilter.asStateFlow()

    // Ronda SubTab & Filters
    private val _rondaSubTab = MutableStateFlow(RondaSubTab.SCHEDULES)
    val rondaSubTab: StateFlow<RondaSubTab> = _rondaSubTab.asStateFlow()

    private val _rondaDayFilter = MutableStateFlow("Semua")
    val rondaDayFilter: StateFlow<String> = _rondaDayFilter.asStateFlow()

    private val _rondaStatusFilter = MutableStateFlow("Semua")
    val rondaStatusFilter: StateFlow<String> = _rondaStatusFilter.asStateFlow()

    private val _rondaSearchQuery = MutableStateFlow("")
    val rondaSearchQuery: StateFlow<String> = _rondaSearchQuery.asStateFlow()

    // Base flows
    val allCitizens = repository.allCitizens.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allDues = repository.allDues.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allReports = repository.allReports.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allRondaGroups = repository.allRondaGroups.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allRondaSchedules = repository.allRondaSchedules.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Today's Day of Week in Indonesian
    fun getTodayIndonesianDay(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Senin"
            Calendar.TUESDAY -> "Selasa"
            Calendar.WEDNESDAY -> "Rabu"
            Calendar.THURSDAY -> "Kamis"
            Calendar.FRIDAY -> "Jumat"
            Calendar.SATURDAY -> "Sabtu"
            Calendar.SUNDAY -> "Minggu"
            else -> "Senin"
        }
    }

    val todayRondaGroup: StateFlow<RondaGroupEntity?> = allRondaGroups.map { groups ->
        val today = getTodayIndonesianDay()
        groups.firstOrNull { it.dayOfWeek.equals(today, ignoreCase = true) } ?: groups.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filtered Ronda Groups Flow
    val filteredRondaGroups: StateFlow<List<RondaGroupEntity>> = combine(
        allRondaGroups,
        _rondaDayFilter,
        _rondaSearchQuery
    ) { groups, day, query ->
        groups.filter { g ->
            val matchDay = (day == "Semua" || g.dayOfWeek.equals(day, ignoreCase = true))
            val matchQuery = query.isBlank() ||
                    g.groupName.contains(query, ignoreCase = true) ||
                    g.coordinatorName.contains(query, ignoreCase = true) ||
                    g.members.contains(query, ignoreCase = true) ||
                    g.targetZone.contains(query, ignoreCase = true)
            matchDay && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Ronda Schedules Flow
    val filteredRondaSchedules: StateFlow<List<RondaScheduleEntity>> = combine(
        allRondaSchedules,
        _rondaDayFilter,
        _rondaStatusFilter,
        _rondaSearchQuery
    ) { schedules, day, status, query ->
        schedules.filter { s ->
            val matchDay = (day == "Semua" || s.dayOfWeek.equals(day, ignoreCase = true))
            val matchStatus = (status == "Semua" || s.status.equals(status, ignoreCase = true))
            val matchQuery = query.isBlank() ||
                    s.groupName.contains(query, ignoreCase = true) ||
                    s.securityNotes.contains(query, ignoreCase = true) ||
                    s.incidentReport.contains(query, ignoreCase = true) ||
                    s.checkedInMembers.contains(query, ignoreCase = true)
            matchDay && matchStatus && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Dues Flow
    val filteredDues: StateFlow<List<DuesEntity>> = combine(
        allDues,
        _duesStatusFilter,
        _duesCategoryFilter,
        _duesSearchQuery
    ) { duesList, status, category, query ->
        duesList.filter { dues ->
            val matchStatus = (status == "Semua" || dues.status.equals(status, ignoreCase = true))
            val matchCategory = (category == "Semua" || dues.category == category)
            val matchQuery = query.isBlank() ||
                    dues.citizenName.contains(query, ignoreCase = true) ||
                    dues.houseNumber.contains(query, ignoreCase = true) ||
                    dues.rt.contains(query, ignoreCase = true)
            matchStatus && matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Citizens Flow
    val filteredCitizens: StateFlow<List<CitizenEntity>> = combine(
        allCitizens,
        _citizenRtFilter
    ) { citizens, rt ->
        if (rt == "Semua") citizens else citizens.filter { it.rt == rt }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Activity Reports Flow
    val filteredReports: StateFlow<List<ActivityReportEntity>> = combine(
        allReports,
        _activityCategoryFilter,
        _activityStatusFilter
    ) { reports, category, status ->
        reports.filter { r ->
            val matchCategory = (category == "Semua" || r.category == category)
            val matchStatus = (status == "Semua" || r.status.equals(status, ignoreCase = true))
            matchCategory && matchStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial & Community Stats
    val financialStats: StateFlow<FinancialStats> = combine(
        allDues,
        allReports,
        allCitizens
    ) { duesList, reportsList, citizensList ->
        var collected = 0L
        var pending = 0L
        var paidCount = 0
        var pendingCount = 0

        for (d in duesList) {
            if (d.status.equals("Lunas", ignoreCase = true)) {
                collected += d.amount
                paidCount++
            } else {
                pending += d.amount
                pendingCount++
            }
        }

        val spent = reportsList.sumOf { it.budgetSpent }
        val net = collected - spent

        FinancialStats(
            totalCollected = collected,
            totalPending = pending,
            totalSpent = spent,
            netBalance = net,
            paidCount = paidCount,
            pendingCount = pendingCount,
            citizenCount = citizensList.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialStats())

    // Filter Setter Functions
    fun setDuesStatusFilter(status: String) { _duesStatusFilter.value = status }
    fun setDuesCategoryFilter(category: String) { _duesCategoryFilter.value = category }
    fun setDuesSearchQuery(query: String) { _duesSearchQuery.value = query }
    fun setCitizenRtFilter(rt: String) { _citizenRtFilter.value = rt }
    fun setActivityCategoryFilter(category: String) { _activityCategoryFilter.value = category }
    fun setActivityStatusFilter(status: String) { _activityStatusFilter.value = status }

    fun setRondaSubTab(subTab: RondaSubTab) { _rondaSubTab.value = subTab }
    fun setRondaDayFilter(day: String) { _rondaDayFilter.value = day }
    fun setRondaStatusFilter(status: String) { _rondaStatusFilter.value = status }
    fun setRondaSearchQuery(query: String) { _rondaSearchQuery.value = query }

    // Dialog & Interaction State
    private val _showAddDuesDialog = MutableStateFlow(false)
    val showAddDuesDialog = _showAddDuesDialog.asStateFlow()

    private val _showAddActivityDialog = MutableStateFlow(false)
    val showAddActivityDialog = _showAddActivityDialog.asStateFlow()

    private val _showAddCitizenDialog = MutableStateFlow(false)
    val showAddCitizenDialog = _showAddCitizenDialog.asStateFlow()

    private val _showAddRondaGroupDialog = MutableStateFlow(false)
    val showAddRondaGroupDialog = _showAddRondaGroupDialog.asStateFlow()

    private val _showAddRondaScheduleDialog = MutableStateFlow(false)
    val showAddRondaScheduleDialog = _showAddRondaScheduleDialog.asStateFlow()

    private val _selectedRondaSchedule = MutableStateFlow<RondaScheduleEntity?>(null)
    val selectedRondaSchedule = _selectedRondaSchedule.asStateFlow()

    private val _selectedRondaGroup = MutableStateFlow<RondaGroupEntity?>(null)
    val selectedRondaGroup = _selectedRondaGroup.asStateFlow()

    private val _editingRondaGroup = MutableStateFlow<RondaGroupEntity?>(null)
    val editingRondaGroup = _editingRondaGroup.asStateFlow()

    private val _selectedReceipt = MutableStateFlow<DuesEntity?>(null)
    val selectedReceipt = _selectedReceipt.asStateFlow()

    private val _selectedReportDetail = MutableStateFlow<ActivityReportEntity?>(null)
    val selectedReportDetail = _selectedReportDetail.asStateFlow()

    private val _quickPayTarget = MutableStateFlow<DuesEntity?>(null)
    val quickPayTarget = _quickPayTarget.asStateFlow()

    private val _showExportPdfDialog = MutableStateFlow(false)
    val showExportPdfDialog = _showExportPdfDialog.asStateFlow()

    fun openExportPdfDialog() { _showExportPdfDialog.value = true }
    fun closeExportPdfDialog() { _showExportPdfDialog.value = false }

    fun openAddDuesDialog() { _showAddDuesDialog.value = true }
    fun closeAddDuesDialog() { _showAddDuesDialog.value = false }

    fun openAddActivityDialog() { _showAddActivityDialog.value = true }
    fun closeAddActivityDialog() { _showAddActivityDialog.value = false }

    fun openAddCitizenDialog() { _showAddCitizenDialog.value = true }
    fun closeAddCitizenDialog() { _showAddCitizenDialog.value = false }

    fun openAddRondaGroupDialog(group: RondaGroupEntity? = null) {
        _editingRondaGroup.value = group
        _showAddRondaGroupDialog.value = true
    }
    fun closeAddRondaGroupDialog() {
        _editingRondaGroup.value = null
        _showAddRondaGroupDialog.value = false
    }

    fun openAddRondaScheduleDialog() { _showAddRondaScheduleDialog.value = true }
    fun closeAddRondaScheduleDialog() { _showAddRondaScheduleDialog.value = false }

    fun openRondaScheduleDetail(schedule: RondaScheduleEntity) { _selectedRondaSchedule.value = schedule }
    fun closeRondaScheduleDetail() { _selectedRondaSchedule.value = null }

    fun openRondaGroupDetail(group: RondaGroupEntity) { _selectedRondaGroup.value = group }
    fun closeRondaGroupDetail() { _selectedRondaGroup.value = null }

    fun openReceipt(dues: DuesEntity) { _selectedReceipt.value = dues }
    fun closeReceipt() { _selectedReceipt.value = null }

    fun openReportDetail(report: ActivityReportEntity) { _selectedReportDetail.value = report }
    fun closeReportDetail() { _selectedReportDetail.value = null }

    fun openQuickPay(dues: DuesEntity) { _quickPayTarget.value = dues }
    fun closeQuickPay() { _quickPayTarget.value = null }

    // Actions
    fun addCitizen(citizen: CitizenEntity) {
        viewModelScope.launch {
            repository.insertCitizen(citizen)
        }
    }

    fun deleteCitizen(citizen: CitizenEntity) {
        viewModelScope.launch {
            repository.deleteCitizen(citizen)
        }
    }

    fun addDues(dues: DuesEntity) {
        viewModelScope.launch {
            repository.insertDues(dues)
        }
    }

    fun payDues(dues: DuesEntity, paymentMethod: String) {
        viewModelScope.launch {
            repository.markDuesAsPaid(dues, paymentMethod)
            _quickPayTarget.value = null
        }
    }

    fun deleteDues(dues: DuesEntity) {
        viewModelScope.launch {
            repository.deleteDues(dues)
        }
    }

    fun addActivityReport(report: ActivityReportEntity) {
        viewModelScope.launch {
            repository.insertReport(report)
        }
    }

    fun deleteActivityReport(report: ActivityReportEntity) {
        viewModelScope.launch {
            repository.deleteReport(report)
        }
    }

    // Ronda Actions
    fun saveRondaGroup(group: RondaGroupEntity) {
        viewModelScope.launch {
            if (group.id == 0L) {
                repository.insertRondaGroup(group)
            } else {
                repository.updateRondaGroup(group)
            }
            closeAddRondaGroupDialog()
        }
    }

    fun deleteRondaGroup(group: RondaGroupEntity) {
        viewModelScope.launch {
            repository.deleteRondaGroup(group)
            if (_selectedRondaGroup.value?.id == group.id) {
                _selectedRondaGroup.value = null
            }
        }
    }

    fun saveRondaSchedule(schedule: RondaScheduleEntity) {
        viewModelScope.launch {
            if (schedule.id == 0L) {
                repository.insertRondaSchedule(schedule)
            } else {
                repository.updateRondaSchedule(schedule)
            }
            closeAddRondaScheduleDialog()
        }
    }

    fun deleteRondaSchedule(schedule: RondaScheduleEntity) {
        viewModelScope.launch {
            repository.deleteRondaSchedule(schedule)
            if (_selectedRondaSchedule.value?.id == schedule.id) {
                _selectedRondaSchedule.value = null
            }
        }
    }

    fun updateRondaScheduleStatus(schedule: RondaScheduleEntity, newStatus: String) {
        viewModelScope.launch {
            val updated = schedule.copy(status = newStatus)
            repository.updateRondaSchedule(updated)
            if (_selectedRondaSchedule.value?.id == schedule.id) {
                _selectedRondaSchedule.value = updated
            }
        }
    }

    fun toggleRondaMemberCheckIn(schedule: RondaScheduleEntity, memberName: String) {
        viewModelScope.launch {
            val currentList = schedule.checkedInMembers
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .toMutableList()

            if (currentList.contains(memberName)) {
                currentList.remove(memberName)
            } else {
                currentList.add(memberName)
            }

            val updatedCheckedIn = currentList.joinToString(", ")
            val updatedSummary = "${currentList.size} Petugas Hadir"
            val updated = schedule.copy(
                checkedInMembers = updatedCheckedIn,
                attendanceSummary = updatedSummary
            )
            repository.updateRondaSchedule(updated)
            if (_selectedRondaSchedule.value?.id == schedule.id) {
                _selectedRondaSchedule.value = updated
            }
        }
    }

    fun getRondaWhatsAppText(schedule: RondaScheduleEntity): String {
        val group = allRondaGroups.value.firstOrNull { it.id == schedule.groupId }
            ?: allRondaGroups.value.firstOrNull { it.groupName.equals(schedule.groupName, ignoreCase = true) }
        return WargaRepository.generateRondaReportText(schedule, group)
    }
}
