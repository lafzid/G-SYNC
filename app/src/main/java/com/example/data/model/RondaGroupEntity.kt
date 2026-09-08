package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ronda_groups")
data class RondaGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val groupName: String,
    val dayOfWeek: String,
    val shiftHours: String = "22:00 - 04:00 WIB",
    val postLocation: String = "Pos Kamling Utama RW 26",
    val coordinatorName: String,
    val coordinatorPhone: String = "",
    val members: String,
    val targetZone: String = "Wilayah RW 26",
    val equipmentNotes: String = "Senter patroli, pentungan, rompi reflektif, kotak P3K",
    val isActive: Boolean = true
)
