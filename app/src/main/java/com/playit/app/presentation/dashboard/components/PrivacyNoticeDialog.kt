package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.theme.DialogShape
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.ModernBorder
import com.playit.app.presentation.theme.PrimaryJoy
import com.playit.app.presentation.theme.PrimaryJoyShadow
import com.playit.app.presentation.theme.SurfaceCard
import com.playit.app.presentation.theme.TextMidnight
import com.playit.app.presentation.theme.TextMuted

@Composable
fun PrivacyNoticeDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = DialogShape,
            color = SurfaceCard,
            border = BorderStroke(2.5.dp, ModernBorder),
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your child's data",
                    fontFamily = LexendFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextMidnight
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "PlayIT works without the internet. Everything stays on this device.",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextMuted
                    )
                    Text(
                        text = "PlayIT never records or saves your child's voice. It listens only while the microphone button is active, and only the result (right or not yet) is saved.",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextMuted
                    )
                    Text(
                        text = "Saved on this device: your child's name and animal, stars, and which letters they have practised.",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextMuted
                    )
                    Text(
                        text = "Nothing is sent anywhere. A report leaves the device only if you share it.",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextMuted
                    )
                    Text(
                        text = "To remove a child's data, tap Delete this child's data on their card.",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                GummyButton(
                    text = "OK",
                    backgroundColor = PrimaryJoy,
                    shadowColor = PrimaryJoyShadow,
                    contentColor = Color.White,
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }
        }
    }
}
