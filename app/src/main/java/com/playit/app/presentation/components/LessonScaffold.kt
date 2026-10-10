package com.playit.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.LocalPlayItDimens

@Composable
fun LessonScaffold(
    topBar: @Composable () -> Unit,
    header: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val d = LocalPlayItDimens.current
    Column(modifier.fillMaxSize().statusBarsPadding()) {
        topBar()
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Box(Modifier.widthIn(max = d.contentMaxWidth)) { header() }
        }
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .widthIn(max = d.contentMaxWidth)
                    .fillMaxWidth()
                    .heightIn(min = maxHeight)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = d.screenPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                content = content
            )
        }
        Box(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = d.screenPadding, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) { Box(Modifier.widthIn(max = d.contentMaxWidth)) { bottomBar() } }
    }
}
