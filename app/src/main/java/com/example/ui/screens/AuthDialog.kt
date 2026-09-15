package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserEntity
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

val EASY_SECURITY_QUESTIONS = listOf(
    "Apa makanan favorit Anda?",
    "Apa warna favorit Anda?",
    "Apa nama hewan peliharaan pertama Anda?",
    "Di kota mana Anda lahir?",
    "Apa minuman kesukaan Anda?"
)

enum class AuthMode {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    FORGOT_EMAIL,
    PROFILE
}

/**
 * Layar Wajib Daftar & Masuk Akun untuk mengakses seluruh fitur aplikasi.
 * Pengguna tidak dapat masuk ke fitur sebelum mendaftar atau masuk akun.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MandatoryAuthScreen(
    viewModel: MainViewModel,
    onExitRequested: () -> Unit
) {
    var currentMode by remember { mutableStateOf(AuthMode.REGISTER) } // Default wajib daftar akun baru!

    // Back handling: jika dalam recovery, kembali ke login. Jika di form utama, munculkan konfirmasi keluar.
    BackHandler(enabled = true) {
        if (currentMode == AuthMode.FORGOT_PASSWORD || currentMode == AuthMode.FORGOT_EMAIL) {
            currentMode = AuthMode.LOGIN
        } else {
            onExitRequested()
        }
    }

    // Common fields
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Register fields
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regBirthDate by remember { mutableStateOf("") }
    var regSecurityQuestion by remember { mutableStateOf(EASY_SECURITY_QUESTIONS[0]) }
    var regSecurityAnswer by remember { mutableStateOf("") }
    var isQuestionDropdownExpanded by remember { mutableStateOf(false) }

    // Forgot Password fields
    var forgotIdentifier by remember { mutableStateOf("") }
    var forgotBirthDate by remember { mutableStateOf("") }
    var forgotSecurityAnswer by remember { mutableStateOf("") }
    var forgotNewPassword by remember { mutableStateOf("") }
    var forgotConfirmPassword by remember { mutableStateOf("") }

    // Forgot Email fields
    var recoverPhone by remember { mutableStateOf("") }
    var recoverBirthDate by remember { mutableStateOf("") }
    var recoverSecurityAnswer by remember { mutableStateOf("") }
    var recoveredEmailResult by remember { mutableStateOf<String?>(null) }

    // Feedback
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Elegan Islami
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Emerald900, Emerald800, Emerald700)
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Gold500),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = Emerald900,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Panduan Ibadah Islam",
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Jadwal Sholat, Arah Kiblat, Panduan Sholat & Komunitas",
                        color = Emerald100,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pengumuman Wajib Daftar
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Gold500),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Emerald900,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Wajib Daftar untuk Akses Fitur",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Silakan daftarkan akun baru atau masuk untuk menikmati fitur Jadwal Sholat, Kiblat, Panduan, Tanya Ustadz AI & Komunitas.",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // Form Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tab Navigasi: Daftar Akun (Wajib) vs Sudah Punya Akun
                if (currentMode == AuthMode.REGISTER || currentMode == AuthMode.LOGIN) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            val isRegister = currentMode == AuthMode.REGISTER
                            val isLogin = currentMode == AuthMode.LOGIN

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isRegister) MaterialTheme.colorScheme.primary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        clearMessages()
                                        currentMode = AuthMode.REGISTER
                                    }
                                    .testTag("tab_mandatory_register")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = if (isRegister) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Daftar Baru (Wajib)",
                                        color = if (isRegister) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isRegister) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isLogin) MaterialTheme.colorScheme.primary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        clearMessages()
                                        currentMode = AuthMode.LOGIN
                                    }
                                    .testTag("tab_mandatory_login")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Login,
                                        contentDescription = null,
                                        tint = if (isLogin) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Sudah Punya Akun",
                                        color = if (isLogin) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isLogin) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Alert Messages
                AnimatedVisibility(visible = errorMessage != null) {
                    errorMessage?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = successMessage != null) {
                    successMessage?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Form Container
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (currentMode) {
                            AuthMode.REGISTER -> {
                                RegisterContent(
                                    name = regName,
                                    onNameChange = { regName = it },
                                    phone = regPhone,
                                    onPhoneChange = { regPhone = it },
                                    email = regEmail,
                                    onEmailChange = { regEmail = it },
                                    password = regPassword,
                                    onPasswordChange = { regPassword = it },
                                    confirmPassword = regConfirmPassword,
                                    onConfirmPasswordChange = { regConfirmPassword = it },
                                    birthDate = regBirthDate,
                                    onBirthDateChange = { regBirthDate = it },
                                    securityQuestion = regSecurityQuestion,
                                    onSecurityQuestionChange = { regSecurityQuestion = it },
                                    securityAnswer = regSecurityAnswer,
                                    onSecurityAnswerChange = { regSecurityAnswer = it },
                                    isQuestionDropdownExpanded = isQuestionDropdownExpanded,
                                    onToggleDropdown = { isQuestionDropdownExpanded = it },
                                    showPassword = showPassword,
                                    onToggleShowPassword = { showPassword = !showPassword },
                                    isLoading = isLoading,
                                    onRegisterSubmit = {
                                        clearMessages()
                                        if (regPassword != regConfirmPassword) {
                                            errorMessage = "Konfirmasi kata sandi tidak cocok"
                                            return@RegisterContent
                                        }
                                        isLoading = true
                                        viewModel.registerUser(
                                            name = regName,
                                            phone = regPhone,
                                            email = regEmail,
                                            password = regPassword,
                                            birthDate = regBirthDate,
                                            securityQuestion = regSecurityQuestion,
                                            securityAnswer = regSecurityAnswer
                                        ) { success, msg ->
                                            isLoading = false
                                            if (success) {
                                                successMessage = msg
                                            } else {
                                                errorMessage = msg
                                            }
                                        }
                                    },
                                    onGoToLogin = {
                                        clearMessages()
                                        currentMode = AuthMode.LOGIN
                                    }
                                )
                            }

                            AuthMode.LOGIN -> {
                                LoginContent(
                                    identifier = identifier,
                                    onIdentifierChange = { identifier = it },
                                    password = password,
                                    onPasswordChange = { password = it },
                                    showPassword = showPassword,
                                    onToggleShowPassword = { showPassword = !showPassword },
                                    isLoading = isLoading,
                                    onLoginSubmit = {
                                        clearMessages()
                                        isLoading = true
                                        viewModel.loginUser(identifier, password) { success, msg ->
                                            isLoading = false
                                            if (success) {
                                                successMessage = msg
                                            } else {
                                                errorMessage = msg
                                            }
                                        }
                                    },
                                    onGoToRegister = {
                                        clearMessages()
                                        currentMode = AuthMode.REGISTER
                                    },
                                    onGoToForgotPassword = {
                                        clearMessages()
                                        forgotIdentifier = identifier
                                        currentMode = AuthMode.FORGOT_PASSWORD
                                    },
                                    onGoToForgotEmail = {
                                        clearMessages()
                                        currentMode = AuthMode.FORGOT_EMAIL
                                    }
                                )
                            }

                            AuthMode.FORGOT_PASSWORD -> {
                                ForgotPasswordContent(
                                    identifier = forgotIdentifier,
                                    onIdentifierChange = { forgotIdentifier = it },
                                    birthDate = forgotBirthDate,
                                    onBirthDateChange = { forgotBirthDate = it },
                                    securityAnswer = forgotSecurityAnswer,
                                    onSecurityAnswerChange = { forgotSecurityAnswer = it },
                                    newPassword = forgotNewPassword,
                                    onNewPasswordChange = { forgotNewPassword = it },
                                    confirmPassword = forgotConfirmPassword,
                                    onConfirmPasswordChange = { forgotConfirmPassword = it },
                                    showPassword = showPassword,
                                    onToggleShowPassword = { showPassword = !showPassword },
                                    isLoading = isLoading,
                                    onSubmitReset = {
                                        clearMessages()
                                        if (forgotNewPassword != forgotConfirmPassword) {
                                            errorMessage = "Konfirmasi kata sandi baru tidak cocok"
                                            return@ForgotPasswordContent
                                        }
                                        isLoading = true
                                        viewModel.resetPassword(
                                            identifier = forgotIdentifier,
                                            birthDate = forgotBirthDate,
                                            securityAnswer = forgotSecurityAnswer,
                                            newPassword = forgotNewPassword
                                        ) { success, msg ->
                                            isLoading = false
                                            if (success) {
                                                successMessage = msg
                                            } else {
                                                errorMessage = msg
                                            }
                                        }
                                    },
                                    onGoBackToLogin = {
                                        clearMessages()
                                        currentMode = AuthMode.LOGIN
                                    },
                                    onGoToForgotEmail = {
                                        clearMessages()
                                        currentMode = AuthMode.FORGOT_EMAIL
                                    }
                                )
                            }

                            AuthMode.FORGOT_EMAIL -> {
                                ForgotEmailContent(
                                    phone = recoverPhone,
                                    onPhoneChange = { recoverPhone = it },
                                    birthDate = recoverBirthDate,
                                    onBirthDateChange = { recoverBirthDate = it },
                                    securityAnswer = recoverSecurityAnswer,
                                    onSecurityAnswerChange = { recoverSecurityAnswer = it },
                                    recoveredEmail = recoveredEmailResult,
                                    isLoading = isLoading,
                                    onSubmitFindEmail = {
                                        clearMessages()
                                        isLoading = true
                                        viewModel.recoverEmail(
                                            phone = recoverPhone,
                                            birthDate = recoverBirthDate,
                                            securityAnswer = recoverSecurityAnswer
                                        ) { success, msg, foundEmail ->
                                            isLoading = false
                                            if (success && foundEmail != null) {
                                                recoveredEmailResult = foundEmail
                                                successMessage = "Email Anda berhasil ditemukan!"
                                            } else {
                                                errorMessage = msg
                                            }
                                        }
                                    },
                                    onUseEmailToLogin = { foundEmail ->
                                        clearMessages()
                                        identifier = foundEmail
                                        currentMode = AuthMode.LOGIN
                                    },
                                    onGoBackToLogin = {
                                        clearMessages()
                                        currentMode = AuthMode.LOGIN
                                    }
                                )
                            }

                            else -> {}
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Keluar dari Aplikasi
                OutlinedButton(
                    onClick = onExitRequested,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_mandatory_exit_app")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar dari Aplikasi", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

/**
 * Dialog Profil Pengguna untuk melihat data akun & keluar akun.
 */
