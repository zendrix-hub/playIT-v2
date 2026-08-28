package com.playit.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.*

@Composable
fun GummyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    maxLength: Int = 20,
    singleLine: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 64.dp)
                .background(Color.White, RoundedCornerShape(24.dp))
                .border(3.dp, if (isError) DestructiveRed else DarkBrownOutline, RoundedCornerShape(24.dp))
        ) {
            // Simulated inset depth band at bottom inside the box
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(3.dp)
                    .padding(horizontal = 12.dp)
                    .background(BorderColor, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            )
            
            BasicTextField(
                value = value,
                onValueChange = {
                    if (it.length <= maxLength) onValueChange(it)
                },
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = singleLine,
                keyboardOptions = keyboardOptions,
                cursorBrush = SolidColor(LearningBlue),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty() && placeholder.isNotEmpty()) {
                                Text(
                                    text = placeholder,
                                    fontSize = 24.sp,
                                    color = TextSecondary.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            innerTextField()
                        }
                        
                        if (maxLength < Int.MAX_VALUE) {
                            Text(
                                text = "${value.length}/$maxLength",
                                fontSize = 16.sp,
                                color = TextSecondary.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}
