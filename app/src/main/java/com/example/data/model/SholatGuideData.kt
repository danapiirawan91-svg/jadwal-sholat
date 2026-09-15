package com.example.data.model

data class PrayerStep(
    val stepNumber: Int,
    val title: String,
    val movementDescription: String,
    val arabicText: String,
    val latinText: String,
    val translation: String,
    val notes: String = ""
)

data class SholatGuideItem(
    val id: String,
    val name: String,
    val arabicName: String,
    val category: String, // "Wajib", "Sunnah", "Thaharah", "Dzikir"
    val rakaatInfo: String,
    val briefDescription: String,
    val niatArabic: String,
    val niatLatin: String,
    val niatTranslation: String,
    val steps: List<PrayerStep> = emptyList(),
    val specialDoaArabic: String? = null,
    val specialDoaLatin: String? = null,
    val specialDoaTranslation: String? = null
)

object SholatGuideRepository {

    val commonSteps = listOf(
        PrayerStep(
            stepNumber = 1,
            title = "Niat & Berdiri Tegak",
            movementDescription = "Berdiri tegak menghadap kiblat (bagi yang mampu). Pandangan mata diarahkan ke tempat sujud. Hadirkan niat ikhlas karena Allah SWT di dalam hati.",
            arabicText = "اُصَلِّيْ فَرْضَ ... رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ اَدَاءً لِلّٰهِ تَعَالَى",
            latinText = "Ushallii fardha... mustaqbilal qiblati adaa-an lillaahi Ta'aalaa",
            translation = "Aku berniat sholat fardhu... menghadap kiblat karena Allah Ta'ala.",
            notes = "Niat tempatnya di dalam hati pada saat mengawali Takbiratul Ihram."
        ),
        PrayerStep(
            stepNumber = 2,
            title = "Takbiratul Ihram",
            movementDescription = "Mengangkat kedua belah tangan sejajar telinga (bagi pria) atau dada (bagi wanita), telapak tangan terbuka menghadap kiblat, sembari mengucap takbir.",
            arabicText = "اللهُ أَكْبَرُ",
            latinText = "Allaahu Akbar",
            translation = "Allah Maha Besar.",
            notes = "Takbiratul Ihram adalah rukun qauliy (ucapan) yang menandai masuknya batas sholat."
        ),
        PrayerStep(
            stepNumber = 3,
            title = "Bersedekap & Doa Iftitah",
            movementDescription = "Meletakkan tangan kanan di atas pergelangan tangan kiri di antara dada dan pusar. Membaca Doa Iftitah dengan suara lirih.",
            arabicText = "اللهُ أَكْبَرُ كَبِيْرًا وَالْحَمْدُ لِلّٰهِ كَثِيْرًا وَسُبْحَانَ اللهِ بُكْرَةً وَأَصِيْلًا. إِنِّيْ وَجَّهْتُ وَجْهِيَ لِلَّذِيْ فَطَرَ السَّمَاوَاتِ وَالْأَرْضَ حَنِيْفًا مُسْلِمًا وَمَا أَنَا مِنَ الْمُشْرِكِيْنَ. إِنَّ صَلَاتِيْ وَنُسُكِيْ وَمَحْيَايَ وَمَمَاتِيْ لِلّٰهِ رَبِّ الْعَالَمِيْنَ. لَا شَرِيْكَ لَهُ وَبِذٰلِكَ أُمِرْتُ وَأَنَا مِنَ الْمُسْلِمِيْنَ.",
            latinText = "Allaahu akbar kabiiraa walhamdu lillaahi katsiiraa, wa subhaanallaahi bukrataw wa-ashiilaa. Innii wajjahtu wajhiya lilladzii fatharas samaawaati wal ardha haniifam muslimaw wamaa ana minal musyrikiin. Inna shalaatii wa nusukii wa mahyaaya wa mamaatii lillaahi rabbil 'aalamiin. Laa syariikalahu wa bidzaalika umirtu wa ana minal muslimiin.",
            translation = "Allah Maha Besar dengan sebesar-besarnya, segala puji bagi Allah sebanyak-banyaknya, Maha Suci Allah pada pagi dan petang. Sesungguhnya aku menghadapkan wajahku kepada Dzat yang menciptakan langit dan bumi dengan lurus dan berserah diri, dan aku bukanlah termasuk orang-orang musyrik. Sesungguhnya sholatku, ibadahku, hidupku dan matiku hanyalah untuk Allah Tuhan semesta alam...",
            notes = "Membaca doa Iftitah hukumnya Sunnah Muakkad."
        ),
        PrayerStep(
            stepNumber = 4,
            title = "Membaca Al-Fatihah & Surat Pendek",
            movementDescription = "Membaca Ta'awwudz, Basmalah, dan Surat Al-Fatihah dengan tartil (Rukun Sholat). Dilanjutkan dengan mengucapkan 'Aamiin', lalu membaca surat pendek dari Al-Qur'an pada rakaat ke-1 dan ke-2.",
            arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ ۝ الْحَمْدُ لِلّٰهِ رَبِّ الْعٰلَمِيْنَ ۝ الرَّحْمٰنِ الرَّحِيْمِ ۝ مٰلِكِ يَوْمِ الدِّيْنِ ۝ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِيْنُ ۝ اِهْدِنَا الصِّرَاطَ الْمُسْتَقِيْمَ ۝ صِرَاطَ الَّذِيْنَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوْبِ عَلَيْهِمْ وَلَا الضَّالِّيْنَ",
            latinText = "Bismillaahir-rahmaanir-rahiim. Al-hamdu lillaahi rabbil-'aalamiin. Ar-rahmaanir-rahiim. Maaliki yawmid-diin. Iyyaaka na'budu wa iyyaaka nasta'iin. Ihdinas-shiraathal-mustaqiim. Shiraathalladziina an'amta 'alayhim ghayril-maghdhuubi 'alayhim waladh-dhaalliin.",
            translation = "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang. Segala puji bagi Allah, Tuhan seluruh alam. Yang Maha Pengasih, Maha Penyayang. Pemilik hari pembalasan. Hanya kepada Engkaulah kami menyembah dan hanya kepada Engkaulah kami memohon pertolongan. Tunjukilah kami jalan yang lurus. Yaitu jalan orang-orang yang telah Engkau beri nikmat kepadanya; bukan jalan mereka yang dimurkai dan bukan pula jalan mereka yang sesat.",
            notes = "Al-Fatihah adalah rukun sholat, tidak sah sholat tanpa membacanya."
        ),
        PrayerStep(
            stepNumber = 5,
            title = "Ruku' & Thuma'ninah",
            movementDescription = "Mengangkat kedua tangan sambil mengucap 'Allaahu Akbar', lalu membungkukkan badan hingga punggung dan kepala sejajar lurus. Kedua telapak tangan memegang lutut. Diam sejenak (Thuma'ninah) membaca doa ruku' 3 kali.",
            arabicText = "سُبْحَانَ رَبِّيَ الْعَظِيْمِ وَبِحَمْدِهِ",
            latinText = "Subhaana rabbiyal 'azhiimi wa bihamdih (3x)",
            translation = "Maha Suci Tuhanku Yang Maha Agung dan dengan memuji-Nya.",
            notes = "Thuma'ninah (diam sejenak seukuran membaca tasbih) adalah rukun sholat."
        ),
        PrayerStep(
            stepNumber = 6,
            title = "I'tidal & Thuma'ninah",
            movementDescription = "Bangkit dari ruku' berdiri tegak kembali dengan mengangkat kedua tangan sembari membaca tasmi', lalu meluruskan tangan ke bawah atau bersedekap dengan thuma'ninah membaca tahmid.",
            arabicText = "سَمِعَ اللهُ لِمَنْ حَمِدَهُ. رَبَّنَا لَكَ الْحَمْدُ مِلْءُ السَّمٰوَاتِ وَمِلْءُ الْأَرْضِ وَمِلْءُ مَا شِئْتَ مِنْ شَيْءٍ بَعْدُ",
            latinText = "Sami'allaahu liman hamidah. Rabbanaa lakal hamdu mil-us samaawaati wa mil-ul ardhi wa mil-u maa syi'ta min syay-in ba'du.",
            translation = "Allah mendengar orang yang memuji-Nya. Wahai Tuhan kami! Bagi-Mu lah segala puji sepenuh langit dan sepenuh bumi dan sepenuh apa yang Engkau kehendaki sesudah itu.",
            notes = "Pada sholat Subuh rakaat kedua, setelah I'tidal dianjurkan membaca Doa Qunut."
        ),
        PrayerStep(
            stepNumber = 7,
            title = "Sujud Pertama & Thuma'ninah",
            movementDescription = "Bertakbir tanpa mengangkat tangan, lalu turun sujud. Tujuh anggota badan menempel ke lantai: dahi & hidung, kedua telapak tangan, kedua lutut, dan ujung jari jemari kedua kaki menghadap kiblat. Membaca doa sujud 3 kali.",
            arabicText = "سُبْحَانَ رَبِّيَ الْأَعْلَى وَبِحَمْدِهِ",
            latinText = "Subhaana rabbiyal a'laa wa bihamdih (3x)",
            translation = "Maha Suci Tuhanku Yang Maha Tinggi dan dengan memuji-Nya.",
            notes = "Sujud adalah posisi terdekat seorang hamba dengan Tuhannya."
        ),
        PrayerStep(
            stepNumber = 8,
            title = "Duduk Antara Dua Sujud (Iftirasy)",
            movementDescription = "Bangkit dari sujud sembari mengucap 'Allaahu Akbar', duduk di atas telapak kaki kiri, sedangkan telapak kaki kanan ditegakkan dengan jari-jarinya menghadap kiblat. Kedua telapak tangan di atas paha/lutut. Membaca doa:",
            arabicText = "رَبِّ اغْفِرْ لِيْ وَارْحَمْنِيْ وَاجْبُرْنِيْ وَارْفَعْنِيْ وَارْزُقْنِيْ وَاهْدِنِيْ وَعَافِنِيْ وَاعْفُ عَنِّيْ",
            latinText = "Rabbighfirlii warhamnii wajburnii warfa'nii warzuqnii wahdinii wa'aafinii wa'fu 'annii.",
            translation = "Wahai Tuhanku! Ampunilah aku, sayangilah aku, cukupkanlah kekuranganku, angkatlah derajatku, berilah aku rezeki, berilah aku petunjuk, sehatkanlah aku dan maafkanlah aku.",
            notes = "Duduk iftirasy disunnahkan pada duduk antara dua sujud dan tasyahud awal."
        ),
        PrayerStep(
            stepNumber = 9,
            title = "Sujud Kedua & Thuma'ninah",
            movementDescription = "Mengucap takbir lalu sujud kedua dengan cara dan bacaan yang sama persis dengan sujud pertama (3 kali membaca tasbih sujud).",
            arabicText = "سُبْحَانَ رَبِّيَ الْأَعْلَى وَبِحَمْدِهِ",
            latinText = "Subhaana rabbiyal a'laa wa bihamdih (3x)",
            translation = "Maha Suci Tuhanku Yang Maha Tinggi dan dengan memuji-Nya.",
            notes = "Menyelesaikan 1 rakaat secara sempurna."
        ),
        PrayerStep(
            stepNumber = 10,
            title = "Tasyahud Awal (Rakaat ke-2)",
            movementDescription = "Pada sholat 3 atau 4 rakaat, setelah rakaat kedua duduk iftirasy untuk Tasyahud Awal. Jari telunjuk tangan kanan diacungkan saat mengucap 'illallaah'. Membaca:",
            arabicText = "التَّحِيَّاتُ الْمُبَارَكَاتُ الصَّلَوَاتُ الطَّيِّبَاتُ لِلّٰهِ، السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللهِ وَبَرَكَاتُهُ، السَّلَامُ عَلَيْنَا وَعَلَى عِبَادِ اللهِ الصَّالِحِيْنَ، أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا رَسُوْلُ اللهِ، اللّٰهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ",
            latinText = "At-tahiyyaatul mubaarakaatush shalawaatuth thayyibaatu lillaah. As-salaamu 'alayka ayyuhan-nabiyyu wa rahmatullaahi wa barakaatuh. As-salaamu 'alaynaa wa 'alaa 'ibaadillaahish-shaalihiin. Asyhadu allaa ilaaha illallaah wa asyhadu anna Muhammadar rasuulullaah. Allaahumma shalli 'alaa sayyidinaa Muhammad.",
            translation = "Segala kehormatan, keberkahan, shalawat dan kebaikan adalah milik Allah. Semoga keselamatan, rahmat Allah dan berkah-Nya tercurah kepadamu wahai Nabi. Semoga keselamatan tercurah kepada kami dan kepada hamba-hamba Allah yang shalih. Aku bersaksi tiada tuhan selain Allah dan aku bersaksi Muhammad adalah utusan Allah. Ya Allah, limpahkanlah rahmat kepada junjungan kami Nabi Muhammad.",
            notes = "Tasyahud Awal tidak ada pada sholat 2 rakaat (seperti Subuh)."
        ),
        PrayerStep(
            stepNumber = 11,
            title = "Tasyahud Akhir (Tawarruk)",
            movementDescription = "Duduk Tawarruk (kaki kiri dimasukkan ke bawah betis kaki kanan, bokong menempel ke lantai, kaki kanan ditegakkan). Jari telunjuk diangkat menghadap kiblat. Membaca tasyahud dan shalawat Ibrahimiyyah lengkap:",
            arabicText = "التَّحِيَّاتُ الْمُبَارَكَاتُ الصَّلَوَاتُ الطَّيِّبَاتُ لِلّٰهِ، السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللهِ وَبَرَكَاتُهُ، السَّلَامُ عَلَيْنَا وَعَلَى عِبَادِ اللهِ الصَّالِحِيْنَ، أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا رَسُوْلُ اللهِ. اللّٰهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ وَعَلَى آلِ سَيِّدِنَا مُحَمَّدٍ، كَمَا صَلَّيْتَ عَلَى سَيِّدِنَا إِبْرَاهِيْمَ وَعَلَى آلِ سَيِّدِنَا إِبْرَاهِيْمَ، وَبَارِكْ عَلَى سَيِّدِنَا مُحَمَّدٍ وَعَلَى آلِ سَيِّدِنَا مُحَمَّدٍ، كَمَا بَارَكْتَ عَلَى سَيِّدِنَا إِبْرَاهِيْمَ وَعَلَى آلِ سَيِّدِنَا إِبْرَاهِيْمَ، فِي الْعَالَمِيْنَ إِنَّكَ حَمِيْدٌ مَجِيْدٌ.",
            latinText = "At-tahiyyaatul mubaarakaatush shalawaatuth thayyibaatu lillaah. As-salaamu 'alayka ayyuhan-nabiyyu wa rahmatullaahi wa barakaatuh. As-salaamu 'alaynaa wa 'alaa 'ibaadillaahish-shaalihiin. Asyhadu allaa ilaaha illallaah wa asyhadu anna Muhammadar rasuulullaah. Allaahumma shalli 'alaa sayyidinaa Muhammad wa 'alaa aali sayyidinaa Muhammad, kamaa shallayta 'alaa sayyidinaa Ibraahiim wa 'alaa aali sayyidinaa Ibraahiim, wa baarik 'alaa sayyidinaa Muhammad wa 'alaa aali sayyidinaa Muhammad, kamaa baarakta 'alaa sayyidinaa Ibraahiim wa 'alaa aali sayyidinaa Ibraahiim, fil 'aalamiina innaka hamiidum majiid.",
            translation = "...Ya Allah, limpahkanlah rahmat kepada Nabi Muhammad dan keluarga Nabi Muhammad, sebagaimana Engkau telah melimpahkan rahmat kepada Nabi Ibrahim dan keluarga Nabi Ibrahim. Dan berkahilah Nabi Muhammad dan keluarga Nabi Muhammad, sebagaimana Engkau memberkahi Nabi Ibrahim dan keluarga Nabi Ibrahim. Di seluruh alam semesta, sesungguhnya Engkau Maha Terpuji lagi Maha Mulia.",
            notes = "Tasyahud akhir dan shalawat adalah rukun sholat."
        ),
        PrayerStep(
            stepNumber = 12,
            title = "Salam",
            movementDescription = "Menolehkan wajah ke kanan hingga pipi terlihat dari belakang sambil mengucapkan salam pertama (Rukun). Kemudian menoleh ke kiri sambil mengucapkan salam kedua (Sunnah).",
            arabicText = "السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللهِ",
            latinText = "As-salaamu 'alaykum wa rahmatullaah",
            translation = "Semoga keselamatan dan rahmat Allah tercurah kepada kalian.",
            notes = "Dengan selesainya salam pertama, sholat telah usai secara sah."
        )
    )

