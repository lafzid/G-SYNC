package com.example.data

import com.example.data.dao.ActivityReportDao
import com.example.data.dao.CitizenDao
import com.example.data.dao.DuesDao
import com.example.data.dao.RondaDao
import com.example.data.model.ActivityReportEntity
import com.example.data.model.CitizenEntity
import com.example.data.model.DuesEntity
import com.example.data.model.RondaGroupEntity
import com.example.data.model.RondaScheduleEntity
import kotlinx.coroutines.flow.Flow
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WargaRepository(
    private val citizenDao: CitizenDao,
    private val duesDao: DuesDao,
    private val activityReportDao: ActivityReportDao,
    private val rondaDao: RondaDao
) {
    // Citizens
    val allCitizens: Flow<List<CitizenEntity>> = citizenDao.getAllCitizens()
    val totalCitizensCount: Flow<Int> = citizenDao.getCitizenCount()

    fun getCitizensByRt(rt: String): Flow<List<CitizenEntity>> = citizenDao.getCitizensByRt(rt)
    suspend fun insertCitizen(citizen: CitizenEntity): Long = citizenDao.insertCitizen(citizen)
    suspend fun updateCitizen(citizen: CitizenEntity) = citizenDao.updateCitizen(citizen)
    suspend fun deleteCitizen(citizen: CitizenEntity) = citizenDao.deleteCitizen(citizen)
    suspend fun toggleCitizenLock(citizen: CitizenEntity) {
        val updated = citizen.copy(isLocked = !citizen.isLocked)
        citizenDao.updateCitizen(updated)
    }
    suspend fun setCitizenLockStatus(id: Long, isLocked: Boolean) {
        citizenDao.updateCitizenLockStatus(id, isLocked)
    }

    // Dues
    val allDues: Flow<List<DuesEntity>> = duesDao.getAllDues()
    val totalDuesCollected: Flow<Long?> = duesDao.getTotalCollected()
    val totalDuesPending: Flow<Long?> = duesDao.getTotalPending()

    fun getDuesByStatus(status: String): Flow<List<DuesEntity>> = duesDao.getDuesByStatus(status)
    fun getDuesByRt(rt: String): Flow<List<DuesEntity>> = duesDao.getDuesByRt(rt)

    suspend fun insertDues(dues: DuesEntity): Long {
        val receiptNumber = if (dues.receiptNumber.isBlank()) {
            val dateCode = SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date())
            val randomSuffix = (100..999).random()
            "KW-RW26/$dateCode/$randomSuffix"
        } else {
            dues.receiptNumber
        }
        return duesDao.insertDues(dues.copy(receiptNumber = receiptNumber))
    }

    suspend fun updateDues(dues: DuesEntity) = duesDao.updateDues(dues)
    suspend fun deleteDues(dues: DuesEntity) = duesDao.deleteDues(dues)

    suspend fun markDuesAsPaid(dues: DuesEntity, paymentMethod: String) {
        val receiptNumber = if (dues.receiptNumber.isBlank()) {
            val dateCode = SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date())
            val randomSuffix = (100..999).random()
            "KW-RW26/$dateCode/$randomSuffix"
        } else {
            dues.receiptNumber
        }
        duesDao.updateDues(
            dues.copy(
                status = "Lunas",
                paymentMethod = paymentMethod,
                paymentDate = System.currentTimeMillis(),
                receiptNumber = receiptNumber
            )
        )
    }

    // Activity Reports
    val allReports: Flow<List<ActivityReportEntity>> = activityReportDao.getAllReports()
    val totalBudgetSpent: Flow<Long?> = activityReportDao.getTotalBudgetSpent()
    val totalReportsCount: Flow<Int> = activityReportDao.getReportCount()

    fun getReportsByCategory(category: String): Flow<List<ActivityReportEntity>> =
        activityReportDao.getReportsByCategory(category)

    fun getReportsByStatus(status: String): Flow<List<ActivityReportEntity>> =
        activityReportDao.getReportsByStatus(status)

    suspend fun insertReport(report: ActivityReportEntity): Long =
        activityReportDao.insertReport(report)

    suspend fun updateReport(report: ActivityReportEntity) =
        activityReportDao.updateReport(report)

    suspend fun deleteReport(report: ActivityReportEntity) =
        activityReportDao.deleteReport(report)

    // Ronda Groups & Schedules
    val allRondaGroups: Flow<List<RondaGroupEntity>> = rondaDao.getAllGroups()
    val allRondaSchedules: Flow<List<RondaScheduleEntity>> = rondaDao.getAllSchedules()

    fun getRondaGroupByDay(day: String): Flow<RondaGroupEntity?> = rondaDao.getGroupByDay(day)

    suspend fun insertRondaGroup(group: RondaGroupEntity): Long = rondaDao.insertGroup(group)
    suspend fun updateRondaGroup(group: RondaGroupEntity) = rondaDao.updateGroup(group)
    suspend fun deleteRondaGroup(group: RondaGroupEntity) = rondaDao.deleteGroup(group)

    suspend fun insertRondaSchedule(schedule: RondaScheduleEntity): Long = rondaDao.insertSchedule(schedule)
    suspend fun updateRondaSchedule(schedule: RondaScheduleEntity) = rondaDao.updateSchedule(schedule)
    suspend fun deleteRondaSchedule(schedule: RondaScheduleEntity) = rondaDao.deleteSchedule(schedule)

    companion object {
        fun formatRupiah(amount: Long): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            formatter.maximumFractionDigits = 0
            return formatter.format(amount)
        }

        fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            return sdf.format(Date(timestamp))
        }

        fun generateRondaReportText(
            schedule: RondaScheduleEntity,
            group: RondaGroupEntity?
        ): String {
            val formattedDate = formatDate(schedule.dateMillis)
            val coordinator = group?.coordinatorName ?: "Koordinator Regu"
            val post = group?.postLocation ?: "Pos Kamling RW 26"
            val zone = group?.targetZone ?: "Wilayah RW 26"
            val shift = group?.shiftHours ?: "22:00 - 04:00 WIB"

            return """
                📢 *LAPORAN SISKAMLING & RONDA RW 26*
                -------------------------------------
                📅 *Hari/Tanggal* : ${schedule.dayOfWeek}, $formattedDate
                🛡️ *Regu Tugas*    : ${schedule.groupName}
                🕒 *Jam Tugas*    : $shift
                📍 *Pos Pantau*   : $post
                🗺️ *Zona Wilayah* : $zone
                👤 *Koordinator*  : $coordinator
                
                📋 *Status Ronda* : ${schedule.status.uppercase(Locale.getDefault())}
                👥 *Kehadiran*    : ${schedule.attendanceSummary.ifEmpty { "Tercatat di Pos" }}
                ${if (schedule.checkedInMembers.isNotBlank()) "✅ *Petugas Hadir*: ${schedule.checkedInMembers}\n" else ""}
                📝 *Catatan Patroli*:
                ${schedule.securityNotes}
                
                ⚠️ *Kejadian / Insiden*:
                ${schedule.incidentReport}
                
                _Demikian laporan keamanan siskamling malam ini. Terima kasih kepada seluruh petugas ronda atas dedikasinya menjaga kenyamanan lingkungan RW 26._
            """.trimIndent()
        }

        fun generateReceiptText(dues: DuesEntity): String {
            val formattedDate = formatDate(dues.paymentDate)
            val formattedNominal = formatRupiah(dues.amount)

            return """
                ╔════════════════════════════════════╗
                ║   KUITANSI PEMBAYARAN IURAN RW 26  ║
                ║      PENGURUS RUKUN WARGA 26       ║
                ╚════════════════════════════════════╝
                
                No. Kuitansi : ${dues.receiptNumber.ifEmpty { "KW-RW26-OFFLINE" }}
                Status       : ${dues.status.uppercase(Locale.getDefault())}
                Tanggal      : $formattedDate
                
                Warga / KK   : ${dues.citizenName}
                Alamat       : ${dues.houseNumber}, ${dues.rt}
                
                Rincian Pembayaran:
                - Jenis      : ${dues.category}
                - Periode    : ${dues.periodMonth}
                - Jumlah     : $formattedNominal
                - Metode     : ${dues.paymentMethod}
                ${if (dues.notes.isNotBlank()) "- Catatan    : ${dues.notes}\n" else ""}
                Terima kasih atas partisipasi aktif Bapak/Ibu dalam mendukung kenyamanan dan keamanan lingkungan Warga RW 26.
                
                Salam Hangat,
                Pengurus & Bendahara RW 26
            """.trimIndent()
        }
    }
}
