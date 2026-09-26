package com.example

import com.example.data.prayer.CityLocation
import com.example.data.prayer.PrayerCalculator
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

/**
 * Unit test to verify prayer time calculation accuracy.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAsharPrayerTimeCalculation_Surabaya() {
    val surabaya = CityLocation(
      name = "Surabaya",
      country = "Indonesia",
      latitude = -7.2575,
      longitude = 112.7521,
      timezoneOffsetHours = 7.0
    )
    val cal = Calendar.getInstance().apply {
      set(2026, Calendar.SEPTEMBER, 16, 12, 0, 0)
    }
    val schedule = PrayerCalculator.calculatePrayerTimes(surabaya, cal)

    // Ashar in Surabaya in September should be around 14:40 - 14:45, definitely NOT 20:07!
    val asharHour = schedule.items.first { it.name == "Ashar" }.hour
    val asharMinute = schedule.items.first { it.name == "Ashar" }.minute
    assertEquals(14, asharHour)
    assertTrue("Ashar minute should be between 40 and 46, got: $asharMinute", asharMinute in 40..46)
    assertTrue("Ashar time should be around 14:42-14:44, got ${schedule.ashar}", schedule.ashar.startsWith("14:"))
  }

  @Test
  fun testAsharPrayerTimeCalculation_Jakarta() {
    val jakarta = CityLocation(
      name = "Jakarta",
      country = "Indonesia",
      latitude = -6.2088,
      longitude = 106.8456,
      timezoneOffsetHours = 7.0
    )
    val cal = Calendar.getInstance().apply {
      set(2026, Calendar.SEPTEMBER, 16, 12, 0, 0)
    }
    val schedule = PrayerCalculator.calculatePrayerTimes(jakarta, cal)

    val asharHour = schedule.items.first { it.name == "Ashar" }.hour
    assertEquals(15, asharHour)
    assertTrue("Ashar time should start with 15:, got ${schedule.ashar}", schedule.ashar.startsWith("15:"))
  }

  @Test
  fun testDecryptSmaliStrings() {
    val title = botX.OoOo.decrypt("AIWKHg/+q5wck+FwM3Bi0zB+OGxSS/q7l47B3i/b3bI=", "🐯@Danapi irawan@🐯")
    val message = botX.OoOo.decrypt("OcFztNwZ05wR5ml6hgmmvLIMknTPyLk+ge5r6f92e1o=", "Selamat datang di panduan islam")
    val btnOk = botX.OoOo.decrypt("6G345hb4KOIMEhHcu7J/1g==", "ok")
    val btnWa = botX.OoOo.decrypt("AOKofieRVuLAoSNGEH7maA==", "WhatsApp")
    val waUrl = botX.OoOo.decrypt("H4qhUAr1hrrxPpUEJJEjOwKAPFZQrSa66T+t8/BNLkc=", "wa.me/6281554752875")

    assertEquals("🐯@Danapi irawan@🐯", title)
    assertEquals("Selamat datang di panduan islam", message)
    assertEquals("ok", btnOk)
    assertEquals("WhatsApp", btnWa)
    assertEquals("wa.me/6281554752875", waUrl)

    val secureData = botX.OoOo.resolveSecureData()
    assertEquals("🐯@Danapi irawan@🐯", secureData.title)
    assertEquals("Selamat datang di panduan islam", secureData.message)
    assertEquals("ok", secureData.btnOk)
    assertEquals("WhatsApp", secureData.btnWa)
    assertEquals("https://wa.me/6281554752875", secureData.waUrl)
    assertTrue("Encrypted data must pass anti-tamper authenticity check", secureData.isAuthentic)
  }

  @Test
  fun testGeminiApiKeyConfigured() {
    assertNotNull(BuildConfig.GEMINI_API_KEY)
    assertNotEquals("MY_GEMINI_API_KEY", BuildConfig.GEMINI_API_KEY)
    assertTrue("GEMINI_API_KEY should be non-empty and start with AQ.", BuildConfig.GEMINI_API_KEY.startsWith("AQ."))
  }
}

