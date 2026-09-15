package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityDao {
    @Query("SELECT * FROM community_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<CommunityPostEntity>>

    @Query("SELECT * FROM community_posts WHERE category = :category ORDER BY timestamp DESC")
    fun getPostsByCategory(category: String): Flow<List<CommunityPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: CommunityPostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<CommunityPostEntity>)

    @Update
    suspend fun updatePost(post: CommunityPostEntity)

    @Query("UPDATE community_posts SET likesCount = likesCount + 1, isLikedByUser = 1 WHERE id = :postId")
    suspend fun likePost(postId: Long)

    @Query("UPDATE community_posts SET likesCount = CASE WHEN likesCount > 0 THEN likesCount - 1 ELSE 0 END, isLikedByUser = 0 WHERE id = :postId")
    suspend fun unlikePost(postId: Long)

    @Query("UPDATE community_posts SET isBookmarked = :bookmarked WHERE id = :postId")
    suspend fun setBookmark(postId: Long, bookmarked: Boolean)

    @Query("SELECT * FROM community_comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Long): Flow<List<CommunityCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommunityCommentEntity): Long

    @Query("UPDATE community_posts SET commentsCount = commentsCount + 1 WHERE id = :postId")
    suspend fun incrementCommentCount(postId: Long)

    @Query("SELECT COUNT(*) FROM community_posts")
    suspend fun getPostCount(): Int
}
