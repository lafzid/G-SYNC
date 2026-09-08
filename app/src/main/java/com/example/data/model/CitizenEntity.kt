package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citizens")
data class CitizenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val rt: String, // e.g., "RT 01", "RT 02", "RT 03", "RT 04", "RT 05"
    val houseNumber: String, // e.g., "Blok B2 No. 14"
    val phoneNumber: String,
    val familyMembersCount: Int = 3,
    val statusDomisili: String = "Tetap", // "Tetap", "Kontrak", "Kost"
    val createdAt: Long = System.currentTimeMillis()
)
