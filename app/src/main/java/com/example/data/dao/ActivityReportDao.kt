package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ActivityReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityReportDao {
    @Query("SELECT * FROM activity_reports ORDER BY dateMillis DESC, id DESC")
    fun getAllReports(): Flow<List<ActivityReportEntity>>

    @Query("SELECT * FROM activity_reports WHERE category = :category ORDER BY dateMillis DESC")
    fun getReportsByCategory(category: String): Flow<List<ActivityReportEntity>>

    @Query("SELECT * FROM activity_reports WHERE status = :status ORDER BY dateMillis DESC")
    fun getReportsByStatus(status: String): Flow<List<ActivityReportEntity>>

    @Query("SELECT SUM(budgetSpent) FROM activity_reports")
    fun getTotalBudgetSpent(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM activity_reports")
    fun getReportCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ActivityReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReports(reports: List<ActivityReportEntity>)

    @Update
    suspend fun updateReport(report: ActivityReportEntity)

    @Delete
    suspend fun deleteReport(report: ActivityReportEntity)
}
