package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerStep
import com.example.data.model.SholatGuideItem
import com.example.data.model.SholatGuideRepository
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun SholatGuideScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedItem by viewModel.selectedGuideItem.collectAsState()
    val currentCategory by viewModel.guideCategory.collectAsState()
    val tasbihCount by viewModel.tasbihCount.collectAsState()
    val tasbihTarget by viewModel.tasbihTarget.collectAsState()

    if (selectedItem != null) {
        // Detail View for selected Sholat / Wudhu / Dzikir
        SholatDetailView(
            item = selectedItem!!,
            onBack = { viewModel.selectGuideItem(null) }
        )
    } else {
        // Main Catalog View
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("sholat_guide_screen"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                        .background(
                            Brush.verticalGradient(listOf(Emerald900, Emerald700))
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Gold500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = Gold500,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Panduan Sholat & Fiqih",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tata cara ibadah sesuai tuntunan Sunnah Nabi SAW",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Category Filter Chips
            item {
                Spacer(modifier = Modifier.height(16.dp))
                val categories = listOf("Semua", "Wajib", "Sunnah", "Gerakan Sholat", "Wudhu", "Dzikir & Tasbih")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = cat == currentCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setGuideCategory(cat) },
                            label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald700,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Interactive Digital Tasbih (when category is "Semua" or "Dzikir & Tasbih")
            if (currentCategory == "Semua" || currentCategory == "Dzikir & Tasbih") {
                item {
                    DigitalTasbihCard(
                        count = tasbihCount,
                        target = tasbihTarget,
                        onIncrement = { viewModel.incrementTasbih() },
                        onReset = { viewModel.resetTasbih() },
                        onSetTarget = { viewModel.setTasbihTarget(it) }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Gerakan Sholat 12 Langkah Banner Card
            if (currentCategory == "Semua" || currentCategory == "Gerakan Sholat") {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Emerald500, Emerald700))),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clickable {
                                viewModel.selectGuideItem(
                                    SholatGuideItem(
                                        id = "gerakan_lengkap",
                                        name = "Tata Cara Gerakan Sholat 12 Langkah",
                                        arabicName = "صِفَةُ صَلَاةِ النَّبِيِّ",
                                        category = "Gerakan Sholat",
                                        rakaatInfo = "Lengkap dari Takbir hingga Salam",
                                        briefDescription = "Panduan bergambar & bacaan tuntas dari Takbiratul Ihram, Ruku', I'tidal, Sujud, Duduk Iftirasy, Tasyahud hingga Salam.",
                                        niatArabic = "",
                                        niatLatin = "",
                                        niatTranslation = "",
                                        steps = SholatGuideRepository.commonSteps
                                    )
                                )
                            }
                            .testTag("item_gerakan_lengkap")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Emerald700),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessibilityNew,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "12 Langkah Gerakan Sholat Lengkap",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Bacaan Arab, Latin, arti, serta thuma'ninah setiap rukun",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Sholat Fardhu Section
            if (currentCategory == "Semua" || currentCategory == "Wajib") {
                item {
                    Text(
                        text = "Sholat Wajib (Fardhu 5 Waktu)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }

                items(SholatGuideRepository.sholatFardhuList) { item ->
                    GuideItemCard(
                        item = item,
                        onClick = { viewModel.selectGuideItem(item) }
                    )
                }
            }

            // Sholat Sunnah Section
            if (currentCategory == "Semua" || currentCategory == "Sunnah") {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sholat Sunnah Pilihan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }

                items(SholatGuideRepository.sholatSunnahList) { item ->
                    GuideItemCard(
                        item = item,
                        onClick = { viewModel.selectGuideItem(item) }
                    )
                }
            }

            // Wudhu & Thaharah Section
            if (currentCategory == "Semua" || currentCategory == "Wudhu") {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Bersuci (Wudhu & Thaharah)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )

                    GuideItemCard(
                        item = SholatGuideItem(
                            id = "wudhu",
                            name = "Tata Cara Wudhu Sesuai Sunnah",
                            arabicName = "طَهَارَةُ الْوُضُوْءِ",
                            category = "Thaharah",
                            rakaatInfo = "9 Langkah Lengkap",
                            briefDescription = "Tata cara berwudhu dari basuh tangan, kumur, istinsyaq, basuh muka, tangan, kepala, telinga, kaki, hingga doa sesudah wudhu.",
                            niatArabic = "نَوَيْتُ الْوُضُوْءَ لِرَفْعِ الْحَدَثِ الْأَصْغَرِ فَرْضًا لِلّٰهِ تَعَالَى",
                            niatLatin = "Nawaytul wudhuu-a liraf'il hadatsil ashghari fardhal lillaahi Ta'aalaa.",
                            niatTranslation = "Aku berniat wudhu untuk mengangkat hadats kecil fardhu karena Allah Ta'ala.",
                            steps = SholatGuideRepository.wudhuSteps
                        ),
                        onClick = {
                            viewModel.selectGuideItem(
                                SholatGuideItem(
                                    id = "wudhu",
                                    name = "Tata Cara Wudhu Sesuai Sunnah",
                                    arabicName = "طَهَارَةُ الْوُضُوْءِ",
                                    category = "Thaharah",
                                    rakaatInfo = "9 Langkah Lengkap",
                                    briefDescription = "Tata cara berwudhu dari basuh tangan, kumur, istinsyaq, basuh muka, tangan, kepala, telinga, kaki, hingga doa sesudah wudhu.",
                                    niatArabic = "نَوَيْتُ الْوُضُوْءَ لِرَفْعِ الْحَدَثِ الْأَصْغَرِ فَرْضًا لِلّٰهِ تَعَالَى",
                                    niatLatin = "Nawaytul wudhuu-a liraf'il hadatsil ashghari fardhal lillaahi Ta'aalaa.",
                                    niatTranslation = "Aku berniat wudhu untuk mengangkat hadats kecil fardhu karena Allah Ta'ala.",
                                    steps = SholatGuideRepository.wudhuSteps
                                )
                            )
                        }
                    )
                }
            }

            // Dzikir Sesudah Sholat Section
            if (currentCategory == "Semua" || currentCategory == "Dzikir & Tasbih") {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Dzikir & Wirid Ba'da Sholat",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )

                    GuideItemCard(
                        item = SholatGuideItem(
                            id = "dzikir_bada_sholat",
                            name = "Rangkaian Dzikir Sesudah Sholat",
                            arabicName = "أَذْكَارُ مَا بَعْدَ الصَّلَاةِ",
                            category = "Dzikir",
                            rakaatInfo = "Istighfar, Ayat Kursi, Tasbih, Tahlil",
                            briefDescription = "Bacaan wirid shahih yang diajarkan Rasulullah SAW setelah salam sholat fardhu.",
                            niatArabic = "",
                            niatLatin = "",
                            niatTranslation = "",
                            steps = SholatGuideRepository.dzikirList
                        ),
                        onClick = {
                            viewModel.selectGuideItem(
                                SholatGuideItem(
                                    id = "dzikir_bada_sholat",
                                    name = "Rangkaian Dzikir Sesudah Sholat",
                                    arabicName = "أَذْكَارُ مَا بَعْدَ الصَّلَاةِ",
                                    category = "Dzikir",
                                    rakaatInfo = "Istighfar, Ayat Kursi, Tasbih, Tahlil",
                                    briefDescription = "Bacaan wirid shahih yang diajarkan Rasulullah SAW setelah salam sholat fardhu.",
                                    niatArabic = "",
                                    niatLatin = "",
                                    niatTranslation = "",
                                    steps = SholatGuideRepository.dzikirList
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GuideItemCard(
    item: SholatGuideItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clickable { onClick() }
            .testTag("guide_card_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (item.category == "Wajib") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = item.rakaatInfo,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (item.category == "Wajib") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.briefDescription,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DigitalTasbihCard(
    count: Int,
    target: Int,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    onSetTarget: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .testTag("digital_tasbih_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Adjust,
                        contentDescription = null,
                        tint = Emerald700,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tasbih Digital Interaktif",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Row {
                    listOf(33, 100).forEach { tgt ->
                        val isSel = target == tgt
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Emerald700 else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .clickable { onSetTarget(tgt) }
                        ) {
                            Text(
                                text = "$tgt",
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big Circular Tasbih Counter Button
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(listOf(Emerald600, Emerald800))
                    )
                    .clickable { onIncrement() }
                    .testTag("tasbih_counter_button"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$count",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Target: $target",
                        fontSize = 11.sp,
                        color = Gold500,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ketuk lingkaran untuk menghitung dzikir",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                TextButton(
                    onClick = onReset,
                    modifier = Modifier.testTag("tasbih_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SholatDetailView(
    item: SholatGuideItem,
    onBack: () -> Unit
) {
    val steps = if (item.steps.isNotEmpty()) item.steps else SholatGuideRepository.commonSteps

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Header Info & Niat
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.category,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = item.rakaatInfo,
                                color = if (isSystemInDarkTheme()) Gold500 else Gold700,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.briefDescription,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        if (item.niatArabic.isNotBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Lafadz Niat:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.niatArabic,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.niatLatin,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"${item.niatTranslation}\"",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Langkah-Langkah Pelaksanaan:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Steps
            items(steps) { step ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Emerald700),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${step.stepNumber}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = step.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = step.movementDescription,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (step.arabicText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = step.arabicText,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Right,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = step.latinText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"${step.translation}\"",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (step.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 ${step.notes}",
                                fontSize = 11.sp,
                                color = if (isSystemInDarkTheme()) Gold500 else Gold700,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Special Doa (if available, e.g. Doa Qunut or Doa Dhuha)
            if (item.specialDoaArabic != null) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Gold500, Gold700))),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Doa Khusus (${item.name}):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = item.specialDoaArabic,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.specialDoaLatin ?: "",
                                fontSize = 12.sp,
                                color = if (isSystemInDarkTheme()) Gold500 else Gold700,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "\"${item.specialDoaTranslation ?: ""}\"",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}
