package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_reports")
data class ActivityReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "Kerja Bakti", "Ronda Malam", "Musyawarah RW", "Posyandu", "Senam Sehat", "Bakti Sosial", "Pembangunan"
    val dateMillis: Long,
    val timeString: String, // e.g. "07:30 - 11:00 WIB"
    val location: String, // e.g. "Balai Warga RW 26"
    val coordinator: String, // e.g. "Bpk. Agus Santoso"
    val description: String,
    val budgetSpent: Long = 0, // In Rupiah (biaya kas terpakai)
    val attendeesCount: Int = 0, // Jumlah kehadiran warga
    val status: String = "Selesai", // "Selesai", "Sedang Berjalan", "Rencana"
    val documentationNote: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
