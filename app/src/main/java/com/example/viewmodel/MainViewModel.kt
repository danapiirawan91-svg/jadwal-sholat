package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AppPreferences
import com.example.data.local.ChatMessageEntity
import com.example.data.local.CommunityCommentEntity
import com.example.data.local.CommunityPostEntity
import com.example.data.local.UserEntity
import com.example.data.model.SholatGuideItem
import com.example.data.model.SholatGuideRepository
import com.example.data.prayer.CityLocation
import com.example.data.prayer.DayPrayerSchedule
import com.example.data.prayer.LocationHelper
import com.example.data.prayer.PrayerCalculator
import com.example.data.prayer.WorldwideCities
import com.example.data.repository.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = AppRepository(database)

    // Current navigation tab index: 0 = Jadwal Sholat, 1 = Panduan Sholat, 2 = Tanya Ustadz AI, 3 = Komunitas
    // Persisted across app restarts so closing the app does not reset the screen
    private val _selectedTab = MutableStateFlow(AppPreferences.getSelectedTab(application))
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTab.value = index
        AppPreferences.setSelectedTab(getApplication(), index)
    }

    // ==========================================
    // 0. USER AUTH & ACCOUNT STATE
    // ==========================================
    private val _currentUserId = MutableStateFlow(AppPreferences.getLoggedInUserId(application))

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentUser: StateFlow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        if (id > 0) repository.getUserFlow(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun registerUser(
        name: String,
        phone: String,
        email: String,
        password: String,
        birthDate: String,
        securityQuestion: String,
        securityAnswer: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(
                name = name,
                phone = phone,
                email = email,
                password = password,
                birthDate = birthDate,
                securityQuestion = securityQuestion,
                securityAnswer = securityAnswer
            )
            result.onSuccess { user ->
                _currentUserId.value = user.id
                AppPreferences.setLoggedInUserId(getApplication(), user.id)
                onResult(true, "Pendaftaran berhasil! Ahlan wa Sahlan, ${user.name}")
            }.onFailure { error ->
                onResult(false, error.localizedMessage ?: "Gagal mendaftarkan akun")
            }
        }
    }

    fun loginUser(identifier: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.loginUser(identifier, password)
            result.onSuccess { user ->
                _currentUserId.value = user.id
                AppPreferences.setLoggedInUserId(getApplication(), user.id)
                onResult(true, "Berhasil masuk! Ahlan wa Sahlan, ${user.name}")
            }.onFailure { error ->
                onResult(false, error.localizedMessage ?: "Gagal masuk")
            }
        }
    }

    fun resetPassword(
        identifier: String,
        birthDate: String,
        securityAnswer: String,
        newPassword: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.resetPasswordWithRecovery(
                identifier, birthDate, securityAnswer, newPassword
            )
            result.onSuccess { user ->
                _currentUserId.value = user.id
                AppPreferences.setLoggedInUserId(getApplication(), user.id)
                onResult(true, "Kata sandi berhasil diperbarui!")
            }.onFailure { error ->
                onResult(false, error.localizedMessage ?: "Verifikasi gagal")
            }
        }
    }

    fun recoverEmail(
        phone: String,
        birthDate: String,
        securityAnswer: String,
        onResult: (Boolean, String, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.recoverEmail(phone, birthDate, securityAnswer)
            result.onSuccess { user ->
                onResult(true, "Email terdaftar Anda: ${user.email}", user.email)
            }.onFailure { error ->
                onResult(false, error.localizedMessage ?: "Verifikasi gagal", null)
            }
        }
    }

    fun logout() {
        _currentUserId.value = -1L
        AppPreferences.setLoggedInUserId(getApplication(), -1L)
    }

    // ==========================================
    // 1. PRAYER TIMES STATE & LOGIC
    // ==========================================
    private val savedLocationPair = LocationHelper.getSavedUserLocation(application)
    private val initialCity = savedLocationPair?.first ?: WorldwideCities.defaultCity

    private val _selectedCity = MutableStateFlow(initialCity)
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    private val _isGpsLocation = MutableStateFlow(savedLocationPair?.second ?: false)
    val isGpsLocation: StateFlow<Boolean> = _isGpsLocation.asStateFlow()

    private val _isDetectingLocation = MutableStateFlow(false)
    val isDetectingLocation: StateFlow<Boolean> = _isDetectingLocation.asStateFlow()

    private val savedMethod = try {
        PrayerCalculator.CalculationMethod.valueOf(AppPreferences.getCalcMethod(application))
    } catch (e: Exception) {
        PrayerCalculator.CalculationMethod.KEMENAG
    }

    private val _calcMethod = MutableStateFlow(savedMethod)
    val calcMethod: StateFlow<PrayerCalculator.CalculationMethod> = _calcMethod.asStateFlow()

    private val _prayerSchedule = MutableStateFlow(
        PrayerCalculator.calculatePrayerTimes(initialCity)
    )
    val prayerSchedule: StateFlow<DayPrayerSchedule> = _prayerSchedule.asStateFlow()

    private val _citySearchQuery = MutableStateFlow("")
    val citySearchQuery: StateFlow<String> = _citySearchQuery.asStateFlow()

    val filteredCities: StateFlow<List<CityLocation>> = _citySearchQuery.combine(
        MutableStateFlow(WorldwideCities.cities)
    ) { query, list ->
        if (query.isBlank()) {
            list.filter { it.isPopular }
        } else {
            list.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.country.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WorldwideCities.cities.filter { it.isPopular })

    private val todayPrefix: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val checkedPrayers = repository.getPrayerChecks(todayPrefix)
        .combine(MutableStateFlow(Unit)) { checks, _ ->
            checks.map { it.key.substringAfter("_") }.toSet()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun selectCity(city: CityLocation, fromGps: Boolean = false) {
        _selectedCity.value = city
        _isGpsLocation.value = fromGps
        LocationHelper.saveUserLocation(getApplication(), city, isGps = fromGps)
        refreshPrayerSchedule()
    }

    fun detectLocation() {
        if (_isDetectingLocation.value) return
        viewModelScope.launch {
            _isDetectingLocation.value = true
            try {
                val detected = LocationHelper.detectCurrentLocation(getApplication())
                if (detected != null) {
                    _selectedCity.value = detected
                    _isGpsLocation.value = true
                    refreshPrayerSchedule()
                }
            } finally {
                _isDetectingLocation.value = false
            }
        }
    }

    fun setCitySearchQuery(query: String) {
        _citySearchQuery.value = query
    }

    fun setCalculationMethod(method: PrayerCalculator.CalculationMethod) {
        _calcMethod.value = method
        AppPreferences.setCalcMethod(getApplication(), method.name)
        refreshPrayerSchedule()
    }

    fun togglePrayerCheck(prayerName: String) {
        viewModelScope.launch {
            val key = "${todayPrefix}_$prayerName"
            val isChecked = checkedPrayers.value.contains(prayerName)
            repository.togglePrayerCheck(key, isChecked)
        }
    }

    private fun refreshPrayerSchedule() {
        _prayerSchedule.value = PrayerCalculator.calculatePrayerTimes(
            city = _selectedCity.value,
            calendar = Calendar.getInstance(),
            method = _calcMethod.value
        )
    }

    private var countdownJob: Job? = null

    private fun startCountdownTimer() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                refreshPrayerSchedule()
                delay(30000L) // Refresh every 30 seconds
            }
        }
    }

    // ==========================================
    // 2. SHOLAT GUIDE & TASBIH
    // ==========================================
    private val _guideCategory = MutableStateFlow(AppPreferences.getGuideCategory(application))
    val guideCategory: StateFlow<String> = _guideCategory.asStateFlow()

    private val _selectedGuideItem = MutableStateFlow<SholatGuideItem?>(null)
    val selectedGuideItem: StateFlow<SholatGuideItem?> = _selectedGuideItem.asStateFlow()

    // Digital Tasbih counter persisted
    private val _tasbihCount = MutableStateFlow(AppPreferences.getTasbihCount(application))
    val tasbihCount: StateFlow<Int> = _tasbihCount.asStateFlow()

    private val _tasbihTarget = MutableStateFlow(AppPreferences.getTasbihTarget(application))
    val tasbihTarget: StateFlow<Int> = _tasbihTarget.asStateFlow()

    fun incrementTasbih() {
        val newCount = if (_tasbihCount.value < _tasbihTarget.value) {
            _tasbihCount.value + 1
        } else {
            1 // loop around
        }
        _tasbihCount.value = newCount
        AppPreferences.setTasbihCount(getApplication(), newCount)
    }

    fun resetTasbih() {
        _tasbihCount.value = 0
        AppPreferences.setTasbihCount(getApplication(), 0)
    }

    fun setTasbihTarget(target: Int) {
        _tasbihTarget.value = target
        _tasbihCount.value = 0
        AppPreferences.setTasbihTarget(getApplication(), target)
        AppPreferences.setTasbihCount(getApplication(), 0)
    }

    fun setGuideCategory(category: String) {
        _guideCategory.value = category
        AppPreferences.setGuideCategory(getApplication(), category)
    }

    fun selectGuideItem(item: SholatGuideItem?) {
        _selectedGuideItem.value = item
    }

    // ==========================================
    // 3. TANYA JAWAB AI REAL-TIME (GEMINI)
    // ==========================================
    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _chatInputText = MutableStateFlow("")
    val chatInputText: StateFlow<String> = _chatInputText.asStateFlow()

    val quickQuestions = listOf(
        "Hukum sholat jamak & qashar bagi musafir?",
        "Batalkah wudhu jika bersentuhan kulit suami istri?",
        "Bagaimana cara qadha sholat fardhu yang terlewat?",
        "Hukum makmum masbuq saat imam sedang ruku'?",
        "Bolehkah sholat witir hanya 1 rakaat?"
    )

    fun onChatInputChanged(text: String) {
        _chatInputText.value = text
    }

    fun sendQuestion(questionText: String = _chatInputText.value) {
        val query = questionText.trim()
        if (query.isBlank() || _isAiLoading.value) return

        _chatInputText.value = ""
        _isAiLoading.value = true

        viewModelScope.launch {
            try {
                repository.sendChatMessage(query)
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    // ==========================================
    // 4. KOMUNITAS BERBAGI IBADAH
    // ==========================================
    private val _communityCategory = MutableStateFlow(AppPreferences.getCommunityCategory(application))
    val communityCategory: StateFlow<String> = _communityCategory.asStateFlow()

    val communityPosts: StateFlow<List<CommunityPostEntity>> = _communityCategory.combine(
        repository.allPosts
    ) { category, posts ->
        if (category == "Semua") posts else posts.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedPostForComments = MutableStateFlow<CommunityPostEntity?>(null)
    val selectedPostForComments: StateFlow<CommunityPostEntity?> = _selectedPostForComments.asStateFlow()

    private val _postComments = MutableStateFlow<List<CommunityCommentEntity>>(emptyList())
    val postComments: StateFlow<List<CommunityCommentEntity>> = _postComments.asStateFlow()

    fun setCommunityCategory(cat: String) {
        _communityCategory.value = cat
        AppPreferences.setCommunityCategory(getApplication(), cat)
    }

    fun toggleLikePost(post: CommunityPostEntity) {
        viewModelScope.launch {
            repository.toggleLike(post)
        }
    }

    fun toggleBookmarkPost(post: CommunityPostEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(post)
        }
    }

    fun openCommentsForPost(post: CommunityPostEntity) {
        _selectedPostForComments.value = post
        viewModelScope.launch {
            repository.getComments(post.id).collect {
                _postComments.value = it
            }
        }
    }

    fun closeComments() {
        _selectedPostForComments.value = null
        _postComments.value = emptyList()
    }

    fun submitComment(postId: Long, authorName: String, commentText: String) {
        if (commentText.isBlank()) return
        val effectiveAuthor = authorName.ifBlank { currentUser.value?.name ?: "Hamba Allah" }
        viewModelScope.launch {
            repository.addComment(postId, effectiveAuthor, commentText)
        }
    }

    fun createNewPost(authorName: String, title: String, content: String, category: String) {
        if (title.isBlank() || content.isBlank()) return
        val effectiveAuthor = authorName.ifBlank { currentUser.value?.name ?: "Sahabat Hijrah" }
        viewModelScope.launch {
            repository.addPost(effectiveAuthor, title, content, category)
        }
    }

    init {
        viewModelScope.launch {
            repository.seedCommunityIfEmpty()
        }
        startCountdownTimer()
        if (LocationHelper.hasLocationPermission(application)) {
            detectLocation()
        }
    }
}