    val sholatFardhuList = listOf(
        SholatGuideItem(
            id = "subuh",
            name = "Sholat Subuh",
            arabicName = "صَلَاةُ الصُّبْحِ",
            category = "Wajib",
            rakaatInfo = "2 Rakaat (Dengan Doa Qunut)",
            briefDescription = "Sholat wajib pertama di awal fajar shadiq hingga terbit matahari. Disunnahkan membaca Doa Qunut pada rakaat kedua setelah I'tidal.",
            niatArabic = "اُصَلِّيْ فَرْضَ الصُّبْحِ رَكْعَتَيْنِ مُسْتَقْبِلَ الْقِبْلَةِ اَدَاءً لِلّٰهِ تَعَالَى",
            niatLatin = "Ushallii fardhash-shubhi rak'atayni mustaqbilal qiblati adaa-an lillaahi Ta'aalaa.",
            niatTranslation = "Aku berniat sholat fardhu Subuh dua rakaat menghadap kiblat pada waktunya karena Allah Ta'ala.",
            specialDoaArabic = "اللّٰهُمَّ اهْدِنِيْ فِيْمَنْ هَدَيْتَ، وَعَافِنِيْ فِيْمَنْ عَافَيْتَ، وَتَوَلَّنِيْ فِيْمَنْ تَوَلَّيْتَ، وَبَارِكْ لِيْ فِيْمَا أَعْطَيْتَ، وَقِنِيْ شَرَّ مَا قَضَيْتَ، فَإِنَّكَ تَقْضِيْ وَلَا يُقْضَى عَلَيْكَ، وَإِنَّهُ لَا يَذِلُّ مَنْ وَالَيْتَ، وَلَا يَعِزُّ مَنْ عَادَيْتَ، تَبَارَكْتَ رَبَّنَا وَتَعَالَيْتَ، فَلَكَ الْحَمْدُ عَلَى مَا قَضَيْتَ، أَسْتَغْفِرُكَ وَأَتُوْبُ إِلَيْكَ، وَصَلَّى اللهُ عَلَى سَيِّدِنَا مُحَمَّدٍ النَّبِيِّ الْأُمِّيِّ وَعَلَى آلِهِ وَصَحْبِهِ وَسَلَّمَ.",
            specialDoaLatin = "Allaahummahdinii fiiman hadayt, wa 'aafinii fiiman 'aafayt, wa tawallanii fiiman tawallayt, wa baarik lii fiimaa a'thayt, wa qinii syarra maa qadhayt, fa innaka taqdhii wa laa yuqdhaa 'alayk, wa innahu laa yadzillu maw waalayt, wa laa ya'izzu man 'aadayt, tabaarakta rabbanaa wa ta'aalayt, fa lakal hamdu 'alaa maa qadhayt, astaghfiruka wa atuubu ilayk, wa shallallaahu 'alaa sayyidinaa Muhammadinin-nabiyyil-ummiyyi wa 'alaa aalihi wa shahbihi wa sallam.",
            specialDoaTranslation = "Ya Allah, berilah aku petunjuk sebagaimana orang yang telah Engkau beri petunjuk, berilah aku kesehatan sebagaimana orang yang telah Engkau beri kesehatan, peliharalah aku sebagaimana orang yang telah Engkau pelihara, berkahilah bagiku apa yang Engkau berikan, lindungilah aku dari keburukan yang Engkau tetapkan..."
        ),
        SholatGuideItem(
            id = "dzuhur",
            name = "Sholat Dzuhur",
            arabicName = "صَلَاةُ الظُّهْرِ",
            category = "Wajib",
            rakaatInfo = "4 Rakaat",
            briefDescription = "Sholat fardhu di waktu tengah hari ketika matahari telah tergelincir condong ke arah barat hingga bayangan sama panjang dengan bendanya.",
            niatArabic = "اُصَلِّيْ فَرْضَ الظُّهْرِ أَرْبَعَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ اَدَاءً لِلّٰهِ تَعَالَى",
            niatLatin = "Ushallii fardhadh-dhuhri arba'a raka'aatin mustaqbilal qiblati adaa-an lillaahi Ta'aalaa.",
            niatTranslation = "Aku berniat sholat fardhu Dzuhur empat rakaat menghadap kiblat pada waktunya karena Allah Ta'ala."
        ),
        SholatGuideItem(
            id = "ashar",
            name = "Sholat Ashar",
            arabicName = "صَلَاةُ الْعَصْرِ",
            category = "Wajib",
            rakaatInfo = "4 Rakaat",
            briefDescription = "Sholat fardhu di waktu sore hari sejak bayangan suatu benda melebihi panjang bendanya hingga menjelang matahari terbenam (Ash-Shalatul Wustha).",
            niatArabic = "اُصَلِّيْ فَرْضَ الْعَصْرِ أَرْبَعَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ اَدَاءً لِلّٰهِ تَعَالَى",
            niatLatin = "Ushallii fardhal-'ashri arba'a raka'aatin mustaqbilal qiblati adaa-an lillaahi Ta'aalaa.",
            niatTranslation = "Aku berniat sholat fardhu Ashar empat rakaat menghadap kiblat pada waktunya karena Allah Ta'ala."
        ),
        SholatGuideItem(
            id = "maghrib",
            name = "Sholat Maghrib",
            arabicName = "صَلَاةُ الْمَغْرِبِ",
            category = "Wajib",
            rakaatInfo = "3 Rakaat",
            briefDescription = "Sholat fardhu saat piringan matahari terbenam sempurna hingga mega merah (syafaq ahmar) menghilang di ufuk barat.",
            niatArabic = "اُصَلِّيْ فَرْضَ الْمَغْرِبِ ثَلَاثَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ اَدَاءً لِلّٰهِ تَعَالَى",
            niatLatin = "Ushallii fardhal-maghribi tsalaatsa raka'aatin mustaqbilal qiblati adaa-an lillaahi Ta'aalaa.",
            niatTranslation = "Aku berniat sholat fardhu Maghrib tiga rakaat menghadap kiblat pada waktunya karena Allah Ta'ala."
        ),
        SholatGuideItem(
            id = "isya",
            name = "Sholat Isya",
            arabicName = "صَلَاةُ الْعِشَاءِ",
            category = "Wajib",
            rakaatInfo = "4 Rakaat",
            briefDescription = "Sholat fardhu malam sejak hilangnya mega merah di ufuk barat hingga terbit fajar shadiq.",
            niatArabic = "اُصَلِّيْ فَرْضَ الْعِشَاءِ أَرْبَعَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ اَدَاءً لِلّٰهِ تَعَالَى",
            niatLatin = "Ushallii fardhal-'isyaa-i arba'a raka'aatin mustaqbilal qiblati adaa-an lillaahi Ta'aalaa.",
            niatTranslation = "Aku berniat sholat fardhu Isya empat rakaat menghadap kiblat pada waktunya karena Allah Ta'ala."
        )
    )

