package com.example.data.prayer

import java.util.Calendar
import java.util.Locale
import kotlin.math.*

object PrayerCalculator {

    enum class CalculationMethod(
        val displayName: String,
        val fajrAngle: Double,
        val ishaAngle: Double
    ) {
        KEMENAG("Kemenag RI", 20.0, 18.0),
        MWL("Muslim World League", 18.0, 17.0),
        ISNA("Islamic Society of North America", 15.0, 15.0),
        EGYPT("Egyptian General Authority", 19.5, 17.5),
        MAKKAH("Umm al-Qura (Makkah)", 18.5, 19.0),
        KARACHI("Univ. of Islamic Sciences, Karachi", 18.0, 18.0)
    }

    /**
     * Calculates prayer times for a given city and date.
     */
    fun calculatePrayerTimes(
        city: CityLocation,
        calendar: Calendar = Calendar.getInstance(),
        method: CalculationMethod = CalculationMethod.KEMENAG
    ): DayPrayerSchedule {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val timezone = city.timezoneOffsetHours
        val lat = city.latitude
        val lng = city.longitude

        // Julian date
        val d = julianDate(year, month, day) - 2451545.0

        // Sun calculations
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(toRadians(g)) + 0.020 * sin(toRadians(2 * g)))

        val e = 23.439 - 0.00000036 * d
        val dd = toDegrees(asin(sin(toRadians(e)) * sin(toRadians(l))))
        val ra = toDegrees(atan2(cos(toRadians(e)) * sin(toRadians(l)), cos(toRadians(l)))) / 15.0
        val eqt = q / 15.0 - fixHour(ra)

        // Midday (Dhuhr) in fractional hours
        val dhuhrFrac = fixHour(12.0 + timezone - lng / 15.0 - eqt)

        // Sunrise and Sunset (refraction angle 0.833)
        val sunTransit = sunAngleHour(lat, dd, 0.833)
        val sunriseFrac = dhuhrFrac - sunTransit
        val sunsetFrac = dhuhrFrac + sunTransit

        // Fajr & Isha
        val fajrTransit = sunAngleHour(lat, dd, method.fajrAngle)
        val fajrFrac = dhuhrFrac - fajrTransit

        val ishaTransit = sunAngleHour(lat, dd, method.ishaAngle)
        val ishaFrac = dhuhrFrac + ishaTransit

        // Asr (Shafi'i shadow length = 1)
        val asrAngle = toDegrees(atan(1.0 / (1.0 + tan(toRadians(abs(lat - dd))))))
        val asrTransit = sunAngleHour(lat, dd, asrAngle)
        val asrFrac = dhuhrFrac + asrTransit

        // Imsak is 10 minutes before Fajr
        val imsakFrac = fajrFrac - (10.0 / 60.0)

        // Safety offset (Ikhtiyat) +2 minutes for Dhuhr, Asr, Maghrib, Isya, Subuh
        val subuhTime = formatTime(fajrFrac + 2.0 / 60.0)
        val imsakTime = formatTime(imsakFrac + 2.0 / 60.0)
        val terbitTime = formatTime(sunriseFrac)
        val dzuhurTime = formatTime(dhuhrFrac + 2.0 / 60.0)
        val asharTime = formatTime(asrFrac + 2.0 / 60.0)
        val maghribTime = formatTime(sunsetFrac + 2.0 / 60.0)
        val isyaTime = formatTime(ishaFrac + 2.0 / 60.0)

        // Qibla bearing
        val qibla = calculateQibla(lat, lng)

        // Date strings
        val dateString = String.format(
            Locale("id", "ID"),
            "%02d %s %04d",
            day,
            getMonthNameIndo(month),
            year
        )
        val hijriString = approximateHijriDate(calendar)

        // Evaluate next prayer and countdown
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTotalMinutes = currentHour * 60 + currentMinute

        val prayerEntries = listOf(
            Triple("Subuh", "الفجر", parseMinutes(subuhTime)),
            Triple("Terbit", "الشروق", parseMinutes(terbitTime)),
            Triple("Dzuhur", "الظهر", parseMinutes(dzuhurTime)),
            Triple("Ashar", "العصر", parseMinutes(asharTime)),
            Triple("Maghrib", "المغرب", parseMinutes(maghribTime)),
            Triple("Isya", "العشاء", parseMinutes(isyaTime))
        )

        // Find next prayer
        var nextTriple = prayerEntries.firstOrNull { it.third > currentTotalMinutes }
        val isNextDayFajr = nextTriple == null
        if (nextTriple == null) {
            nextTriple = prayerEntries.first() // Subuh tomorrow
        }

        val minutesRemaining = if (isNextDayFajr) {
            (24 * 60 - currentTotalMinutes) + nextTriple.third
        } else {
            nextTriple.third - currentTotalMinutes
        }

        val hoursRemaining = minutesRemaining / 60
        val minsRemaining = minutesRemaining % 60
        val remainingString = if (hoursRemaining > 0) {
            "$hoursRemaining jam $minsRemaining mnt lagi"
        } else {
            "$minsRemaining menit lagi"
        }

