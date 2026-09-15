package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.prayer.CityLocation
import com.example.ui.screens.AiConsultantScreen
import com.example.ui.screens.AuthDialog
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.PrayerTimesScreen
import com.example.ui.screens.SholatGuideScreen
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold500
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.google.android.gms.location.LocationServices
import java.util.Locale
import java.util.TimeZone

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val context = LocalContext.current

    var showExitDialog by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }

    val selectedGuideItem by viewModel.selectedGuideItem.collectAsState()
    val selectedPostForComments by viewModel.selectedPostForComments.collectAsState()

    // Handle back button: dismiss active modal first, or prompt exit confirmation
    BackHandler(enabled = true) {
        when {
            showExitDialog -> showExitDialog = false
            showAuthDialog -> showAuthDialog = false
            selectedPostForComments != null -> viewModel.closeComments()
            selectedGuideItem != null -> viewModel.selectGuideItem(null)
            else -> showExitDialog = true
        }
    }

    // Runtime location permission request
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            viewModel.detectLocation()
        }
    }

    // Auto-request location permission on first launch and auto-detect
    LaunchedEffect(Unit) {
        val fineCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fineCheck != PackageManager.PERMISSION_GRANTED && coarseCheck != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.detectLocation()
        }
    }

    val navItems = listOf(
        NavigationItemData(
            label = "Jadwal Sholat",
            selectedIcon = Icons.Filled.AccessTime,
            unselectedIcon = Icons.Outlined.AccessTime,
            testTag = "nav_tab_jadwal"
        ),
        NavigationItemData(
            label = "Panduan",
            selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
            unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook,
            testTag = "nav_tab_panduan"
        ),
        NavigationItemData(
            label = "Tanya Ustadz",
            selectedIcon = Icons.Filled.AutoAwesome,
            unselectedIcon = Icons.Outlined.AutoAwesome,
            testTag = "nav_tab_ai"
        ),
        NavigationItemData(
            label = "Komunitas",
            selectedIcon = Icons.Filled.Groups,
            unselectedIcon = Icons.Outlined.Groups,
            testTag = "nav_tab_komunitas"
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(index) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> PrayerTimesScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding),
                onOpenAuth = { showAuthDialog = true }
            )
            1 -> SholatGuideScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> AiConsultantScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            3 -> CommunityScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding),
                onOpenAuth = { showAuthDialog = true }
            )
        }
    }

    // Exit Confirmation Dialog ("Iya" dan "Tidak")
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Keluar dari Aplikasi?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin keluar dari aplikasi Panduan Ibadah Islam?",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        (context as? android.app.Activity)?.finish()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("dialog_exit_confirm_yes")
                ) {
                    Text("Iya", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showExitDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("dialog_exit_dismiss_no")
                ) {
                    Text("Tidak", fontWeight = FontWeight.SemiBold)
                }
            },
            modifier = Modifier.testTag("exit_confirmation_dialog")
        )
    }

    // User Auth & Profile Dialog
    if (showAuthDialog) {
        AuthDialog(
            viewModel = viewModel,
            onDismissRequest = { showAuthDialog = false }
        )
    }
}

data class NavigationItemData(
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