@Composable
fun UserProfileDialog(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 500.dp)
                .padding(vertical = 24.dp)
                .testTag("user_profile_dialog_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Profil Akun Anda",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("auth_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ProfileContent(
                    user = currentUser,
                    onLogout = {
                        viewModel.logout()
                        onDismissRequest()
                    }
                )
            }
        }
    }
}

/**
 * Dialog Otentikasi dan Profil Pengguna.
 * Jika pengguna sudah masuk, tampilkan Profil.
 * Jika belum masuk, tampilkan form Pendaftaran / Masuk Akun yang elegan dan dapat ditutup kapan saja.
 */
@Composable
fun AuthDialog(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    if (currentUser != null) {
        UserProfileDialog(
            viewModel = viewModel,
            onDismissRequest = onDismissRequest
        )
    } else {
        UserAuthDialog(
            viewModel = viewModel,
            onDismissRequest = onDismissRequest
        )
    }
}

/**
 * Dialog Pendaftaran & Masuk Akun untuk pengguna yang belum login.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAuthDialog(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    var currentMode by remember { mutableStateOf(AuthMode.REGISTER) }

    // Common fields
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Register fields
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regBirthDate by remember { mutableStateOf("") }
    var regSecurityQuestion by remember { mutableStateOf(EASY_SECURITY_QUESTIONS[0]) }
    var regSecurityAnswer by remember { mutableStateOf("") }
    var isQuestionDropdownExpanded by remember { mutableStateOf(false) }

    // Forgot Password fields
    var forgotIdentifier by remember { mutableStateOf("") }
    var forgotBirthDate by remember { mutableStateOf("") }
    var forgotSecurityAnswer by remember { mutableStateOf("") }
    var forgotNewPassword by remember { mutableStateOf("") }
    var forgotConfirmPassword by remember { mutableStateOf("") }

    // Forgot Email fields
    var recoverPhone by remember { mutableStateOf("") }
    var recoverBirthDate by remember { mutableStateOf("") }
    var recoverSecurityAnswer by remember { mutableStateOf("") }
    var recoveredEmailResult by remember { mutableStateOf<String?>(null) }

    // Feedback
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 520.dp)
                .padding(vertical = 16.dp)
                .testTag("auth_dialog_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Gold500),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = Emerald900,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (currentMode == AuthMode.REGISTER) "Daftar Akun Baru" else if (currentMode == AuthMode.LOGIN) "Masuk Akun" else "Pemulihan Akun",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Panduan Ibadah Islam",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("auth_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mode Tabs: REGISTER or LOGIN
                if (currentMode == AuthMode.LOGIN || currentMode == AuthMode.REGISTER) {
                    TabRow(
                        selectedTabIndex = if (currentMode == AuthMode.REGISTER) 0 else 1,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = currentMode == AuthMode.REGISTER,
                            onClick = {
                                clearMessages()
                                currentMode = AuthMode.REGISTER
                            },
                            text = {
                                Text(
                                    text = "Daftar Baru",
                                    fontWeight = if (currentMode == AuthMode.REGISTER) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.testTag("tab_mode_register")
                        )
                        Tab(
                            selected = currentMode == AuthMode.LOGIN,
                            onClick = {
                                clearMessages()
                                currentMode = AuthMode.LOGIN
                            },
                            text = {
                                Text(
                                    text = "Masuk Akun",
                                    fontWeight = if (currentMode == AuthMode.LOGIN) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.testTag("tab_mode_login")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error Banner
                if (errorMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(errorMessage!!, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                        }
                    }
                }

                // Success Banner
                if (successMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Emerald100),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = Emerald700, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(successMessage!!, color = Emerald900, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Dynamic Form Content
                when (currentMode) {
                    AuthMode.REGISTER -> {
                        RegisterContent(
                            name = regName,
                            onNameChange = { regName = it },
                            phone = regPhone,
                            onPhoneChange = { regPhone = it },
                            email = regEmail,
                            onEmailChange = { regEmail = it },
                            password = regPassword,
                            onPasswordChange = { regPassword = it },
                            confirmPassword = regConfirmPassword,
                            onConfirmPasswordChange = { regConfirmPassword = it },
                            birthDate = regBirthDate,
                            onBirthDateChange = { regBirthDate = it },
                            securityQuestion = regSecurityQuestion,
                            onSecurityQuestionChange = { regSecurityQuestion = it },
                            securityAnswer = regSecurityAnswer,
                            onSecurityAnswerChange = { regSecurityAnswer = it },
                            isQuestionDropdownExpanded = isQuestionDropdownExpanded,
                            onToggleDropdown = { isQuestionDropdownExpanded = it },
                            showPassword = showPassword,
                            onToggleShowPassword = { showPassword = !showPassword },
                            isLoading = isLoading,
                            onRegisterSubmit = {
                                clearMessages()
                                if (regName.isBlank() || regPhone.isBlank() || regEmail.isBlank() ||
                                    regPassword.isBlank() || regBirthDate.isBlank() || regSecurityAnswer.isBlank()
                                ) {
                                    errorMessage = "Semua kolom wajib diisi dengan lengkap"
                                    return@RegisterContent
                                }
                                if (regPassword != regConfirmPassword) {
                                    errorMessage = "Konfirmasi kata sandi tidak cocok"
                                    return@RegisterContent
                                }
                                isLoading = true
                                viewModel.registerUser(
                                    name = regName.trim(),
                                    phone = regPhone.trim(),
                                    email = regEmail.trim(),
                                    password = regPassword,
                                    birthDate = regBirthDate.trim(),
                                    securityQuestion = regSecurityQuestion,
                                    securityAnswer = regSecurityAnswer.trim()
                                ) { success, msg ->
                                    isLoading = false
                                    if (success) {
                                        successMessage = msg
                                        onDismissRequest()
                                    } else {
                                        errorMessage = msg
                                    }
                                }
                            },
                            onGoToLogin = {
                                clearMessages()
                                currentMode = AuthMode.LOGIN
                            }
                        )
                    }

                    AuthMode.LOGIN -> {
                        LoginContent(
                            identifier = identifier,
                            onIdentifierChange = { identifier = it },
                            password = password,
                            onPasswordChange = { password = it },
                            showPassword = showPassword,
                            onToggleShowPassword = { showPassword = !showPassword },
                            isLoading = isLoading,
                            onLoginSubmit = {
                                clearMessages()
                                if (identifier.isBlank() || password.isBlank()) {
                                    errorMessage = "Harap masukkan email/no HP dan kata sandi"
                                    return@LoginContent
                                }
                                isLoading = true
                                viewModel.loginUser(identifier.trim(), password) { success, msg ->
                                    isLoading = false
                                    if (success) {
                                        successMessage = msg
                                        onDismissRequest()
                                    } else {
                                        errorMessage = msg
                                    }
                                }
                            },
                            onGoToRegister = {
                                clearMessages()
                                currentMode = AuthMode.REGISTER
                            },
                            onGoToForgotPassword = {
                                clearMessages()
                                currentMode = AuthMode.FORGOT_PASSWORD
                            },
                            onGoToForgotEmail = {
                                clearMessages()
                                currentMode = AuthMode.FORGOT_EMAIL
                            }
                        )
                    }

                    AuthMode.FORGOT_PASSWORD -> {
                        ForgotPasswordContent(
                            identifier = forgotIdentifier,
                            onIdentifierChange = { forgotIdentifier = it },
                            birthDate = forgotBirthDate,
                            onBirthDateChange = { forgotBirthDate = it },
                            securityAnswer = forgotSecurityAnswer,
                            onSecurityAnswerChange = { forgotSecurityAnswer = it },
                            newPassword = forgotNewPassword,
                            onNewPasswordChange = { forgotNewPassword = it },
                            confirmPassword = forgotConfirmPassword,
                            onConfirmPasswordChange = { forgotConfirmPassword = it },
                            showPassword = showPassword,
                            onToggleShowPassword = { showPassword = !showPassword },
                            isLoading = isLoading,
                            onSubmitReset = {
                                clearMessages()
                                if (forgotNewPassword != forgotConfirmPassword) {
                                    errorMessage = "Konfirmasi kata sandi baru tidak cocok"
                                    return@ForgotPasswordContent
                                }
                                isLoading = true
                                viewModel.resetPassword(
                                    identifier = forgotIdentifier,
                                    birthDate = forgotBirthDate,
                                    securityAnswer = forgotSecurityAnswer,
                                    newPassword = forgotNewPassword
                                ) { success, msg ->
                                    isLoading = false
                                    if (success) {
                                        successMessage = msg
                                        onDismissRequest()
                                    } else {
                                        errorMessage = msg
                                    }
                                }
                            },
                            onGoBackToLogin = {
                                clearMessages()
                                currentMode = AuthMode.LOGIN
                            },
                            onGoToForgotEmail = {
                                clearMessages()
                                currentMode = AuthMode.FORGOT_EMAIL
                            }
                        )
                    }

                    AuthMode.FORGOT_EMAIL -> {
                        ForgotEmailContent(
                            phone = recoverPhone,
                            onPhoneChange = { recoverPhone = it },
                            birthDate = recoverBirthDate,
                            onBirthDateChange = { recoverBirthDate = it },
                            securityAnswer = recoverSecurityAnswer,
                            onSecurityAnswerChange = { recoverSecurityAnswer = it },
                            recoveredEmail = recoveredEmailResult,
                            isLoading = isLoading,
                            onSubmitFindEmail = {
                                clearMessages()
                                isLoading = true
                                viewModel.recoverEmail(
                                    phone = recoverPhone,
                                    birthDate = recoverBirthDate,
                                    securityAnswer = recoverSecurityAnswer
                                ) { success, msg, foundEmail ->
                                    isLoading = false
                                    if (success && foundEmail != null) {
                                        recoveredEmailResult = foundEmail
                                        successMessage = "Email Anda berhasil ditemukan!"
                                    } else {
                                        errorMessage = msg
                                    }
                                }
                            },
                            onUseEmailToLogin = { foundEmail ->
                                clearMessages()
                                identifier = foundEmail
                                currentMode = AuthMode.LOGIN
                            },
                            onGoBackToLogin = {
                                clearMessages()
                                currentMode = AuthMode.LOGIN
                            }
                        )
                    }

                    else -> {}
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tombol Batal / Nanti Saja
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_dialog_dismiss_button")
                ) {
                    Text("Nanti Saja / Tutup", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: UserEntity?,
    onLogout: () -> Unit
) {
    if (user == null) {
        Text("Data profil belum tersedia.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = user.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "Sahabat Terdaftar",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    ProfileItem(label = "Nomor Telepon", value = user.phone, icon = Icons.Default.Phone)
    ProfileItem(label = "Email", value = user.email, icon = Icons.Default.Email)
    ProfileItem(label = "Tanggal Lahir", value = user.birthDate, icon = Icons.Default.Cake)
    ProfileItem(label = "Pertanyaan Keamanan", value = user.securityQuestion, icon = Icons.Default.HelpOutline)

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onLogout,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("logout_button")
    ) {
        Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Keluar Akun", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProfileItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun LoginContent(
    identifier: String,
    onIdentifierChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onToggleShowPassword: () -> Unit,
    isLoading: Boolean,
    onLoginSubmit: () -> Unit,
    onGoToRegister: () -> Unit,
    onGoToForgotPassword: () -> Unit,
    onGoToForgotEmail: () -> Unit
) {
    Text(
        text = "Masuk untuk menyimpan data ibadah dan berbagi kisah dengan jamaah.",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    OutlinedTextField(
        value = identifier,
        onValueChange = onIdentifierChange,
        label = { Text("Email atau Nomor Telepon") },
        placeholder = { Text("contoh: 08123456789 atau user@email.com") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("login_identifier_input")
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Kata Sandi") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onToggleShowPassword) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onLoginSubmit() }),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("login_password_input")
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Recovery Links
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = onGoToForgotEmail,
            modifier = Modifier.testTag("forgot_email_link")
        ) {
            Text("Lupa Email?", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }

        TextButton(
            onClick = onGoToForgotPassword,
            modifier = Modifier.testTag("forgot_password_link")
        ) {
            Text("Lupa Kata Sandi?", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onLoginSubmit,
        enabled = !isLoading && identifier.isNotBlank() && password.isNotBlank(),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("login_submit_button")
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text("Masuk", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Belum punya akun? ", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = "Daftar Sekarang",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onGoToRegister() }
                .testTag("register_now_link")
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterContent(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    birthDate: String,
    onBirthDateChange: (String) -> Unit,
    securityQuestion: String,
    onSecurityQuestionChange: (String) -> Unit,
    securityAnswer: String,
    onSecurityAnswerChange: (String) -> Unit,
    isQuestionDropdownExpanded: Boolean,
    onToggleDropdown: (Boolean) -> Unit,
    showPassword: Boolean,
    onToggleShowPassword: () -> Unit,
    isLoading: Boolean,
    onRegisterSubmit: () -> Unit,
    onGoToLogin: () -> Unit
) {
    Text(
        text = "Lengkapi data untuk pendaftaran akun. Tanggal lahir dan pertanyaan keamanan digunakan untuk memulihkan akun bila lupa.",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 14.dp)
    )

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Nama Lengkap / Panggilan") },
        placeholder = { Text("contoh: Ahmad Fauzi") },
        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_name_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = phone,
        onValueChange = onPhoneChange,
        label = { Text("Nomor Telepon (HP)") },
        placeholder = { Text("contoh: 081234567890") },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_phone_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Alamat Email") },
        placeholder = { Text("contoh: ahmad@gmail.com") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_email_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = birthDate,
        onValueChange = onBirthDateChange,
        label = { Text("Tanggal Lahir (DD-MM-YYYY)") },
        placeholder = { Text("contoh: 27-05-1991") },
        leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) },
        supportingText = { Text("Contoh: 27-05-1991 (Hari-Bulan-Tahun)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_birthdate_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Pertanyaan Keamanan Dropdown
    ExposedDropdownMenuBox(
        expanded = isQuestionDropdownExpanded,
        onExpandedChange = { onToggleDropdown(!isQuestionDropdownExpanded) }
    ) {
        OutlinedTextField(
            value = securityQuestion,
            onValueChange = {},
            readOnly = true,
            label = { Text("Pertanyaan Keamanan (Gampang)") },
            leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isQuestionDropdownExpanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .testTag("reg_question_input")
        )
        ExposedDropdownMenu(
            expanded = isQuestionDropdownExpanded,
            onDismissRequest = { onToggleDropdown(false) }
        ) {
            EASY_SECURITY_QUESTIONS.forEach { q ->
                DropdownMenuItem(
                    text = { Text(q, fontSize = 13.sp) },
                    onClick = {
                        onSecurityQuestionChange(q)
                        onToggleDropdown(false)
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = securityAnswer,
        onValueChange = onSecurityAnswerChange,
        label = { Text("Jawaban Pertanyaan Keamanan") },
        placeholder = { Text("contoh: Nasi Goreng") },
        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_answer_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Kata Sandi") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onToggleShowPassword) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_password_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = { Text("Konfirmasi Kata Sandi") },
        leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reg_confirm_password_input")
    )

    Spacer(modifier = Modifier.height(18.dp))

    Button(
        onClick = onRegisterSubmit,
        enabled = !isLoading && phone.isNotBlank() && email.isNotBlank() &&
                password.isNotBlank() && birthDate.isNotBlank() && securityAnswer.isNotBlank(),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("register_submit_button")
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text("Daftar Akun", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Sudah punya akun? ", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = "Masuk di sini",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onGoToLogin() }
                .testTag("login_here_link")
        )
    }
}

@Composable
private fun ForgotPasswordContent(
    identifier: String,
    onIdentifierChange: (String) -> Unit,
    birthDate: String,
    onBirthDateChange: (String) -> Unit,
    securityAnswer: String,
    onSecurityAnswerChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onToggleShowPassword: () -> Unit,
    isLoading: Boolean,
    onSubmitReset: () -> Unit,
    onGoBackToLogin: () -> Unit,
    onGoToForgotEmail: () -> Unit
) {
    Text(
        text = "Verifikasi tanggal lahir dan jawaban pertanyaan keamanan Anda untuk membuat kata sandi baru.",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 14.dp)
    )

    OutlinedTextField(
        value = identifier,
        onValueChange = onIdentifierChange,
        label = { Text("Email atau Nomor Telepon") },
        placeholder = { Text("contoh: ahmad@gmail.com atau 08123456789") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("forgot_pwd_identifier_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = birthDate,
        onValueChange = onBirthDateChange,
        label = { Text("Tanggal Lahir (DD-MM-YYYY)") },
        placeholder = { Text("contoh: 27-05-1991") },
        supportingText = { Text("Format: Tanggal-Bulan-Tahun (contoh: 27-05-1991)") },
        leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("forgot_pwd_birthdate_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = securityAnswer,
        onValueChange = onSecurityAnswerChange,
        label = { Text("Jawaban Pertanyaan Keamanan") },
        placeholder = { Text("contoh: Makanan / Warna / Kota favorit Anda") },
        leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("forgot_pwd_answer_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = newPassword,
        onValueChange = onNewPasswordChange,
        label = { Text("Kata Sandi Baru") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onToggleShowPassword) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("forgot_pwd_new_password_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = { Text("Konfirmasi Kata Sandi Baru") },
        leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("forgot_pwd_confirm_password_input")
    )

    Spacer(modifier = Modifier.height(18.dp))

    Button(
        onClick = onSubmitReset,
        enabled = !isLoading && identifier.isNotBlank() && birthDate.isNotBlank() &&
                securityAnswer.isNotBlank() && newPassword.isNotBlank(),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("forgot_pwd_submit_button")
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text("Simpan Kata Sandi Baru & Masuk", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = onGoToForgotEmail) {
            Text("Lupa Email Terdaftar?", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }
        TextButton(onClick = onGoBackToLogin) {
            Text("Kembali ke Masuk", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ForgotEmailContent(
    phone: String,
    onPhoneChange: (String) -> Unit,
    birthDate: String,
    onBirthDateChange: (String) -> Unit,
    securityAnswer: String,
    onSecurityAnswerChange: (String) -> Unit,
    recoveredEmail: String?,
    isLoading: Boolean,
    onSubmitFindEmail: () -> Unit,
    onUseEmailToLogin: (String) -> Unit,
    onGoBackToLogin: () -> Unit
) {
    Text(
        text = "Lupa alamat email? Masukkan nomor telepon, tanggal lahir, dan jawaban pertanyaan keamanan yang didaftarkan.",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 14.dp)
    )

    OutlinedTextField(
        value = phone,
        onValueChange = onPhoneChange,
        label = { Text("Nomor Telepon (HP) Terdaftar") },
        placeholder = { Text("contoh: 081234567890") },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("forgot_email_phone_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = birthDate,
        onValueChange = onBirthDateChange,
        label = { Text("Tanggal Lahir (DD-MM-YYYY)") },
        placeholder = { Text("contoh: 27-05-1991") },
        supportingText = { Text("Format: Tanggal-Bulan-Tahun (contoh: 27-05-1991)") },
        leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("forgot_email_birthdate_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = securityAnswer,
        onValueChange = onSecurityAnswerChange,
        label = { Text("Jawaban Pertanyaan Keamanan") },
        placeholder = { Text("contoh: Makanan / Warna / Kota favorit Anda") },
        leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("forgot_email_answer_input")
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onSubmitFindEmail,
        enabled = !isLoading && phone.isNotBlank() && birthDate.isNotBlank() && securityAnswer.isNotBlank(),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("forgot_email_submit_button")
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text("Cari Email Saya", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }

    if (recoveredEmail != null) {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Alhamdulillah! Email Anda Ditemukan:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = recoveredEmail,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onUseEmailToLogin(recoveredEmail) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Gunakan Email Ini untuk Masuk", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    TextButton(
        onClick = onGoBackToLogin,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Kembali ke Masuk", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
    }
}
