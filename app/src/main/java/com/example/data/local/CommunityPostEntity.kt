package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val authorName: String,
    val authorBadge: String = "Jamaah",
    val title: String,
    val content: String,
    val category: String, // "Pengalaman Hijrah", "Tips Khusyuk", "Doa Bersama", "Kisah Ibadah"
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val isBookmarked: Boolean = false
)
