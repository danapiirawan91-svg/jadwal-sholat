package com.example.data.prayer

data class CityLocation(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double,
    val isPopular: Boolean = false
)

object WorldwideCities {
    val defaultCity = CityLocation(
        name = "Jakarta",
        country = "Indonesia",
        latitude = -6.2088,
        longitude = 106.8456,
        timezoneOffsetHours = 7.0,
        isPopular = true
    )

    val cities = listOf(
        // Indonesia
        CityLocation("Jakarta", "Indonesia", -6.2088, 106.8456, 7.0, true),
        CityLocation("Surabaya", "Indonesia", -7.2575, 112.7521, 7.0, true),
        CityLocation("Bandung", "Indonesia", -6.9175, 107.6191, 7.0, true),
        CityLocation("Medan", "Indonesia", 3.5952, 98.6722, 7.0, true),
        CityLocation("Makassar", "Indonesia", -5.1477, 119.4327, 8.0, true),
        CityLocation("Yogyakarta", "Indonesia", -7.7956, 110.3695, 7.0, true),
        CityLocation("Semarang", "Indonesia", -6.9667, 110.4167, 7.0),
        CityLocation("Palembang", "Indonesia", -2.9909, 104.7565, 7.0),
        CityLocation("Banda Aceh", "Indonesia", 5.5483, 95.3238, 7.0, true),
        CityLocation("Denpasar (Bali)", "Indonesia", -8.6705, 115.2126, 8.0),
        CityLocation("Jayapura", "Indonesia", -2.5916, 140.6690, 9.0),
        CityLocation("Balikpapan", "Indonesia", -1.2379, 116.8529, 8.0),

        // Timur Tengah & Tanah Suci
        CityLocation("Makkah (Mecca)", "Arab Saudi", 21.4225, 39.8262, 3.0, true),
        CityLocation("Madinah (Medina)", "Arab Saudi", 24.5247, 39.5692, 3.0, true),
        CityLocation("Riyadh", "Arab Saudi", 24.7136, 46.6753, 3.0),
        CityLocation("Dubai", "Uni Emirat Arab", 25.2048, 55.2708, 4.0, true),
        CityLocation("Abu Dhabi", "Uni Emirat Arab", 24.4539, 54.3773, 4.0),
        CityLocation("Kairo (Cairo)", "Mesir", 30.0444, 31.2357, 2.0, true),
        CityLocation("Istanbul", "Turki", 41.0082, 28.9784, 3.0, true),
        CityLocation("Ankara", "Turki", 39.9334, 32.8597, 3.0),
        CityLocation("Amman", "Yordania", 31.9454, 35.9284, 3.0),
        CityLocation("Doha", "Qatar", 25.2854, 51.5310, 3.0),

        // Asia Tenggara & Asia
        CityLocation("Kuala Lumpur", "Malaysia", 3.1390, 101.6869, 8.0, true),
        CityLocation("Singapura", "Singapura", 1.3521, 103.8198, 8.0, true),
        CityLocation("Brunei (Bandar Seri Begawan)", "Brunei", 4.9031, 114.9398, 8.0),
        CityLocation("Bangkok", "Thailand", 13.7563, 100.5018, 7.0),
        CityLocation("Tokyo", "Jepang", 35.6762, 139.6503, 9.0, true),
        CityLocation("Seoul", "Korea Selatan", 37.5665, 126.9780, 9.0),
        CityLocation("Beijing", "Tiongkok", 39.9042, 116.4074, 8.0),
        CityLocation("Islamabad", "Pakistan", 33.6844, 73.0479, 5.0),
        CityLocation("Dhaka", "Bangladesh", 23.8103, 90.4125, 6.0),
        CityLocation("New Delhi", "India", 28.6139, 77.2090, 5.5),

        // Eropa & Amerika & Australia
        CityLocation("London", "Inggris", 51.5074, -0.1278, 1.0, true),
        CityLocation("Paris", "Prancis", 48.8566, 2.3522, 2.0),
        CityLocation("Berlin", "Jerman", 52.5200, 13.4050, 2.0),
        CityLocation("Amsterdam", "Belanda", 52.3676, 4.9041, 2.0),
        CityLocation("New York", "Amerika Serikat", 40.7128, -74.0060, -4.0, true),
        CityLocation("Los Angeles", "Amerika Serikat", 34.0522, -118.2437, -7.0),
        CityLocation("Chicago", "Amerika Serikat", 41.8781, -87.6298, -5.0),
        CityLocation("Toronto", "Kanada", 43.6532, -79.3832, -4.0),
        CityLocation("Sydney", "Australia", -33.8688, 151.2093, 10.0, true),
        CityLocation("Melbourne", "Australia", -37.8136, 144.9631, 10.0),
        CityLocation("Auckland", "Selandia Baru", -36.8485, 174.7633, 12.0),
        CityLocation("Cape Town", "Afrika Selatan", -33.9249, 18.4241, 2.0)
    )
}
