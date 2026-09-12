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
                fontFamily = LexendFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextMidnight,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 64.dp)
                .background(SurfaceCard, ButtonShape)
                .border(2.5.dp, if (isError) CoralBerry else ModernBorder, ButtonShape)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(3.dp)
                    .padding(horizontal = 12.dp)
                    .background(ModernBorderSoft, RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
            )
            
            BasicTextField(
                value = value,
                onValueChange = {
                    if (it.length <= maxLength) onValueChange(it)
                },
                textStyle = TextStyle(
                    fontFamily = LexendFontFamily,
                    fontSize = 22.sp,
                    color = TextMidnight,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = singleLine,
                keyboardOptions = keyboardOptions,
                cursorBrush = SolidColor(PrimaryJoy),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty() && placeholder.isNotEmpty()) {
                                Text(
                                    text = placeholder,
                                    fontFamily = LexendFontFamily,
                                    fontSize = 22.sp,
                                    color = TextMuted.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            innerTextField()
                        }
                        
                        if (maxLength < Int.MAX_VALUE) {
                            Text(
                                text = "${value.length}/$maxLength",
                                fontFamily = LexendFontFamily,
                                fontSize = 15.sp,
                                color = TextMuted.copy(alpha = 0.6f),
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