    val sholatSunnahList = listOf(
        SholatGuideItem(
            id = "tahajjud",
            name = "Sholat Tahajjud",
            arabicName = "صَلَاةُ التَّهَجُّدِ",
            category = "Sunnah",
            rakaatInfo = "Minimal 2 Rakaat (Tanpa Batas)",
            briefDescription = "Sholat malam yang didirikan setelah bangun tidur, paling utama pada sepertiga malam terakhir. Membuka pintu kemuliaan dan kedudukan terpuji (Maqamam Mahmuda).",
            niatArabic = "اُصَلِّيْ سُنَّةَ التَّهَجُّدِ رَكْعَتَيْنِ لِلّٰهِ تَعَالَى",
            niatLatin = "Ushallii sunnatat-tahajjudi rak'atayni lillaahi Ta'aalaa.",
            niatTranslation = "Aku berniat sholat sunnah Tahajjud dua rakaat karena Allah Ta'ala."
        ),
        SholatGuideItem(
            id = "dhuha",
            name = "Sholat Dhuha",
            arabicName = "صَلَاةُ الضُّحَى",
            category = "Sunnah",
            rakaatInfo = "2 hingga 8/12 Rakaat",
            briefDescription = "Dikerjakan setelah matahari naik setinggi tombak (sekitar 20 menit setelah terbit) hingga menjelang waktu Dzuhur. Menjadi sedekah bagi seluruh persendian tubuh dan pembuka pintu rezeki.",
            niatArabic = "اُصَلِّيْ سُنَّةَ الضُّحَى رَكْعَتَيْنِ لِلّٰهِ تَعَالَى",
            niatLatin = "Ushallii sunnatadh-dhuhaa rak'atayni lillaahi Ta'aalaa.",
            niatTranslation = "Aku berniat sholat sunnah Dhuha dua rakaat karena Allah Ta'ala.",
            specialDoaArabic = "اللّٰهُمَّ إِنَّ الضُّحَاءَ ضُحَاؤُكَ وَالْبَهَاءَ بَهَاؤُكَ وَالْجَمَالَ جَمَالُكَ وَالْقُوَّةَ قُوَّتُكَ وَالْقُدْرَةَ قُدْرَتُكَ وَالْعِصْمَةَ عِصْمَتُكَ. اللّٰهُمَّ إِنْ كَانَ رِزْقِيْ فِي السَّمَاءِ فَأَنْزِلْهُ وَإِنْ كَانَ فِي الْأَرْضِ فَأَخْرِجْهُ وَإِنْ كَانَ مُعَسَّرًا فَيَسِّرْهُ وَإِنْ كَانَ حَرَامًا فَطَهِّرْهُ وَإِنْ كَانَ بَعِيْدًا فَقَرِّبْهُ بِحَقِّ ضُحَائِكَ وَبَهَائِكَ وَجَمَالِكَ وَقُوَّتِكَ وَقُدْرَتِكَ آتِنِيْ مَا آتَيْتَ عِبَادَكَ الصَّالِحِيْنَ.",
            specialDoaLatin = "Allaahumma innadh-dhuhaa-a dhuhaa-uka, wal bahaa-a bahaa-uka, wal jamaala jamaaluka, wal quwwata quwwatuka, wal qudrata qudratuka, wal 'ishmata 'ishmatuka. Allaahumma in kaana rizqii fis-samaa-i fa anzilhu, wa in kaana fil ardhi fa akhrijhu, wa in kaana mu'assaran fa yassirhu, wa in kaana haraaman fa thahhirhu, wa in kaana ba'iidan fa qarribhu, bihaqqi dhuhaa-ika wa bahaa-ika wa jamaalika wa quwwatika wa qudratika, aatinii maa aatayta 'ibaadakash-shaalihiin.",
            specialDoaTranslation = "Ya Allah, sesungguhnya waktu Dhuha adalah waktu Dhuha-Mu, keagungan adalah keagungan-Mu, keindahan adalah keindahan-Mu, kekuatan adalah kekuatan-Mu, kekuasaan adalah kekuasaan-Mu, dan perlindungan adalah perlindungan-Mu. Ya Allah, jika rezekiku masih di atas langit maka turunkanlah..."
        ),
        SholatGuideItem(
            id = "witir",
            name = "Sholat Witir",
            arabicName = "صَلَاةُ الْوِتْرِ",
            category = "Sunnah",
            rakaatInfo = "Ganjil: 1, 3, atau hingga 11 Rakaat",
            briefDescription = "Sholat penutup ibadah malam hari, dilakukan setelah sholat Isya atau sesudah sholat Tahajjud/Tarawih.",
            niatArabic = "اُصَلِّيْ سُنَّةَ الْوِتْرِ ثَلَاثَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ لِلّٰهِ تَعَالَى",
            niatLatin = "Ushallii sunnatal-witri tsalaatsa raka'aatin mustaqbilal qiblati lillaahi Ta'aalaa.",
            niatTranslation = "Aku berniat sholat sunnah Witir tiga rakaat menghadap kiblat karena Allah Ta'ala."
        ),
        SholatGuideItem(
            id = "istikharah",
            name = "Sholat Istikharah",
            arabicName = "صَلَاةُ الِاسْتِخَارَةِ",
            category = "Sunnah",
            rakaatInfo = "2 Rakaat",
            briefDescription = "Dikerjakan untuk memohon petunjuk pilihan terbaik dari Allah ketika berada di antara dua pilihan yang mubah atau menentukan keputusan penting.",
            niatArabic = "اُصَلِّيْ سُنَّةَ الِاسْتِخَارَةِ رَكْعَتَيْنِ لِلّٰهِ تَعَالَى",
            niatLatin = "Ushallii sunnatal-istikhaarati rak'atayni lillaahi Ta'aalaa.",
            niatTranslation = "Aku berniat sholat sunnah Istikharah dua rakaat karena Allah Ta'ala."
        ),
        SholatGuideItem(
            id = "taubat",
            name = "Sholat Taubat",
            arabicName = "صَلَاةُ التَّوْبَةِ",
            category = "Sunnah",
            rakaatInfo = "2 Rakaat",
            briefDescription = "Dikerjakan seseorang setelah menyadari dosa dan kesalahan, bertekad memohon ampunan Allah dan tidak mengulangi perbuatan maksiat tersebut.",
            niatArabic = "اُصَلِّيْ سُنَّةَ التَّوْبَةِ رَكْعَتَيْنِ لِلّٰهِ تَعَالَى",
            niatLatin = "Ushallii sunnatat-tawbati rak'atayni lillaahi Ta'aalaa.",
            niatTranslation = "Aku berniat sholat sunnah Taubat dua rakaat karena Allah Ta'ala."
        ),
        SholatGuideItem(
            id = "hajat",
            name = "Sholat Hajat",
            arabicName = "صَلَاةُ الْحَاجَةِ",
            category = "Sunnah",
            rakaatInfo = "2 Rakaat (hingga 12 Rakaat)",
            briefDescription = "Dikerjakan saat memiliki hajat atau keinginan kebaikan yang mendesak, baik urusan duniawi maupun akhirat.",
            niatArabic = "اُصَلِّيْ سُنَّةَ الْحَاجَةِ رَكْعَتَيْنِ لِلّٰهِ تَعَالَى",
            niatLatin = "Ushallii sunnatal-haajati rak'atayni lillaahi Ta'aalaa.",
            niatTranslation = "Aku berniat sholat sunnah Hajat dua rakaat karena Allah Ta'ala."
        )
    )

