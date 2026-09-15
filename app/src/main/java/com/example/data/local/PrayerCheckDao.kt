package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerCheckDao {
    @Query("SELECT * FROM prayer_checks WHERE key LIKE :datePrefix || '%'")
    fun getChecksForDate(datePrefix: String): Flow<List<PrayerCheckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCheck(check: PrayerCheckEntity)

    @Query("DELETE FROM prayer_checks WHERE key = :key")
    suspend fun removeCheck(key: String)
}
