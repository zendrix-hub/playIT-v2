package com.playit.app.presentation.dashboard

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.playit.app.presentation.components.GummyBackButton
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LeafShadow
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.SandDeep
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.SkyDeep
import com.playit.app.presentation.theme.Ube
import com.playit.app.presentation.theme.UbeShadow
import java.io.File

@Composable
fun ReportPreviewScreen(
    pdfFilePath: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val pdfFile = remember(pdfFilePath) { File(pdfFilePath) }
    val fileSizeKb = remember(pdfFilePath) { if (pdfFile.exists()) pdfFile.length() / 1024 else 0L }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SkyDeep,
                        Sky,
                        Sand,
                        SandDeep
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header — Box overlay instead of Row + matched-width Spacer,
            // so the title stays centered regardless of GummyBackButton's
            // actual measured width.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                GummyBackButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "Report Ready",
                    fontFamily = LexendFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Ink,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Cloud),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = BorderStroke(3.dp, DarkBrownOutline)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Description,
                        contentDescription = "PDF Report",
                        tint = Ube,
                        modifier = Modifier
                            .size(56.dp)
                            .padding(bottom = 12.dp)
                    )

                    Text(
                        text = pdfFile.name,
                        fontFamily = LexendFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "File Size: $fileSizeKb KB | Format: PDF",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        color = InkSoft
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Saved locally to application documents folder.",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        color = InkSoft
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GummyButton(
                        text = "Open PDF Document",
                        onClick = {
                            if (pdfFile.exists()) {
                                try {
                                    val uri: Uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        pdfFile
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "application/pdf")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(context, "No PDF viewer app found on device.", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "File does not exist.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        backgroundColor = Ube,
                        shadowColor = UbeShadow,
                        contentColor = Cloud,
                        fontSize = 16,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GummyButton(
                        text = "Share Report",
                        icon = Icons.Filled.Share,
                        onClick = {
                            if (pdfFile.exists()) {
                                try {
                                    val uri: Uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        pdfFile
                                    )
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/pdf"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share PDF Progress Report"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Share failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        backgroundColor = Leaf,
                        shadowColor = LeafShadow,
                        contentColor = Cloud,
                        fontSize = 16,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                }
            }
        }
    }
}
