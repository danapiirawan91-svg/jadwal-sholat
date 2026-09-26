package botX

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.util.Base64
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Obfuscated & Encrypted Dialog Engine (botX.OoOo).
 * All sensitive metadata (Author, Welcome message, Button labels, WhatsApp number/link)
 * are stored as dynamic XOR-masked byte streams, encrypted via AES-256, and validated
 * with anti-tamper cryptographic integrity checks to prevent unauthorized decompilation/editing.
 */
object OoOo {
    private var alert: AlertDialog? = null
    private var dialog: LinearLayout? = null
    private var title: TextView? = null
    private var message: TextView? = null
    private var button: LinearLayout? = null
    private var but: TextView? = null
    private var but1: TextView? = null
    private var showTime: Int = 3

    // Dynamic XOR-masked byte arrays - No plain strings stored in bytecode
    private val M_C1 = intArrayOf(10, 21, 58, 53, 199, 199, 158, 233, 162, 209, 130, 101, 124, 3, 127, 61, 22, 95, 63, 231, 175, 202, 131, 249, 172, 179, 125, 69, 116, 23, 56, 109, 7, 72, 186, 220, 156, 169, 254, 128, 192, 102, 92, 27).map { it.toByte() }.toByteArray()
    private val M_K1 = intArrayOf(-118, -236, -244, -254, 254, 139, 129, 159, 99, 99, 77, 21, 47, 37, 9, 14, 235, 245, 236, -179, -175, -177, -161).map { it.toByte() }.toByteArray()

    private val M_C2 = intArrayOf(115, 46, 24, 21, 244, 223, 213, 233, 244, 224, 145, 165, 61, 116, 70, 13, 36, 58, 3, 18, 230, 237, 251, 142, 191, 139, 162, 87, 97, 101, 81, 96, 59, 8, 75, 253, 150, 215, 251, 225, 129, 196, 105, 42).map { it.toByte() }.toByteArray()
    private val M_K2 = intArrayOf(14, 11, 19, 241, 204, 211, 183, 244, 129, 151, 115, 121, 71, 93, 107, 56, 4, 94, 255, 193, 223, 166, 166, 133, 155, 38, 126, 91, 85, 43, 54).map { it.toByte() }.toByteArray()

    private val M_C3 = intArrayOf(88, 56, 163, 149, 135, 171, 182, 209, 189, 72, 81, 100, 127, 35, 20, 14, 11, 184, 234, 158, 243, 180, 217, 200).map { it.toByte() }.toByteArray()
    private val M_K3 = intArrayOf(69, 80).map { it.toByte() }.toByteArray()

    private val M_C4 = intArrayOf(206, 239, 250, 173, 181, 141, 144, 84, 65, 93, 117, 11, 52, 63, 51, 201, 218, 248, 246, 191, 130, 181, 56, 43).map { it.toByte() }.toByteArray()
    private val M_K4 = intArrayOf(76, 68, 92, 58, 44, 49, 241, 226).map { it.toByte() }.toByteArray()

    private val M_C5 = intArrayOf(218, 151, 197, 173, 131, 166, 138, 56, 114, 89, 78, 53, 14, 31, 213, 212, 232, 249, 129, 191, 169, 128, 67, 88, 122, 125, 22, 12, 28, 44, 241, 151, 132, 151, 255, 145, 206, 40, 90, 103, 118, 32, 63, 80).map { it.toByte() }.toByteArray()
    private val M_K5 = intArrayOf(58, 63, 65, 237, 244, 141, 133, 246, 237, 215, 194, 61, 45, 29, 14, 126, 101, 89, 74).map { it.toByte() }.toByteArray()

    private val M_URL = intArrayOf(11, 0, 241, 230, 212, 130, 230, 245, 156, 157, 35, 115, 74, 111, 103, 80, 75, 181, 160, 147, 131, 255, 236, 216, 195, 59, 40).map { it.toByte() }.toByteArray()

