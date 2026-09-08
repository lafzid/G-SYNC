package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dues")
data class DuesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val citizenId: Long = 0,
    val citizenName: String,
    val rt: String,
    val houseNumber: String,
    val category: String, // "Iuran Kebersihan & Sampah", "Keamanan & Ronda", "Kas RW 26", "Dana Sosial & Kematian", "Pembangunan Fasum"
    val amount: Long,
    val periodMonth: String, // e.g. "September 2026"
    val paymentDate: Long = System.currentTimeMillis(),
    val status: String = "Lunas", // "Lunas", "Belum Lunas"
    val paymentMethod: String = "Tunai", // "Tunai", "Transfer Bank", "QRIS RW"
    val receiptNumber: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