    val wudhuSteps = listOf(
        PrayerStep(
            stepNumber = 1,
            title = "Niat Wudhu & Membasuh Telapak Tangan",
            movementDescription = "Membaca Basmalah, berniat di dalam hati untuk menghilangkan hadats kecil, lalu membasuh kedua telapak tangan hingga pergelangan sebanyak 3 kali serta menyela-nyela jari jemari.",
            arabicText = "نَوَيْتُ الْوُضُوْءَ لِرَفْعِ الْحَدَثِ الْأَصْغَرِ فَرْضًا لِلّٰهِ تَعَالَى",
            latinText = "Nawaytul wudhuu-a liraf'il hadatsil ashghari fardhal lillaahi Ta'aalaa.",
            translation = "Aku berniat berwudhu untuk menghilangkan hadats kecil, fardhu karena Allah Ta'ala."
        ),
        PrayerStep(
            stepNumber = 2,
            title = "Berkumur-kumur (Madhmadhah)",
            movementDescription = "Memasukkan air ke dalam mulut lalu mengeluarkannya sebanyak 3 kali (Sunnah muakkad).",
            arabicText = "اللّٰهُمَّ أَعِنِّيْ عَلَى تِلَاوَةِ الْقُرْآنِ وَذِكْرِكَ وَشُكْرِكَ",
            latinText = "Allaahumma a'innii 'alaa tilaawatil Qur-aani wa dzikrika wa syukrik.",
            translation = "Ya Allah, tolonglah aku untuk membaca Al-Qur'an dan mengingat-Mu serta bersyukur kepada-Mu."
        ),
        PrayerStep(
            stepNumber = 3,
            title = "Istinsyaq (Menghirup Air ke Hidung)",
            movementDescription = "Menghirup sedikit air ke dalam lubang hidung lalu menyemprotkannya keluar (Istintsar) sebanyak 3 kali.",
            arabicText = "اللّٰهُمَّ أَرِحْنِيْ رَائِحَةَ الْجَنَّةِ",
            latinText = "Allaahumma arihnii raa-ihatal jannah.",
            translation = "Ya Allah, ciumkanlah kepadaku wewangian surga."
        ),
        PrayerStep(
            stepNumber = 4,
            title = "Membasuh Muka (Rukun)",
            movementDescription = "Membasuh seluruh wajah mulai dari tumbuhnya rambut kepala sampai dagu, dan dari telinga kanan sampai telinga kiri secara merata sebanyak 3 kali.",
            arabicText = "اللّٰهُمَّ بَيِّضْ وَجْهِيْ يَوْمَ تَبْيَضُّ وُجُوْهٌ وَتَسْوَدُّ وُجُوْهٌ",
            latinText = "Allaahumma bayyidh wajhii yawma tabyadh-dhu wujuuhuw wa taswaddu wujuuh.",
            translation = "Ya Allah, putihkanlah (bersihkanlah) wajahku pada hari di mana wajah-wajah memutih dan menghitam."
        ),
        PrayerStep(
            stepNumber = 5,
            title = "Membasuh Kedua Tangan Sampai Siku (Rukun)",
            movementDescription = "Membasuh tangan kanan mulai dari ujung jari hingga melebihi siku 3 kali, lalu tangan kiri dengan cara yang sama.",
            arabicText = "اللّٰهُمَّ أَعْطِنِيْ كِتَابِيْ بِيَمِيْنِيْ وَحَاسِبْنِيْ حِسَابًا يَسِيْرًا",
            latinText = "Allaahumma a'thinii kitaabii bi yamiinii wa haasibnii hisaabay yasiiraa.",
            translation = "Ya Allah, berikanlah buku catatanku dari tangan kananku dan hisablah aku dengan hisab yang mudah."
        ),
        PrayerStep(
            stepNumber = 6,
            title = "Mengusap Sebagian Kepala / Rambut (Rukun)",
            movementDescription = "Mengusap sebagian kepala atau rambut dengan air yang baru sebanyak 3 kali.",
            arabicText = "اللّٰهُمَّ حَرِّمْ شَعْرِيْ وَبَشَرِيْ عَلَى النَّارِ",
            latinText = "Allaahumma harrim sya'rii wa basyarii 'alan-naar.",
            translation = "Ya Allah, haramkanlah rambut dan kulitku dari api neraka."
        ),
        PrayerStep(
            stepNumber = 7,
            title = "Membasuh Kedua Daun Telinga",
            movementDescription = "Memasukkan jari telunjuk ke liang telinga dan ibu jari mengusap daun telinga bagian luar secara bersamaan 3 kali (Sunnah).",
            arabicText = "اللّٰهُمَّ اجْعَلْنِيْ مِنَ الَّذِيْنَ يَسْتَمِعُوْنَ الْقَوْلَ فَيَتَّبِعُوْنَ أَحْسَنَهُ",
            latinText = "Allaahummaj'alnii minal ladziina yastami'uunal qawla fayattabi'uuna ahsanah.",
            translation = "Ya Allah, jadikanlah aku termasuk orang yang mendengarkan perkataan lalu mengikuti yang terbaik darinya."
        ),
        PrayerStep(
            stepNumber = 8,
            title = "Membasuh Kedua Kaki Sampai Mata Kaki (Rukun)",
            movementDescription = "Membasuh kaki kanan mulai dari jari kaki sampai mata kaki (melebihkannya disunnahkan) 3 kali dengan menyela jari kaki, lalu kaki kiri.",
            arabicText = "اللّٰهُمَّ ثَبِّتْ قَدَمَيَّ عَلَى الصِّرَاطِ يَوْمَ تَزِلُّ فِيْهِ الْأَقْدَامُ",
            latinText = "Allaahumma tsabbit qadamayya 'alash-shiraathi yawma tazillu fiihil aqdaam.",
            translation = "Ya Allah, kokohkanlah kedua telapak kakiku di atas jembatan (shirath) pada hari tergelincirnya kaki-kaki."
        ),
        PrayerStep(
            stepNumber = 9,
            title = "Tertib & Doa Sesudah Wudhu",
            movementDescription = "Melakukan rukun-rukun wudhu secara berurutan (Tertib). Selesai wudhu, menghadap kiblat dan mengangkat kedua tangan seraya membaca doa:",
            arabicText = "أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيْكَ لَهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُوْلُهُ. اللّٰهُمَّ اجْعَلْنِيْ مِنَ التَّوَّابِيْنَ وَاجْعَلْنِيْ مِنَ الْمُتَطَهِّرِيْنَ وَاجْعَلْنِيْ مِنْ عِبَادِكَ الصَّالِحِيْنَ.",
            latinText = "Asyhadu allaa ilaaha illallaahu wahdahu laa syariika lah, wa asyhadu anna Muhammadan 'abduhu wa rasuuluh. Allaahummaj'alnii minat-tawwaabiina waj'alnii minal mutatathahhiriina waj'alnii min 'ibaadikash-shaalihiin.",
            translation = "Aku bersaksi tiada tuhan selain Allah Yang Maha Esa, tiada sekutu bagi-Nya. Dan aku bersaksi Muhammad adalah hamba dan utusan-Nya. Ya Allah, jadikanlah aku termasuk orang-orang yang bertaubat, jadikanlah aku termasuk orang-orang yang menyucikan diri, dan jadikanlah aku termasuk golongan hamba-hamba-Mu yang shalih."
        )
    )