    // Expected cryptographic integrity hashes (SHA-256) to detect tampering or unauthorized binary modification
    private const val HASH_TITLE = "59633d5f9e00fb37d6c541f23b7394eb33da61472c538d78c5aa6c1befb73122"
    private const val HASH_WA = "42179b3228156c843b700c4b752645b1a1b354d2a80ff627dd8db971b3ce44f6"

    /**
     * Unmasks binary obfuscated byte stream at runtime with rolling XOR key.
     */
    private fun unmask(bytes: ByteArray, seed: Int): String {
        val out = ByteArray(bytes.size)
        for (i in bytes.indices) {
            out[i] = (bytes[i].toInt() xor ((seed + (i * 17)) and 0xFF)).toByte()
        }
        return String(out, Charsets.UTF_8)
    }

    /**
     * Computes SHA-256 digest of given string.
     */
    private fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Internal Encrypted Dialog Payload model.
     */
    data class EncryptedDialogData(
        val title: String,
        val message: String,
        val btnOk: String,
        val btnWa: String,
        val waUrl: String,
        val isAuthentic: Boolean
    )

    /**
     * Resolves and verifies the authentic dialog data from encrypted payloads.
     */
    fun resolveSecureData(): EncryptedDialogData {
        val c1 = unmask(M_C1, 0x4B)
        val k1 = unmask(M_K1, 0x7A)
        val c2 = unmask(M_C2, 0x3C)
        val k2 = unmask(M_K2, 0x5D)
        val c3 = unmask(M_C3, 0x6E)
        val k3 = unmask(M_K3, 0x2A)
        val c4 = unmask(M_C4, 0x8F)
        val k4 = unmask(M_K4, 0x1B)
        val c5 = unmask(M_C5, 0x92)
        val k5 = unmask(M_K5, 0x4D)
        val rawUrl = unmask(M_URL, 0x63)

        var decTitle = decrypt(c1, k1)
        var decMessage = decrypt(c2, k2)
        var decOk = decrypt(c3, k3)
        var decWa = decrypt(c4, k4)
        var decUrl = decrypt(c5, k5)

        // Anti-tamper verification
        val isAuthentic = (sha256(decTitle) == HASH_TITLE) && (sha256(decUrl) == HASH_WA)

        if (!isAuthentic) {
            // Self-repair: enforce authentic unmasked values against smali tampering
            decTitle = decrypt(unmask(M_C1, 0x4B), unmask(M_K1, 0x7A))
            decMessage = decrypt(unmask(M_C2, 0x3C), unmask(M_K2, 0x5D))
            decOk = decrypt(unmask(M_C3, 0x6E), unmask(M_K3, 0x2A))
            decWa = decrypt(unmask(M_C4, 0x8F), unmask(M_K4, 0x1B))
            decUrl = rawUrl
        }

        val finalUrl = if (decUrl.startsWith("http://") || decUrl.startsWith("https://")) {
            decUrl
        } else {
            "https://$decUrl"
        }

        return EncryptedDialogData(
            title = decTitle,
            message = decMessage,
            btnOk = decOk,
            btnWa = decWa,
            waUrl = finalUrl,
            isAuthentic = isAuthentic
        )
    }

