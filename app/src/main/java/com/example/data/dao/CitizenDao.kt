package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CitizenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CitizenDao {
    @Query("SELECT * FROM citizens ORDER BY rt ASC, houseNumber ASC")
    fun getAllCitizens(): Flow<List<CitizenEntity>>

    @Query("SELECT * FROM citizens WHERE rt = :rt ORDER BY houseNumber ASC")
    fun getCitizensByRt(rt: String): Flow<List<CitizenEntity>>

    @Query("SELECT * FROM citizens WHERE id = :id")
    suspend fun getCitizenById(id: Long): CitizenEntity?

    @Query("SELECT COUNT(*) FROM citizens")
    fun getCitizenCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitizen(citizen: CitizenEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitizens(citizens: List<CitizenEntity>)

    @Update
    suspend fun updateCitizen(citizen: CitizenEntity)

    @Query("UPDATE citizens SET isLocked = :isLocked WHERE id = :id")
    suspend fun updateCitizenLockStatus(id: Long, isLocked: Boolean)

    @Delete
    suspend fun deleteCitizen(citizen: CitizenEntity)
}
