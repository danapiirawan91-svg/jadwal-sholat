package com.example.data.model

import com.example.data.local.CommunityPostEntity

object CommunitySeedData {
    val initialPosts = listOf(
        CommunityPostEntity(
            id = 1,
            authorName = "Fajar Pratama",
            authorBadge = "Alumni Santri",
            title = "Kekuatan Istiqomah Sholat Subuh Berjamaah di Masjid",
            content = "Dulu saya sering terlambat sholat Subuh karena begadang. Sejak 6 bulan lalu bertekad sholat Subuh di masjid terdekat, rezeki terasa mengalir lebih berkah dan pikiran selalu tenang menghadapi hari. Kuncinya: tidur sebelum jam 10 malam dan minta dibangunkan malaikat lewat doa sebelum tidur. Mari sama-sama jaga Subuh kita!",
            category = "Tips Sholat Khusyuk",
            timestamp = System.currentTimeMillis() - 3600000L * 5,
            likesCount = 42,
            commentsCount = 7,
            isLikedByUser = false,
            isBookmarked = false
        ),
        CommunityPostEntity(
            id = 2,
            authorName = "Nurul Azizah",
            authorBadge = "Sahabat Hijrah",
            title = "Miracle of Tahajjud: Doa yang Menembus Langit",
            content = "Ketika menghadapi masalah yang terasa buntu tahun lalu, saya membiasakan bangun di sepertiga malam terakhir, sholat Tahajjud 2 rakaat lalu witir. Menumpahkan seluruh air mata saat sujud. Sungguh Allah tidak pernah tidur, satu per satu jalan keluar terbuka dari arah yang tak disangka-sangka. Jangan pernah berputus asa dari rahmat-Nya.",
            category = "Pengalaman Ibadah",
            timestamp = System.currentTimeMillis() - 3600000L * 18,
            likesCount = 89,
            commentsCount = 14,
            isLikedByUser = true,
            isBookmarked = true
        ),
        CommunityPostEntity(
            id = 3,
            authorName = "Ahmad Zaki",
            authorBadge = "Pegiat Dakwah",
            title = "Cara Menjaga Pikiran Tetap Khusyuk Saat Sholat",
            content = "Banyak yang bertanya bagaimana cara agar tidak mikirin pekerjaan saat sholat. Salah satu tips terbaik: hayati arti bacaan Al-Fatihah ayat per ayat, luangkan waktu 1 menit sebelum takbiratul ihram untuk 'menenangkan napas' dan menyadari bahwa kita sedang berdiri langsung di hadapan Raja Diraja Penguasa Alam Semesta.",
            category = "Tips Sholat Khusyuk",
            timestamp = System.currentTimeMillis() - 3600000L * 30,
            likesCount = 63,
            commentsCount = 9,
            isLikedByUser = false,
            isBookmarked = false
        ),
        CommunityPostEntity(
            id = 4,
            authorName = "Siti Rahmah",
            authorBadge = "Ibu Rumah Tangga",
            title = "Doa Bersama untuk Kesembuhan Saudara Kita",
            content = "Bismillah, mohon keikhlasan doa dari sahabat semua untuk ibu mertua saya yang saat ini sedang dirawat di ruang ICU. Semoga Allah angkat penyakitnya, memberikan kesembuhan yang sempurna (syifa-an 'aajilan laa yughaadiru saqaman). Jazaakumullaahu khayran katsiiran.",
            category = "Doa & Motivasi",
            timestamp = System.currentTimeMillis() - 3600000L * 45,
            likesCount = 115,
            commentsCount = 28,
            isLikedByUser = false,
            isBookmarked = false
        )
    )
}
