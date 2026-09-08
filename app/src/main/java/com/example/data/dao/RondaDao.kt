package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RondaGroupEntity
import com.example.data.model.RondaScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RondaDao {
    // Groups
    @Query("SELECT * FROM ronda_groups ORDER BY id ASC")
    fun getAllGroups(): Flow<List<RondaGroupEntity>>

    @Query("SELECT * FROM ronda_groups WHERE dayOfWeek = :day LIMIT 1")
    fun getGroupByDay(day: String): Flow<RondaGroupEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: RondaGroupEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<RondaGroupEntity>)

    @Update
    suspend fun updateGroup(group: RondaGroupEntity)

    @Delete
    suspend fun deleteGroup(group: RondaGroupEntity)

    // Schedules
    @Query("SELECT * FROM ronda_schedules ORDER BY dateMillis DESC")
    fun getAllSchedules(): Flow<List<RondaScheduleEntity>>

    @Query("SELECT * FROM ronda_schedules WHERE status = :status ORDER BY dateMillis DESC")
    fun getSchedulesByStatus(status: String): Flow<List<RondaScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: RondaScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<RondaScheduleEntity>)

    @Update
    suspend fun updateSchedule(schedule: RondaScheduleEntity)

    @Delete
    suspend fun deleteSchedule(schedule: RondaScheduleEntity)
}
