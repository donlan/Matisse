package github.leavesczy.matisse.internal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.matisse.R
import github.leavesczy.matisse.internal.logic.MatissePreviewButtonViewState
import github.leavesczy.matisse.internal.logic.MatisseSureButtonViewState

/**
 * @Author: leavesCZY
 * @Date: 2022/6/1 19:19
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
internal fun MatisseBottomBar(
    previewButtonViewState: MatissePreviewButtonViewState,
    sureButtonViewState: MatisseSureButtonViewState
) {
    Box(
        modifier = Modifier
            .shadow(elevation = 4.dp)
            .navigationBarsPadding()
            .fillMaxWidth()
            .height(height = 56.dp)
            .background(color = MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                .then(
                    other = if (previewButtonViewState.clickable) {
                        Modifier.clickable(onClick = previewButtonViewState.onClick)
                    } else {
                        Modifier
                    }
                )
                .fillMaxHeight()
                .padding(horizontal = 24.dp)
                .wrapContentSize(align = Alignment.Center),
            textAlign = TextAlign.Center,
            style = TextStyle(
                color = if (previewButtonViewState.clickable) {
                    MaterialTheme.colorScheme.onSecondary
                } else {
                    MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.3f)
                },
                fontSize = 16.sp
            ),
            text = previewButtonViewState.text
        )
        Text(
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd)
                .then(
                    other = if (sureButtonViewState.clickable) {
                        Modifier.clickable(onClick = sureButtonViewState.onClick)
                    } else {
                        Modifier
                    }
                )
                .fillMaxHeight()
                .padding(horizontal = 24.dp)
                .wrapContentSize(align = Alignment.Center),
            textAlign = TextAlign.Center,
            style = TextStyle(
                color = if (sureButtonViewState.clickable) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(0.3f)
                },
                fontSize = 16.sp
            ),
            text = sureButtonViewState.text
        )
    }
}