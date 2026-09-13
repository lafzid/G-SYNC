package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ActivityReportDao
import com.example.data.dao.CitizenDao
import com.example.data.dao.DuesDao
import com.example.data.dao.RondaDao
import com.example.data.model.ActivityReportEntity
import com.example.data.model.CitizenEntity
import com.example.data.model.DuesEntity
import com.example.data.model.RondaGroupEntity
import com.example.data.model.RondaScheduleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CitizenEntity::class,
        DuesEntity::class,
        ActivityReportEntity::class,
        RondaGroupEntity::class,
        RondaScheduleEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class WargaDatabase : RoomDatabase() {

    abstract fun citizenDao(): CitizenDao
    abstract fun duesDao(): DuesDao
    abstract fun activityReportDao(): ActivityReportDao
    abstract fun rondaDao(): RondaDao

    companion object {
        @Volatile
        private var INSTANCE: WargaDatabase? = null

        fun getDatabase(context: Context): WargaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WargaDatabase::class.java,
                    "warga_rw26_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with realistic community data for RW 26
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                seedInitialData(database)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(db: WargaDatabase) {
            val citizens = listOf(
                CitizenEntity(1, "Bpk. Bambang Sutrisno", "RT 01", "Blok A1 No. 04", "0812-8876-1234", 4, "Tetap", isLocked = true),
                CitizenEntity(2, "Ibu Sri Wahyuni", "RT 01", "Blok A2 No. 08", "0813-1122-3344", 3, "Tetap", isLocked = true),
                CitizenEntity(3, "Bpk. Hendra Gunawan", "RT 01", "Blok A3 No. 12", "0857-4455-6677", 5, "Tetap", isLocked = false),
                CitizenEntity(4, "Bpk. Agus Santoso", "RT 02", "Blok B1 No. 02", "0812-9988-7766", 3, "Tetap", isLocked = true),
                CitizenEntity(5, "Ibu Ratna Dewi", "RT 02", "Blok B2 No. 07", "0878-3344-5566", 2, "Kontrak", isLocked = false),
                CitizenEntity(6, "Bpk. Dedi Kurniawan", "RT 02", "Blok B3 No. 15", "0813-7788-9900", 4, "Tetap", isLocked = true),
                CitizenEntity(7, "Bpk. Joko Prasetyo", "RT 03", "Blok C1 No. 03", "0852-6677-8899", 4, "Tetap", isLocked = false),
                CitizenEntity(8, "Ibu Siti Nurhaliza", "RT 03", "Blok C2 No. 11", "0819-2233-4455", 3, "Tetap", isLocked = true),
                CitizenEntity(9, "Bpk. Rudi Hartono", "RT 04", "Blok D1 No. 05", "0812-3344-8899", 5, "Tetap", isLocked = true),
                CitizenEntity(10, "Bpk. Ahmad Fauzi", "RT 04", "Blok D2 No. 14", "0877-5566-7788", 2, "Tetap", isLocked = false)
            )
            db.citizenDao().insertCitizens(citizens)

            val dues = listOf(
                DuesEntity(
                    id = 1,
                    citizenId = 1,
                    citizenName = "Bpk. Bambang Sutrisno",
                    rt = "RT 01",
                    houseNumber = "Blok A1 No. 04",
                    category = "Iuran Kebersihan & Sampah",
                    amount = 50000,
                    periodMonth = "September 2026",
                    paymentDate = System.currentTimeMillis() - 86400000L * 2,
                    status = "Lunas",
                    paymentMethod = "Transfer Bank",
                    receiptNumber = "KW-26/2609/001",
                    notes = "Iuran sampah bulanan transfer BCA"
                ),
                DuesEntity(
                    id = 2,
                    citizenId = 1,
                    citizenName = "Bpk. Bambang Sutrisno",
                    rt = "RT 01",
                    houseNumber = "Blok A1 No. 04",
                    category = "Keamanan & Ronda",
                    amount = 40000,
                    periodMonth = "September 2026",
                    paymentDate = System.currentTimeMillis() - 86400000L * 2,
                    status = "Lunas",
                    paymentMethod = "Transfer Bank",
                    receiptNumber = "KW-26/2609/002",
                    notes = "Iuran ronda satpam pos portal"
                ),
                DuesEntity(
                    id = 3,
                    citizenId = 2,
                    citizenName = "Ibu Sri Wahyuni",
                    rt = "RT 01",
                    houseNumber = "Blok A2 No. 08",
                    category = "Kas RW 26",
                    amount = 30000,
                    periodMonth = "September 2026",
                    paymentDate = System.currentTimeMillis() - 86400000L * 3,
                    status = "Lunas",
                    paymentMethod = "QRIS RW",
                    receiptNumber = "KW-26/2609/003",
                    notes = "Via QRIS RW di posko"
                ),
                DuesEntity(
                    id = 4,
                    citizenId = 3,
                    citizenName = "Bpk. Hendra Gunawan",
                    rt = "RT 01",
                    houseNumber = "Blok A3 No. 12",
                    category = "Iuran Kebersihan & Sampah",
                    amount = 50000,
                    periodMonth = "September 2026",
                    paymentDate = System.currentTimeMillis(),
                    status = "Belum Lunas",
                    paymentMethod = "Tunai",
                    receiptNumber = "KW-26/2609/004",
                    notes = "Menunggu tagihan dititip ke ketua RT"
                ),
                DuesEntity(
                    id = 5,
                    citizenId = 4,
                    citizenName = "Bpk. Agus Santoso",
                    rt = "RT 02",
                    houseNumber = "Blok B1 No. 02",
                    category = "Iuran Kebersihan & Sampah",
                    amount = 50000,
                    periodMonth = "September 2026",
                    paymentDate = System.currentTimeMillis() - 86400000L * 4,
                    status = "Lunas",
                    paymentMethod = "Tunai",
                    receiptNumber = "KW-26/2609/005",
                    notes = "Dibayar tunai ke bendahara"
                ),
                DuesEntity(
                    id = 6,
                    citizenId = 5,
                    citizenName = "Ibu Ratna Dewi",
                    rt = "RT 02",
                    houseNumber = "Blok B2 No. 07",
                    category = "Dana Sosial & Kematian",
                    amount = 25000,
                    periodMonth = "September 2026",
                    paymentDate = System.currentTimeMillis() - 86400000L * 5,
                    status = "Lunas",
                    paymentMethod = "QRIS RW",
                    receiptNumber = "KW-26/2609/006",
                    notes = "Dana sosial RW"
                ),
                DuesEntity(
                    id = 7,
                    citizenId = 7,
                    citizenName = "Bpk. Joko Prasetyo",
                    rt = "RT 03",
                    houseNumber = "Blok C1 No. 03",
                    category = "Pembangunan Fasum",
                    amount = 100000,
                    periodMonth = "September 2026",
                    paymentDate = System.currentTimeMillis() - 86400000L * 1,
                    status = "Lunas",
                    paymentMethod = "Transfer Bank",
                    receiptNumber = "KW-26/2609/007",
                    notes = "Sumbangan semen & paving jalan RT 03"
                ),
                DuesEntity(
                    id = 8,
                    citizenId = 9,
                    citizenName = "Bpk. Rudi Hartono",
                    rt = "RT 04",
                    houseNumber = "Blok D1 No. 05",
                    category = "Keamanan & Ronda",
                    amount = 40000,
                    periodMonth = "September 2026",
                    paymentDate = System.currentTimeMillis(),
                    status = "Belum Lunas",
                    paymentMethod = "Tunai",
                    receiptNumber = "KW-26/2609/008",
                    notes = "Jatuh tempo tanggal 10"
                )
            )
            db.duesDao().insertAllDues(dues)

            val reports = listOf(
                ActivityReportEntity(
                    id = 1,
                    title = "Kerja Bakti Massal Normalisasi Saluran Air & Fogging RW 26",
                    category = "Kerja Bakti",
                    dateMillis = System.currentTimeMillis() - 86400000L * 3,
                    timeString = "07:00 - 11:30 WIB",
                    location = "Saluran drainase utama RT 01 s/d RT 04",
                    coordinator = "Bpk. Agus Santoso (Sie Kebersihan RW 26)",
                    description = "Pembersihan endapan lumpur got menjelang musim hujan dan fogging nyamuk DBD di seluruh gang. Disediakan karung dan konsumsi sarapan warga.",
                    budgetSpent = 450000,
                    attendeesCount = 68,
                    status = "Selesai",
                    documentationNote = "Saluran lancar, 45 karung sampah lumpur diangkut dinas LH."
                ),
                ActivityReportEntity(
                    id = 2,
                    title = "Posyandu Teratai RW 26: Timbang Balita & Pemeriksaan Lansia",
                    category = "Posyandu",
                    dateMillis = System.currentTimeMillis() - 86400000L * 7,
                    timeString = "08:30 - 12:00 WIB",
                    location = "Balai Pertemuan Warga RW 26",
                    coordinator = "Ibu Sri Wahyuni (Kader PKK RW 26)",
                    description = "Penimbangan balita, pemberian vitamin A dan PMT biskuit susu. Pemeriksaan tensi darah dan cek gula darah berkala untuk para lansia.",
                    budgetSpent = 350000,
                    attendeesCount = 52,
                    status = "Selesai",
                    documentationNote = "42 Balita hadir, 28 Lansia tervaksin dan terperiksa kondisi kesehatannya."
                ),
                ActivityReportEntity(
                    id = 3,
                    title = "Musyawarah Warga RW 26: Rencana Paving Jalan & Portal Baru",
                    category = "Musyawarah RW",
                    dateMillis = System.currentTimeMillis() + 86400000L * 4,
                    timeString = "19:45 WIB - Selesai",
                    location = "Pendopo RW 26",
                    coordinator = "Bpk. Bambang Sutrisno (Ketua RW 26)",
                    description = "Rapat evaluasi keamanan lingkungan portal RT 01-04, transparansi laporan keuangan kas RW bulan berjalan, dan sosialisasi program digitalisasi warga.",
                    budgetSpent = 150000,
                    attendeesCount = 35,
                    status = "Rencana",
                    documentationNote = "Undangan telah disebar via grup WhatsApp seluruh ketua RT."
                ),
                ActivityReportEntity(
                    id = 4,
                    title = "Senam Sehat Bersama & Pemeriksaan Kesehatan Gratis",
                    category = "Senam Sehat",
                    dateMillis = System.currentTimeMillis() + 86400000L * 6,
                    timeString = "06:30 - 08:30 WIB",
                    location = "Lapangan Olahraga Serbaguna RT 03",
                    coordinator = "Ibu Ratna Dewi (Sie Pemuda & Olahraga)",
                    description = "Senam aerobik bersama instruktur profesional, doorprize botol minum ramah lingkungan, dan sarapan bubur kacang ijo gratis untuk warga.",
                    budgetSpent = 250000,
                    attendeesCount = 80,
                    status = "Rencana",
                    documentationNote = "Sound system dan instruktur senam telah dikonfirmasi."
                )
            )
            db.activityReportDao().insertReports(reports)

            val rondaGroups = listOf(
                RondaGroupEntity(
                    id = 1,
                    groupName = "Regu Elang",
                    dayOfWeek = "Senin",
                    shiftHours = "22:00 - 04:00 WIB",
                    postLocation = "Pos Kamling Utama RW 26 (Depan Balai Warga)",
                    coordinatorName = "Bpk. Bambang Sutrisno",
                    coordinatorPhone = "0812-8876-1234",
                    members = "Bpk. Bambang Sutrisno, Bpk. Agus Santoso, Bpk. Hendra Gunawan, Bpk. Rudi Hartono",
                    targetZone = "RT 01 & Portal Barat Blok A",
                    equipmentNotes = "Senter Cree LED (2), Pentungan (2), Rompi Reflektif (4), Kotak P3K"
                ),
                RondaGroupEntity(
                    id = 2,
                    groupName = "Regu Garuda",
                    dayOfWeek = "Selasa",
                    shiftHours = "22:00 - 04:00 WIB",
                    postLocation = "Pos Pantau RT 02 (Blok B Timur)",
                    coordinatorName = "Bpk. Agus Santoso",
                    coordinatorPhone = "0812-9988-7766",
                    members = "Bpk. Agus Santoso, Bpk. Dedi Kurniawan, Bpk. Ahmad Fauzi, Bpk. Joko Prasetyo",
                    targetZone = "RT 02 & Blok B",
                    equipmentNotes = "Senter Polisi (2), Handy Talky (2), Pentungan kayu, Jas Hujan"
                ),
                RondaGroupEntity(
                    id = 3,
                    groupName = "Regu Macan",
                    dayOfWeek = "Rabu",
                    shiftHours = "22:00 - 04:00 WIB",
                    postLocation = "Pos Kamling RT 03 (Taman Ceria)",
                    coordinatorName = "Bpk. Joko Prasetyo",
                    coordinatorPhone = "0852-6677-8899",
                    members = "Bpk. Joko Prasetyo, Bpk. Hendra Gunawan, Bpk. Rudi Hartono, Bpk. Dedi Kurniawan",
                    targetZone = "RT 03 & Blok C",
                    equipmentNotes = "Senter Sorot, Peluit (4), Kotak Obat, Payung Pos"
                ),
                RondaGroupEntity(
                    id = 4,
                    groupName = "Regu Rajawali",
                    dayOfWeek = "Kamis",
                    shiftHours = "22:00 - 04:00 WIB",
                    postLocation = "Pos Pantau Gerbang Selatan",
                    coordinatorName = "Bpk. Rudi Hartono",
                    coordinatorPhone = "0812-3344-8899",
                    members = "Bpk. Rudi Hartono, Bpk. Ahmad Fauzi, Bpk. Bambang Sutrisno, Bpk. Agus Santoso",
                    targetZone = "RT 04 & Blok D",
                    equipmentNotes = "Senter LED (3), Pentungan Rotan (2), Buku Tamu Pos, HT"
                ),
                RondaGroupEntity(
                    id = 5,
                    groupName = "Regu Badak",
                    dayOfWeek = "Jumat",
                    shiftHours = "22:00 - 04:00 WIB",
                    postLocation = "Pos Kamling Utama RW 26",
                    coordinatorName = "Bpk. Ahmad Fauzi",
                    coordinatorPhone = "0877-5566-7788",
                    members = "Bpk. Ahmad Fauzi, Bpk. Dedi Kurniawan, Bpk. Joko Prasetyo, Bpk. Hendra Gunawan",
                    targetZone = "RT 01 & RT 04 (Jalur Utama)",
                    equipmentNotes = "Senter Cas (2), Megaphone Pos, Rompi Keselamatan, Jas Hujan"
                ),
                RondaGroupEntity(
                    id = 6,
                    groupName = "Regu Banteng",
                    dayOfWeek = "Sabtu",
                    shiftHours = "21:30 - 04:30 WIB",
                    postLocation = "Pos Utama & Seluruh Portal RW 26",
                    coordinatorName = "Bpk. Hendra Gunawan",
                    coordinatorPhone = "0857-4455-6677",
                    members = "Bpk. Hendra Gunawan, Bpk. Bambang Sutrisno, Bpk. Rudi Hartono, Bpk. Dedi Kurniawan, Bpk. Agus Santoso",
                    targetZone = "Malam Minggu - Keliling Menyeluruh RW 26",
                    equipmentNotes = "Senter Daya Tinggi (4), HT 4 Channel, Pentungan (4), Rompi (5)"
                ),
                RondaGroupEntity(
                    id = 7,
                    groupName = "Regu Singa",
                    dayOfWeek = "Minggu",
                    shiftHours = "22:00 - 04:00 WIB",
                    postLocation = "Pos Kamling RT 01",
                    coordinatorName = "Bpk. Dedi Kurniawan",
                    coordinatorPhone = "0813-7788-9900",
                    members = "Bpk. Dedi Kurniawan, Bpk. Joko Prasetyo, Bpk. Ahmad Fauzi, Bpk. Bambang Sutrisno",
                    targetZone = "RT 01 s/d RT 03",
                    equipmentNotes = "Senter Polisi, Perlengkapan P3K, Buku Catatan Ronda, Peluit"
                )
            )
            db.rondaDao().insertGroups(rondaGroups)

            val now = System.currentTimeMillis()
            val rondaSchedules = listOf(
                RondaScheduleEntity(
                    id = 1,
                    groupId = 1,
                    groupName = "Regu Elang",
                    dateMillis = now - 86400000L * 7,
                    dayOfWeek = "Senin",
                    status = "Selesai",
                    checkedInMembers = "Bpk. Bambang Sutrisno, Bpk. Agus Santoso, Bpk. Hendra Gunawan",
                    attendanceSummary = "3 Hadir, 1 Izin (Bpk. Rudi Hartono dinas luar)",
                    securityNotes = "Patroli 4 putaran lancar. Portal timur dikunci pukul 23:00 WIB sesuai SOP RW.",
                    incidentReport = "Nihil kejadian. Warga Blok A2 laporan ada kucing terjepit pagar sudah dibantu."
                ),
                RondaScheduleEntity(
                    id = 2,
                    groupId = 6,
                    groupName = "Regu Banteng",
                    dateMillis = now - 86400000L * 2,
                    dayOfWeek = "Sabtu",
                    status = "Selesai",
                    checkedInMembers = "Bpk. Hendra Gunawan, Bpk. Bambang Sutrisno, Bpk. Rudi Hartono, Bpk. Dedi Kurniawan, Bpk. Agus Santoso",
                    attendanceSummary = "5 Hadir Lengkap",
                    securityNotes = "Malam minggu ramai warga kumpul. Patroli putaran pukul 01:00 dan 03:00 WIB situasi tertib aman.",
                    incidentReport = "Nihil kejadian mencurigakan. Tamu luar tercatat di buku pos 4 kendaraan bermotor."
                ),
                RondaScheduleEntity(
                    id = 3,
                    groupId = 1,
                    groupName = "Regu Elang",
                    dateMillis = now,
                    dayOfWeek = "Senin",
                    status = "Sedang Bertugas",
                    checkedInMembers = "Bpk. Bambang Sutrisno, Bpk. Agus Santoso, Bpk. Hendra Gunawan, Bpk. Rudi Hartono",
                    attendanceSummary = "4 Petugas Siap di Pos",
                    securityNotes = "Pos kamling utama sudah dibuka. Portal barat dan timur dijaga bergantian.",
                    incidentReport = "Lampu penerangan gang Blok A normal. Situasi kondusif."
                ),
                RondaScheduleEntity(
                    id = 4,
                    groupId = 2,
                    groupName = "Regu Garuda",
                    dateMillis = now + 86400000L,
                    dayOfWeek = "Selasa",
                    status = "Terjadwal",
                    checkedInMembers = "",
                    attendanceSummary = "Menunggu jadwal tugas",
                    securityNotes = "Persiapan ronda Selasa malam. HT dan senter dalam pengecasan di pos.",
                    incidentReport = "Belum ada laporan."
                ),
                RondaScheduleEntity(
                    id = 5,
                    groupId = 3,
                    groupName = "Regu Macan",
                    dateMillis = now + 86400000L * 2,
                    dayOfWeek = "Rabu",
                    status = "Terjadwal",
                    checkedInMembers = "",
                    attendanceSummary = "Menunggu jadwal tugas",
                    securityNotes = "Jadwal patroli terjadwal untuk hari Rabu malam.",
                    incidentReport = "Belum ada laporan."
                )
            )
            db.rondaDao().insertSchedules(rondaSchedules)
        }
    }
}
