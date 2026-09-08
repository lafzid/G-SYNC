package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DuesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DuesDao {
    @Query("SELECT * FROM dues ORDER BY paymentDate DESC, id DESC")
    fun getAllDues(): Flow<List<DuesEntity>>

    @Query("SELECT * FROM dues WHERE status = :status ORDER BY paymentDate DESC")
    fun getDuesByStatus(status: String): Flow<List<DuesEntity>>

    @Query("SELECT * FROM dues WHERE rt = :rt ORDER BY paymentDate DESC")
    fun getDuesByRt(rt: String): Flow<List<DuesEntity>>

    @Query("SELECT * FROM dues WHERE citizenId = :citizenId ORDER BY paymentDate DESC")
    fun getDuesByCitizen(citizenId: Long): Flow<List<DuesEntity>>

    @Query("SELECT SUM(amount) FROM dues WHERE status = 'Lunas'")
    fun getTotalCollected(): Flow<Long?>

    @Query("SELECT SUM(amount) FROM dues WHERE status = 'Belum Lunas'")
    fun getTotalPending(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDues(dues: DuesEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDues(duesList: List<DuesEntity>)

    @Update
    suspend fun updateDues(dues: DuesEntity)

    @Delete
    suspend fun deleteDues(dues: DuesEntity)
}
