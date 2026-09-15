package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String,
    val passwordHash: String,
    val birthDate: String, // Format: DD-MM-YYYY (contoh: 27-05-1991)
    val securityQuestion: String, // Pertanyaan gampang
    val securityAnswer: String, // Jawaban pertanyaan
    val createdAt: Long = System.currentTimeMillis()
)
