package com.example

import com.example.data.WargaRepository
import com.example.data.model.RondaGroupEntity
import com.example.data.model.RondaScheduleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testGenerateRondaReportText() {
        val group = RondaGroupEntity(
            id = 1L,
            groupName = "Regu Elang",
            dayOfWeek = "Senin",
            shiftHours = "22:00 - 04:00 WIB",
            postLocation = "Pos Kamling Utama RW 26",
            coordinatorName = "Budi Santoso",
            coordinatorPhone = "08123456789",
            members = "Budi Santoso, Pak Joko, Pak Slamet, Mas Agus",
            targetZone = "RT 01, RT 02 & Gerbang Utama",
            equipmentNotes = "Senter, rompi, HT"
        )

        val schedule = RondaScheduleEntity(
            id = 1L,
            groupId = 1L,
            groupName = "Regu Elang",
            dateMillis = 1700000000000L,
            dayOfWeek = "Senin",
            status = "Sedang Bertugas",
            checkedInMembers = "Budi Santoso, Pak Joko",
            attendanceSummary = "2 Petugas Hadir",
            securityNotes = "Pintu gerbang timur terkunci rapat. Situasi kondusif.",
            incidentReport = "Nihil kejadian"
        )

        val reportText = WargaRepository.generateRondaReportText(schedule, group)

        assertTrue(reportText.contains("LAPORAN SISKAMLING & RONDA RW 26"))
        assertTrue(reportText.contains("Regu Elang"))
        assertTrue(reportText.contains("Budi Santoso"))
        assertTrue(reportText.contains("2 Petugas Hadir"))
        assertTrue(reportText.contains("Situasi kondusif"))
    }

    @Test
    fun testFormatRupiahAndDuesReviewData() {
        val amount = 50000L
        val formatted = WargaRepository.formatRupiah(amount)
        assertTrue(formatted.contains("50.000") || formatted.contains("50,000"))

        val dues = com.example.data.model.DuesEntity(
            citizenId = 1L,
            citizenName = "Budi Santoso",
            rt = "RT 01",
            houseNumber = "Blok A4/12",
            category = "Iuran Kebersihan & Sampah",
            amount = 50000L,
            periodMonth = "September 2026",
            status = "Lunas",
            paymentMethod = "Transfer Bank",
            receiptNumber = "KW-RW26/260907/123"
        )
        val receipt = WargaRepository.generateReceiptText(dues)
        assertTrue(receipt.contains("KUITANSI PEMBAYARAN IURAN RW 26"))
        assertTrue(receipt.contains("Budi Santoso"))
        assertTrue(receipt.contains("Iuran Kebersihan & Sampah"))
        assertTrue(receipt.contains("Transfer Bank"))
    }

    @Test
    fun testDuesFilteringForPdf() {
        val config = com.example.util.DuesPdfGenerator.PdfConfig(
            periodFilter = "September 2026",
            rtFilter = "RT 01",
            statusFilter = "Lunas",
            ketuaName = "Drs. H. Mulyono",
            bendaharaName = "Hj. Siti Rahmawati"
        )

        val list = listOf(
            com.example.data.model.DuesEntity(
                citizenId = 1L,
                citizenName = "Budi Santoso",
                rt = "RT 01",
                houseNumber = "Blok A4/12",
                category = "Iuran Kebersihan",
                amount = 50000L,
                periodMonth = "September 2026",
                status = "Lunas"
            ),
            com.example.data.model.DuesEntity(
                citizenId = 2L,
                citizenName = "Joko Widodo",
                rt = "RT 02",
                houseNumber = "Blok B1/05",
                category = "Iuran Kebersihan",
                amount = 50000L,
                periodMonth = "September 2026",
                status = "Belum Lunas"
            )
        )

        val filtered = list.filter { dues ->
            val matchPeriod = config.periodFilter == "Semua Periode" || dues.periodMonth.equals(config.periodFilter, ignoreCase = true)
            val matchRt = config.rtFilter == "Semua RT" || dues.rt.equals(config.rtFilter, ignoreCase = true)
            val matchStatus = config.statusFilter == "Semua Status" || dues.status.equals(config.statusFilter, ignoreCase = true)
            matchPeriod && matchRt && matchStatus
        }

        assertEquals(1, filtered.size)
        assertEquals("Budi Santoso", filtered[0].citizenName)
        assertEquals(50000L, filtered.sumOf { it.amount })
    }

    @Test
    fun testCitizenLockStatus() {
        val citizen = com.example.data.model.CitizenEntity(
            id = 1L,
            name = "Bpk. Bambang Sutrisno",
            rt = "RT 01",
            houseNumber = "Blok A1 No. 04",
            phoneNumber = "0812-8876-1234",
            isLocked = true
        )
        assertTrue(citizen.isLocked)
        val unlocked = citizen.copy(isLocked = false)
        org.junit.Assert.assertFalse(unlocked.isLocked)
    }
}

