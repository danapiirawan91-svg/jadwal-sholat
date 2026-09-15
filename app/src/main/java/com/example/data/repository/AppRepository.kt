package com.example.data.repository

import com.example.data.api.GeminiRepository
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.CommunityCommentEntity
import com.example.data.local.CommunityPostEntity
import com.example.data.local.PrayerCheckEntity
import com.example.data.local.UserEntity
import com.example.data.model.CommunitySeedData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(
    private val database: AppDatabase,
    val geminiRepo: GeminiRepository = GeminiRepository()
) {
    private val communityDao = database.communityDao()
    private val chatDao = database.chatDao()
    private val prayerCheckDao = database.prayerCheckDao()
    private val userDao = database.userDao()

    // Community
    val allPosts: Flow<List<CommunityPostEntity>> = communityDao.getAllPosts()

    fun getPostsByCategory(category: String): Flow<List<CommunityPostEntity>> {
        return if (category == "Semua") {
            communityDao.getAllPosts()
        } else {
            communityDao.getPostsByCategory(category)
        }
    }

    suspend fun seedCommunityIfEmpty() {
        val count = communityDao.getPostCount()
        if (count == 0) {
            communityDao.insertPosts(CommunitySeedData.initialPosts)
        }
    }

    suspend fun addPost(authorName: String, title: String, content: String, category: String) {
        val newPost = CommunityPostEntity(
            authorName = authorName.ifBlank { "Hamba Allah" },
            authorBadge = "Sahabat Hijrah",
            title = title,
            content = content,
            category = category,
            timestamp = System.currentTimeMillis()
        )
        communityDao.insertPost(newPost)
    }

    suspend fun toggleLike(post: CommunityPostEntity) {
        if (post.isLikedByUser) {
            communityDao.unlikePost(post.id)
        } else {
            communityDao.likePost(post.id)
        }
    }

    suspend fun toggleBookmark(post: CommunityPostEntity) {
        communityDao.setBookmark(post.id, !post.isBookmarked)
    }

    fun getComments(postId: Long): Flow<List<CommunityCommentEntity>> {
        return communityDao.getCommentsForPost(postId)
    }

    suspend fun addComment(postId: Long, authorName: String, commentText: String) {
        val comment = CommunityCommentEntity(
            postId = postId,
            authorName = authorName.ifBlank { "Hamba Allah" },
            commentText = commentText,
            timestamp = System.currentTimeMillis()
        )
        communityDao.insertComment(comment)
        communityDao.incrementCommentCount(postId)
    }

    // Chat AI History
    val chatMessages: Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()

    suspend fun sendChatMessage(userText: String): Result<String> {
        // Save user message
        chatDao.insertMessage(
            ChatMessageEntity(sender = "USER", messageText = userText)
        )

        // Fetch recent context
        val messages = chatDao.getAllMessages().firstOrNull() ?: emptyList()
        val historyPairs = messages.chunked(2).mapNotNull { chunk ->
            if (chunk.size == 2 && chunk[0].sender == "USER" && chunk[1].sender == "AI") {
                chunk[0].messageText to chunk[1].messageText
            } else null
        }

        val result = geminiRepo.askIslamicQuestion(userText, historyPairs)
        val aiReply = result.getOrElse { error ->
            "Maaf, terjadi kendala saat memproses jawaban: ${error.localizedMessage ?: "Silakan coba lagi."}"
        }

        // Save AI reply
        chatDao.insertMessage(
            ChatMessageEntity(sender = "AI", messageText = aiReply)
        )

        return result
    }

    suspend fun clearChat() {
        chatDao.clearAllMessages()
    }

    // Prayer Checklist
    fun getPrayerChecks(datePrefix: String): Flow<List<PrayerCheckEntity>> {
        return prayerCheckDao.getChecksForDate(datePrefix)
    }

    suspend fun togglePrayerCheck(key: String, currentChecked: Boolean) {
        if (currentChecked) {
            prayerCheckDao.removeCheck(key)
        } else {
            prayerCheckDao.setCheck(PrayerCheckEntity(key = key, isCompleted = true))
        }
    }

    // ==========================================
    // User Authentication & Recovery
    // ==========================================
    fun getUserFlow(userId: Long): Flow<UserEntity?> = userDao.getUserByIdFlow(userId)

    suspend fun getUserById(userId: Long): UserEntity? = userDao.getUserById(userId)

    suspend fun registerUser(
        name: String,
        phone: String,
        email: String,
        password: String,
        birthDate: String,
        securityQuestion: String,
        securityAnswer: String
    ): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.trim()
        val cleanName = name.trim().ifBlank { "Sahabat Hijrah" }
        val cleanBirthDate = birthDate.trim()
        val cleanAnswer = securityAnswer.trim()

        if (cleanPhone.isBlank()) {
            return Result.failure(Exception("Nomor telepon tidak boleh kosong"))
        }
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return Result.failure(Exception("Format email tidak valid"))
        }
        if (password.length < 4) {
            return Result.failure(Exception("Kata sandi minimal 4 karakter"))
        }
        if (cleanBirthDate.isBlank()) {
            return Result.failure(Exception("Tanggal lahir tidak boleh kosong (contoh: 27-05-1991)"))
        }
        if (cleanAnswer.isBlank()) {
            return Result.failure(Exception("Jawaban pertanyaan keamanan tidak boleh kosong"))
        }

        val existingEmail = userDao.getUserByEmail(cleanEmail)
        if (existingEmail != null) {
            return Result.failure(Exception("Email '$cleanEmail' sudah terdaftar"))
        }

        val existingPhone = userDao.getUserByPhone(cleanPhone)
        if (existingPhone != null) {
            return Result.failure(Exception("Nomor telepon '$cleanPhone' sudah terdaftar"))
        }

        val newUser = UserEntity(
            name = cleanName,
            phone = cleanPhone,
            email = cleanEmail,
            passwordHash = password,
            birthDate = cleanBirthDate,
            securityQuestion = securityQuestion,
            securityAnswer = cleanAnswer
        )

        val id = userDao.insertUser(newUser)
        val registeredUser = newUser.copy(id = id)
        return Result.success(registeredUser)
    }

    suspend fun loginUser(identifier: String, password: String): Result<UserEntity> {
        val cleanId = identifier.trim()
        if (cleanId.isBlank()) {
            return Result.failure(Exception("Email atau nomor telepon harus diisi"))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("Kata sandi harus diisi"))
        }

        val user = userDao.getUserByEmailOrPhone(cleanId)
            ?: return Result.failure(Exception("Akun tidak ditemukan. Silakan periksa kembali email atau nomor telepon Anda."))

        if (user.passwordHash != password) {
            return Result.failure(Exception("Kata sandi salah. Silakan coba lagi atau gunakan fitur Lupa Kata Sandi."))
        }

        return Result.success(user)
    }

    suspend fun resetPasswordWithRecovery(
        identifier: String,
        birthDate: String,
        securityAnswer: String,
        newPassword: String
    ): Result<UserEntity> {
        val cleanId = identifier.trim()
        val cleanBirthDate = birthDate.trim()
        val cleanAnswer = securityAnswer.trim()

        if (cleanId.isBlank()) {
            return Result.failure(Exception("Email atau nomor telepon tidak boleh kosong"))
        }
        if (cleanBirthDate.isBlank()) {
            return Result.failure(Exception("Tanggal lahir harus diisi (contoh: 27-05-1991)"))
        }
        if (cleanAnswer.isBlank()) {
            return Result.failure(Exception("Jawaban pertanyaan keamanan harus diisi"))
        }
        if (newPassword.length < 4) {
            return Result.failure(Exception("Kata sandi baru minimal 4 karakter"))
        }

        val user = userDao.getUserByEmailOrPhone(cleanId)
            ?: return Result.failure(Exception("Akun dengan data tersebut tidak ditemukan"))

        // Verify birth date & security answer
        val birthDateMatches = user.birthDate.trim().replace("/", "-").equals(cleanBirthDate.replace("/", "-"), ignoreCase = true)
        val answerMatches = user.securityAnswer.trim().equals(cleanAnswer, ignoreCase = true)

        if (!birthDateMatches) {
            return Result.failure(Exception("Tanggal lahir tidak cocok dengan data pendaftaran"))
        }
        if (!answerMatches) {
            return Result.failure(Exception("Jawaban pertanyaan keamanan tidak sesuai"))
        }

        val updated = user.copy(passwordHash = newPassword)
        userDao.updateUser(updated)
        return Result.success(updated)
    }

    suspend fun recoverEmail(
        phone: String,
        birthDate: String,
        securityAnswer: String
    ): Result<UserEntity> {
        val cleanPhone = phone.trim()
        val cleanBirthDate = birthDate.trim()
        val cleanAnswer = securityAnswer.trim()

        if (cleanPhone.isBlank()) {
            return Result.failure(Exception("Nomor telepon tidak boleh kosong"))
        }
        if (cleanBirthDate.isBlank()) {
            return Result.failure(Exception("Tanggal lahir harus diisi (contoh: 27-05-1991)"))
        }
        if (cleanAnswer.isBlank()) {
            return Result.failure(Exception("Jawaban pertanyaan keamanan harus diisi"))
        }

        val user = userDao.getUserByPhone(cleanPhone)
            ?: return Result.failure(Exception("Akun dengan nomor telepon '$cleanPhone' tidak ditemukan"))

        val birthDateMatches = user.birthDate.trim().replace("/", "-").equals(cleanBirthDate.replace("/", "-"), ignoreCase = true)
        val answerMatches = user.securityAnswer.trim().equals(cleanAnswer, ignoreCase = true)

        if (!birthDateMatches) {
            return Result.failure(Exception("Tanggal lahir tidak sesuai dengan data akun Anda"))
        }
        if (!answerMatches) {
            return Result.failure(Exception("Jawaban pertanyaan keamanan tidak sesuai"))
        }

        return Result.success(user)
    }
}

