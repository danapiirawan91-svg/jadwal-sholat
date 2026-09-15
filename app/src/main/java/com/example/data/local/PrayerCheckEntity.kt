package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_checks")
data class PrayerCheckEntity(
    @PrimaryKey
    val key: String, // e.g. "2026-09-15_Subuh"
    val isCompleted: Boolean = false,
    val completedAt: Long = System.currentTimeMillis()
)
