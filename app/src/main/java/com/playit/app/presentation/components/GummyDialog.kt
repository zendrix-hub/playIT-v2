package com.playit.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.playit.app.presentation.theme.*

@Composable
fun GummyDialog(
    title: String,
    body: String,
    confirmText: String = "OK",
    dismissText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmColor: Color = LearningBlue,
    confirmShadowColor: Color = LearningBlueShadow,
    icon: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
) {
    Dialog(onDismissRequest = onDismiss) {
        GummyStaticContainer(
            modifier = Modifier.fillMaxWidth(),
            faceColor = CreamWhite,
            shadowColor = CreamWhiteShadow,
            shape = RoundedCornerShape(28.dp),
            strokeWidth = 3.dp,
            strokeColor = DarkBrownOutline,
            depthHeight = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )
                if (body.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = body,
                        fontSize = 20.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )
                }
                if (content != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    content()
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (dismissText != null) {
                        TextButton(onClick = onDismiss, modifier = Modifier.defaultMinSize(minHeight = 64.dp)) {
                            Text(text = dismissText, color = TextSecondary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    GummyButton(
                        text = confirmText,
                        onClick = onConfirm,
                        backgroundColor = confirmColor,
                        shadowColor = confirmShadowColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