        // Progress calculation (approximate progress within current prayer window)
        val progress = (1.0f - (minutesRemaining.toFloat() / 240.0f)).coerceIn(0.05f, 0.95f)

        val items = prayerEntries.map { (name, arab, mins) ->
            val h = mins / 60
            val m = mins % 60
            val isNext = name == nextTriple.first
            val isPast = mins < currentTotalMinutes
            PrayerTimeItem(
                name = name,
                arabicName = arab,
                timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", h, m),
                hour = h,
                minute = m,
                isPast = isPast,
                isNext = isNext,
                isCurrent = isPast && !isNext && name != "Terbit"
            )
        }

        val nextTimeStr = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            nextTriple.third / 60,
            nextTriple.third % 60
        )

        return DayPrayerSchedule(
            cityName = city.name,
            country = city.country,
            dateString = dateString,
            hijriString = hijriString,
            imsak = imsakTime,
            subuh = subuhTime,
            terbit = terbitTime,
            dzuhur = dzuhurTime,
            ashar = asharTime,
            maghrib = maghribTime,
            isya = isyaTime,
            qiblaBearing = qibla,
            items = items,
            nextPrayerName = nextTriple.first,
            nextPrayerTime = nextTimeStr,
            timeRemainingString = remainingString,
            progressToNextPrayer = progress
        )
    }

    /**
     * Calculate Qibla angle from true North to Mecca (21.4225° N, 39.8262° E)
     */
    fun calculateQibla(lat: Double, lng: Double): Double {
        val meccaLat = 21.4225
        val meccaLng = 39.8262

        val phi1 = toRadians(lat)
        val phi2 = toRadians(meccaLat)
        val deltaLng = toRadians(meccaLng - lng)

        val y = sin(deltaLng)
        val x = cos(phi1) * tan(phi2) - sin(phi1) * cos(deltaLng)
        var qibla = toDegrees(atan2(y, x))
        if (qibla < 0) qibla += 360.0
        return (round(qibla * 10.0) / 10.0)
    }

    private fun parseMinutes(timeStr: String): Int {
        val parts = timeStr.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return h * 60 + m
    }

    private fun formatTime(hours: Double): String {
        var h = fixHour(hours)
        val totalMinutes = round(h * 60.0).toInt()
        val finalH = (totalMinutes / 60) % 24
        val finalM = totalMinutes % 60
        return String.format(Locale.getDefault(), "%02d:%02d", finalH, finalM)
    }

    private fun sunAngleHour(lat: Double, declination: Double, angle: Double): Double {
        val angleRad = toRadians(angle)
        val latRad = toRadians(lat)
        val decRad = toRadians(declination)
        val term = (-sin(angleRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
        val clamped = term.coerceIn(-1.0, 1.0)
        return toDegrees(acos(clamped)) / 15.0
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun fixHour(a: Double): Double {
        var res = a - 24.0 * floor(a / 24.0)
        if (res < 0) res += 24.0
        return res
    }

    private fun fixAngle(a: Double): Double {
        var res = a - 360.0 * floor(a / 360.0)
        if (res < 0) res += 360.0
        return res
    }

    private fun toRadians(deg: Double): Double = deg * Math.PI / 180.0
    private fun toDegrees(rad: Double): Double = rad * 180.0 / Math.PI

    private fun getMonthNameIndo(month: Int): String {
        return when (month) {
            1 -> "Januari"
            2 -> "Februari"
            3 -> "Maret"
            4 -> "April"
            5 -> "Mei"
            6 -> "Juni"
            7 -> "Juli"
            8 -> "Agustus"
            9 -> "September"
            10 -> "Oktober"
            11 -> "November"
            12 -> "Desember"
            else -> ""
        }
    }

    fun approximateHijriDate(calendar: Calendar): String {
        val gYear = calendar.get(Calendar.YEAR)
        val gMonth = calendar.get(Calendar.MONTH) + 1
        val gDay = calendar.get(Calendar.DAY_OF_MONTH)

        val jd = julianDate(gYear, gMonth, gDay)
        val l = (jd - 1948440 + 10632).toInt()
        val n = ((l - 1) / 10631).toInt()
        val l2 = l - 10631 * n + 354
        val j = (((10985 - l2) / 5316).toInt() * ((50 * l2) / 17719).toInt() +
                (l2 / 5670).toInt() * ((43 * l2) / 15238).toInt())
        val l3 = (l2 - ((30 - j) / 15).toInt() * ((17719 * j) / 50).toInt() -
                (j / 16).toInt() * ((15238 * j) / 43).toInt() + 29)
        val hMonth = ((24 * l3) / 709).toInt()
        val hDay = l3 - ((709 * hMonth) / 24).toInt()
        val hYear = 30 * n + j - 30

        val hijriMonths = listOf(
            "Muharram", "Safar", "Rabi'ul Awwal", "Rabi'ul Akhir",
            "Jumadil Ula", "Jumadil Akhir", "Rajab", "Sya'ban",
            "Ramadhan", "Syawwal", "Dzulqa'dah", "Dzulhijjah"
        )
        val mName = hijriMonths.getOrElse((hMonth - 1).coerceIn(0, 11)) { "Hijriyah" }
        return "$hDay $mName $hYear H"
    }
}
