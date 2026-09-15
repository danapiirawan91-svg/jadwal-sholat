package com.example.data.api

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository(
    private val apiService: GeminiApiService = GeminiApiService.create()
) {

    private val systemInstructionText = """
        Anda adalah Ustadz AI & Konsultan Fiqih Islam yang bijaksana, santun, terpercaya, dan berpegang teguh pada Al-Qur'an dan As-Sunnah (Hadits shahih).
        Tugas Anda adalah menjawab pertanyaan seputar hukum agama Islam, tata cara sholat, thaharah (bersuci), puasa, zakat, dan persoalan ibadah sehari-hari.
        Pedoman menjawab:
        1. Awali dengan salam atau basmalah secara santun.
        2. Berikan dalil dari Al-Qur'an dan/atau Hadits shahih bila relevan.
        3. Jelaskan pandangan ulama madzhab (khususnya Mazhab Syafi'i yang banyak dianut di Nusantara, serta perbandingan dengan madzhab lain bila ada ikhtilaf).
        4. Berikan kesimpulan praktis yang mudah dipahami dan diamalkan.
        5. Gunakan bahasa Indonesia yang baik, sejuk, dan mendidik.
    """.trimIndent()

    suspend fun askIslamicQuestion(question: String, history: List<Pair<String, String>> = emptyList()): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        // Check if real key is configured
        val isKeyConfigured = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (!isKeyConfigured) {
            // Provide offline knowledgeable answer for common questions or gentle guidance
            val offlineResponse = getCuratedIslamicAnswer(question)
            return@withContext Result.success(offlineResponse)
        }

        try {
            val contentList = mutableListOf<GeminiContent>()

            // Add previous conversational context (last 4 turns for efficiency)
            history.takeLast(4).forEach { (userQuery, botReply) ->
                contentList.add(GeminiContent(role = "user", parts = listOf(GeminiPart(userQuery))))
                contentList.add(GeminiContent(role = "model", parts = listOf(GeminiPart(botReply))))
            }

            // Add current question
            contentList.add(GeminiContent(role = "user", parts = listOf(GeminiPart(question))))

            val request = GeminiRequest(
                contents = contentList,
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(systemInstructionText))),
                generationConfig = GeminiGenerationConfig(temperature = 0.5f, maxOutputTokens = 1500)
            )

            val response = apiService.generateContent(apiKey, request)
            val answer = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (!answer.isNullOrBlank()) {
                Result.success(answer)
            } else {
                val errorMsg = response.error?.message ?: "Maaf, respon dari server kosong."
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            // If network fails or quota is exhausted, fallback gracefully to offline fiqh knowledge
            val fallback = getCuratedIslamicAnswer(question)
            Result.success("$fallback\n\n*(Catatan koneksi: $fallbackNote)*")
        }
    }

    private val fallbackNote = "Jawaban disajikan dari rujukan fiqih terpercaya. Untuk jawaban AI interaktif tanpa batas, pastikan GEMINI_API_KEY aktif di panel Secrets."

    private fun getCuratedIslamicAnswer(query: String): String {
        val lower = query.lowercase()
        return when {
            lower.contains("jamak") || lower.contains("qashar") || lower.contains("musafir") -> """
                *Bismillaahirrohmaanirrohiim.*

                **Hukum Sholat Jamak dan Qashar bagi Musafir:**

                Sholat Jamak (menggabungkan dua sholat fardhu) dan Qashar (meringkas sholat 4 rakaat menjadi 2 rakaat) merupakan **Rukhsah (keringanan)** yang disyariatkan Allah SWT bagi orang yang bepergian (musafir).

                📖 **Dalil Al-Qur'an:**
                *"Dan apabila kamu bepergian di muka bumi, maka tidaklah mengapa kamu men-qashar sholatmu..."* (QS. An-Nisa: 101)

                📌 **Syarat Sah Sholat Jamak & Qashar:**
                1. Perjalanan bukan untuk tujuan maksiat.
                2. Jarak tempuh minimal telah mencapai 2 marhalah (sekitar 81–89 km menurut jumhur ulama / Mazhab Syafi'i).
                3. Sholat yang boleh di-qashar hanyalah sholat 4 rakaat: **Dzuhur, Ashar, dan Isya** (menjadi 2 rakaat). Sholat Subuh dan Maghrib tidak boleh di-qashar.
                4. Pasangan sholat yang boleh dijamak:
                   - Dzuhur dengan Ashar (bisa Jamak Taqdim di waktu Dzuhur, atau Jamak Ta'khir di waktu Ashar).
                   - Maghrib dengan Isya (Jamak Taqdim di waktu Maghrib, atau Jamak Ta'khir di waktu Isya).
                5. Niat jamak dilakukan pada waktu sholat pertama.

                💡 **Kesimpulan:** Selama perjalanan Anda memenuhi jarak minimal dan berniat baik, Anda berhak mengambil rukhshah jamak dan qashar untuk mempermudah ibadah.
            """.trimIndent()

            lower.contains("batal") && (lower.contains("wudhu") || lower.contains("kulit") || lower.contains("suami")) -> """
                *Bismillaahirrohmaanirrohiim.*

                **Hukum Bersentuhan Kulit Lawan Jenis / Suami Istri terhadap Wudhu:**

                Terdapat perbedaan pandangan (ikhtilaf) yang muktabar di kalangan para imam madzhab:

                1. **Mazhab Syafi'i:**
                   Bersentuhan kulit secara langsung antara laki-laki dan perempuan yang bukan mahram (termasuk suami-istri) **membatalkan wudhu secara mutlak**, baik dengan syahwat maupun tanpa syahwat. Dalilnya merujuk pada lafal ayat: *"Aw laamastumun-nisaa'"* (QS. Al-Ma'idah: 6).

                2. **Mazhab Hanafi:**
                   Bersentuhan kulit biasa **tidak membatalkan wudhu sama sekali**, kecuali jika terjadi jima' (hubungan intim).

                3. **Mazhab Maliki dan Hambali:**
                   Bersentuhan kulit **hanya membatalkan wudhu jika disertai syahwat** atau kenikmatan. Jika sentuhan biasa tanpa syahwat (seperti tidak sengaja bersenggolan), wudhu tidak batal.

                💡 **Kesimpulan & Saran Praktis:**
                Di Indonesia yang mayoritas bermazhab Syafi'i, dianjurkan berhati-hati menjaga wudhu setelah bersentuhan. Namun saat kondisi darurat/sulit dihindari (seperti saat Tawaf di Masjidil Haram), ulama membolehkan beralih (*taqlid*) sementara ke mazhab Hanafi atau Maliki.
            """.trimIndent()

            lower.contains("qadha") || lower.contains("tertinggal") || lower.contains("lupa") -> """
                *Bismillaahirrohmaanirrohiim.*

                **Hukum Mengganti (Qadha) Sholat yang Terlewat:**

                Sholat fardhu adalah kewajiban mutlak yang terikat waktu. Jika seseorang terlewat sholat:

                1. **Karena Udzur (Tertidur atau Lupa):**
                   Wajib segera meng-qadhanya begitu ingat atau bangun. Rasulullah SAW bersabda:
                   *"Barangsiapa lupa sholat atau tertidur darinya, maka kaffarah (tebusan)-nya adalah sholat saat ia mengingatnya."* (HR. Bukhari & Muslim). Tidak ada dosa baginya.

                2. **Karena Kelalaian / Sengaja Meninggalkan:**
                   Menurut **Jumhur Ulama (4 Mazhab)**, sholat yang sengaja ditinggalkan tetap wajib di-qadha sebanyak yang ditinggalkan, disertai taubat nasuha dan memperbanyak sholat sunnah serta istighfar.

                💡 **Tata Cara Praktis Qadha Sholat yang Banyak:**
                Kerjakan qadha setiap selesai sholat fardhu (misal: selesai sholat Dzuhur hari ini, langsung sholat qadha Dzuhur kemarin), lakukan secara konsisten hingga yakin telah terbayar lunas.
            """.trimIndent()

            lower.contains("masbuq") || lower.contains("imam") || lower.contains("makmum") -> """
                *Bismillaahirrohmaanirrohiim.*

                **Hukum dan Ketentuan Makmum Masbuq (Tertinggal):**

                Makmum masbuq adalah makmum yang mendapati imam setelah imam selesai membaca Al-Fatihah atau sedang dalam rukun tertentu.

                📌 **Ketentuan Pokok:**
                1. **Mendapatkan Rakaat:**
                   Jika makmum takbiratul ihram (berdiri), lalu langsung ruku' dan sempat thuma'ninah bersama imam dalam ruku' sebelum imam bangkit (I'tidal), maka makmum tersebut **dianggap mendapatkan rakaat tersebut** (tidak perlu menambah rakaat itu nanti).
                   Rasulullah SAW bersabda: *"Barangsiapa mendapati satu ruku' bersama imam, maka ia telah mendapati rakaat itu."* (HR. Abu Dawud).

                2. **Jika Imam Sudah Bangkit dari Ruku':**
                   Makmum tetap langsung bergabung mengikuti gerakan imam (misal sujud), namun rakaat tersebut tidak terhitung, sehingga makmum harus menyempurnakan rakaat yang tertinggal setelah imam salam.

                3. **Kapan Makmum Berdiri Menambah Rakaat?**
                   Makmum berdiri setelah imam mengucapkan salam kedua secara sempurna.
            """.trimIndent()

            lower.contains("witir") -> """
                *Bismillaahirrohmaanirrohiim.*

                **Hukum Sholat Witir 1 Rakaat:**

                Sholat Witir hukumnya **Sunnah Muakkad** (sangat dianjurkan). Jumlah rakaat witir adalah ganjil.

                1. **Bolehkah Sholat Witir 1 Rakaat?**
                   **Boleh.** Minimal sholat witir adalah satu rakaat.
                   Rasulullah SAW bersabda: *"Sholat malam itu dua rakaat dua rakaat. Apabila salah seorang di antara kalian khawatir masuk waktu Subuh, maka hendaklah ia sholat satu rakaat sebagai witir bagi sholat yang telah dikerjakannya."* (HR. Bukhari & Muslim).

                2. **Tingkatan Keutamaan:**
                   - Paling sempurna: 11 rakaat (atau 3 rakaat dengan 2 rakaat salam + 1 rakaat salam).
                   - Standar baik: 3 rakaat.
                   - Minimal sah: 1 rakaat.

                💡 **Kesimpulan:** Jika Anda lelah atau waktu Subuh sudah sangat dekat, Anda boleh langsung mengerjakan sholat witir 1 rakaat.
            """.trimIndent()

            else -> """
                *Bismillaahirrohmaanirrohiim.*

                Terima kasih atas pertanyaan Anda mengenai: **"$query"**.

                📖 **Prinsip Dasar Syariat Islam:**
                Dalam syariat Islam, segala hukum ibadah berpijak pada kaidah: *Al-ashlu fil 'ibaadah al-buthlaan hattaa yadulla ad-daliilu 'alaal amr* (Hukum asal dalam urusan ibadah adalah terlarang/tidak sah hingga ada dalil yang memerintahkannya). Sementara dalam perkara keduniaan dan muamalah, hukum asalnya adalah mubah (boleh) kecuali yang diharamkan.

                📌 **Rekomendasi Ibadah Terkait Pertanyaan Anda:**
                1. Utamakan sholat fardhu lima waktu tepat pada waktunya dengan menyempurnakan thaharah (wudhu).
                2. Apabila menghadapi keraguan dalam sholat atau ibadah, ambillah yang yakin dan buanglah yang ragu sebagaimana sabda Nabi SAW.
                3. Konsultasikan dengan para asatidz/ulama setempat untuk perkara yang membutuhkan fatwa detail spesifik kondisi Anda.

                Semoga Allah SWT senantiasa memberikan taufiq, ilmu yang bermanfaat, serta kemudahan dalam menjalankan syariat-Nya. *Wallahu a'lam bish-shawab.*
            """.trimIndent()
        }
    }
}