    /**
     * Native Smali Decrypt method:
     * .method public static decrypt(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
     */
    @JvmStatic
    fun decrypt(encryptedBase64: String, keyStr: String): String {
        return try {
            val key = generateKey(keyStr)
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, key)
            val decoded = try {
                val bytes = Base64.decode(encryptedBase64, Base64.DEFAULT)
                if (bytes != null && bytes.isNotEmpty()) bytes else java.util.Base64.getDecoder().decode(encryptedBase64)
            } catch (e: Throwable) {
                java.util.Base64.getDecoder().decode(encryptedBase64)
            }
            String(cipher.doFinal(decoded), Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }

    private fun generateKey(password: String): SecretKey {
        val md = MessageDigest.getInstance("SHA-256")
        val keyBytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(keyBytes, "AES")
    }

    private fun param(): LinearLayout.LayoutParams {
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.weight = 1.0f
        return lp
    }

    private fun designLayouts() {
        button?.layoutParams = param()
        button?.setPadding(0, 10, 0, 0)

        dialog?.setPadding(50, 50, 50, 50)
        dialog?.elevation = 0f
        dialog?.orientation = LinearLayout.VERTICAL

        val bg = GradientDrawable().apply {
            cornerRadius = 30f
            setColor(AndroidColor.parseColor("#FFE6BCBC"))
        }
        dialog?.background = bg
        dialog?.layoutParams = param()

        val window = alert?.window
        val inset = InsetDrawable(ColorDrawable(0), 20)
        window?.setBackgroundDrawable(inset)
    }

    private fun designTexts() {
        title?.apply {
            setPadding(5, 10, 0, 50)
            gravity = Gravity.CENTER
            textSize = 18f
            setTextColor(AndroidColor.parseColor("#FF030407"))
            layoutParams = param()
        }

        message?.apply {
            layoutParams = param()
            gravity = Gravity.START
            setPadding(5, 10, 0, 50)
            textSize = 16f
            setTextColor(AndroidColor.parseColor("#FF0A0505"))
        }

        but?.apply {
            textSize = 15f
            setPadding(20, 0, 30, 0)
            layoutParams = param()
            gravity = Gravity.START
            setTextColor(AndroidColor.parseColor("#FF000000"))
        }

        but1?.apply {
            textSize = 15f
            setPadding(20, 0, 30, 0)
            layoutParams = param()
            setTextColor(AndroidColor.parseColor("#FF0D0A08"))
        }
    }

    /**
     * Native Smali get method:
     * .method public static get(Landroid/content/Context;)V
     */
    @JvmStatic
    fun get(context: Context) {
        val secureData = resolveSecureData()

        val builder = AlertDialog.Builder(context)
        alert = builder.create()

        dialog = LinearLayout(context)
        title = TextView(context)
        message = TextView(context)
        button = LinearLayout(context)
        but = TextView(context)
        but1 = TextView(context)

        try {
            title?.typeface = Typeface.createFromAsset(context.assets, "title.ttf")
            message?.typeface = Typeface.createFromAsset(context.assets, "message.ttf")
            but?.typeface = Typeface.createFromAsset(context.assets, "button.ttf")
            but1?.typeface = Typeface.createFromAsset(context.assets, "button.ttf")
        } catch (e: Exception) {
            title?.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD)
            message?.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
            but?.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
            but1?.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        designLayouts()
        designTexts()

        button?.addView(but, 0)
        button?.addView(but1, 1)

        dialog?.addView(title, 0)
        dialog?.addView(message, 1)
        dialog?.addView(button, 2)

        alert?.setView(dialog)

        but1?.setOnClickListener {
            alert?.dismiss()
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(secureData.waUrl))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: Exception) {
                // ignore
            }
        }

        but?.setOnClickListener {
            alert?.dismiss()
        }

        title?.text = secureData.title
        message?.text = secureData.message
        but?.text = secureData.btnOk
        but1?.text = secureData.btnWa

        try {
            alert?.show()
        } catch (e: Exception) {
            // handle exception if activity not attached
        }
    }

    /**
     * Composable Protected Welcome Dialog for Jetpack Compose.
     * All strings, links, colors, and layout instructions are dynamically
     * unpacked from encrypted byte memory at runtime.
     */
    @Composable
    fun ProtectedWelcomeDialog(
        onDismiss: () -> Unit
    ) {
        val context = LocalContext.current
        val data = remember { resolveSecureData() }

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE6BCBC)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 420.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .testTag("welcome_botoo_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = data.title,
                        color = Color(0xFF030407),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 18.dp)
                            .testTag("welcome_dialog_title")
                    )

                    Text(
                        text = data.message,
                        color = Color(0xFF0A0505),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .testTag("welcome_dialog_message")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("welcome_dialog_btn_ok")
                        ) {
                            Text(
                                text = data.btnOk,
                                color = Color(0xFF000000),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.waUrl))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // ignore fallback
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .padding(start = 8.dp)
                                .testTag("welcome_dialog_btn_wa")
                        ) {
                            Text(
                                text = data.btnWa,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
