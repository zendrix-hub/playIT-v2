package com.playit.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.GentleCorrectionOrange

@Composable
fun ErrorStateContent(
    message: String = "Oops! Let's try again!",
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DockedMascotWithBubble(
            message = message,
            mascotState = MascotState.ENCOURAGING
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        GummyButton(
            text = "Try Again",
            onClick = onRetry,
            backgroundColor = GentleCorrectionOrange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