    val dzikirList = listOf(
        PrayerStep(
            stepNumber = 1,
            title = "Istighfar (3x)",
            movementDescription = "Memohon ampunan Allah segera setelah salam.",
            arabicText = "أَسْتَغْفِرُ اللهَ الْعَظِيْمَ الَّذِيْ لَا إِلٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّوْمُ وَأَتُوْبُ إِلَيْهِ",
            latinText = "Astaghfirullaahal 'azhiimal-ladzii laa ilaaha illaa huwal hayyul qayyuumu wa atuubu ilayh (3x)",
            translation = "Aku memohon ampun kepada Allah Yang Maha Agung, yang tiada Tuhan selain Dia Yang Maha Hidup lagi Maha Berdiri Sendiri, dan aku bertaubat kepada-Nya."
        ),
        PrayerStep(
            stepNumber = 2,
            title = "Doa Keselamatan (Allaahumma Antas Salaam)",
            movementDescription = "Dzikir memuji sumber kedamaian sejati.",
            arabicText = "اللّٰهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            latinText = "Allaahumma antas-salaamu wa minkas-salaamu tabaarakta yaa dzal jalaali wal ikraam.",
            translation = "Ya Allah, Engkaulah Maha Damai, dan dari-Mulah keselamatan, Maha Berkah Engkau wahai Tuhan Pemilik Keagungan dan Kemuliaan."
        ),
        PrayerStep(
            stepNumber = 3,
            title = "Ayat Kursi (QS. Al-Baqarah: 255)",
            movementDescription = "Barangsiapa membacanya setiap selesai sholat fardhu, tidak ada yang menghalanginya masuk surga melainkan kematian (HR. An-Nasa'i).",
            arabicText = "اللّٰهُ لَا إِلٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّوْمُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْأَرْضِ ۗ مَنْ ذَا الَّذِيْ يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيْهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيْطُوْنَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُوْدُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيْمُ",
            latinText = "Allaahu laa ilaaha illaa huwal-hayyul-qayyuum, laa ta'khudzuhu sinatuw wa laa nawm, lahu maa fis-samaawaati wa maa fil ardh, man dzalladzii yasyfa'u 'indahuu illaa bi-idznih, ya'lamu maa bayna aydiihim wa maa khalfahum, wa laa yuhiithuuna bi syay-im min 'ilmihii illaa bimaa syaa-a, wasi'a kursiyyuhus samaawaati wal ardh, wa laa ya-uuduhuu hifzhuhumaa, wa huwal 'aliyyul 'azhiim.",
            translation = "Allah, tidak ada tuhan selain Dia. Yang Maha Hidup, yang terus-menerus mengurus makhluk-Nya, tidak mengantuk dan tidak tidur. Milik-Nya apa yang ada di langit dan apa yang ada di bumi..."
        ),
        PrayerStep(
            stepNumber = 4,
            title = "Tasbih, Tahmid, Takbir (Masing-masing 33x)",
            movementDescription = "Membaca Tasbih 33x, Tahmid 33x, Takbir 33x, lalu disempurnakan menjadi 100 dengan kalimat Tauhid.",
            arabicText = "سُبْحَانَ اللهِ (٣٣×) • اَلْحَمْدُ لِلّٰهِ (٣٣×) • اَللهُ أَكْبَرُ (٣٣×)",
            latinText = "Subhaanallaah (33x), Alhamdulillaah (33x), Allaahu Akbar (33x)",
            translation = "Maha Suci Allah (33x), Segala Puji bagi Allah (33x), Allah Maha Besar (33x)."
        ),
        PrayerStep(
            stepNumber = 5,
            title = "Penyempurna Seratus (Tahlil)",
            movementDescription = "Menutup hitungan tasbih ke-100.",
            arabicText = "لَا إِلٰهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيْكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِيْ وَيُمِيْتُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيْرٌ",
            latinText = "Laa ilaaha illallaahu wahdahu laa syariika lah, lahul mulku wa lahul hamdu yuhyii wa yumiitu wa huwa 'alaa kulli syay-in qadiir.",
            translation = "Tiada Tuhan selain Allah Yang Maha Esa, tiada sekutu bagi-Nya. Milik-Nyalah kerajaan dan kepunyaan-Nyalah segala puji. Dia yang menghidupkan dan mematikan, dan Dia Maha Kuasa atas segala sesuatu."
        )
    )
}
