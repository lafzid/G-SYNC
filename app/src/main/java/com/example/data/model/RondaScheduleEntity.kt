package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ronda_schedules")
data class RondaScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val groupId: Long,
    val groupName: String,
    val dateMillis: Long,
    val dayOfWeek: String,
    val status: String,
    val checkedInMembers: String = "",
    val attendanceSummary: String = "",
    val securityNotes: String = "Aman kondusif",
    val incidentReport: String = "Nihil kejadian"
)
