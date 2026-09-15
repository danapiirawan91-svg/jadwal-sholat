package com.example.data.prayer

data class PrayerTimeItem(
    val name: String,
    val arabicName: String,
    val timeFormatted: String,
    val hour: Int,
    val minute: Int,
    val isPast: Boolean = false,
    val isNext: Boolean = false,
    val isCurrent: Boolean = false
)

data class DayPrayerSchedule(
    val cityName: String,
    val country: String,
    val dateString: String,
    val hijriString: String,
    val imsak: String,
    val subuh: String,
    val terbit: String,
    val dzuhur: String,
    val ashar: String,
    val maghrib: String,
    val isya: String,
    val qiblaBearing: Double,
    val items: List<PrayerTimeItem>,
    val nextPrayerName: String,
    val nextPrayerTime: String,
    val timeRemainingString: String,
    val progressToNextPrayer: Float
)
