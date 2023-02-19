package github.leavesczy.matisse.internal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import github.leavesczy.matisse.R
import github.leavesczy.matisse.internal.logic.MatisseViewModel

/**
 * @Author: CZY
 * @Date: 2022/6/1 19:14
 * @Desc:
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MatissePreviewPage(viewModel: MatisseViewModel) {
    val matissePreviewViewState = viewModel.matissePreviewViewState
    val visible = matissePreviewViewState.visible
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(animationSpec = tween(
            durationMillis = 240, easing = LinearEasing
        ), initialOffsetX = { it }),
        exit = slideOutHorizontally(animationSpec = tween(
            durationMillis = 240, easing = LinearEasing
        ), targetOffsetX = { it }),
    ) {
        BackHandler(enabled = visible, onBack = {
            viewModel.dismissPreviewPage()
        })
        val previewResources = matissePreviewViewState.previewResources
        val initialPage = matissePreviewViewState.initialPage
        val pagerState = rememberPagerState(initialPage = initialPage)
        val currentImageIndex by remember {
            derivedStateOf {
                val viewState = viewModel.matissePreviewViewState
                viewState.selectedResources.indexOf(element = viewState.previewResources[pagerState.currentPage])
            }
        }
        val checkboxEnabled by remember {
            derivedStateOf {
                val viewState = viewModel.matissePreviewViewState
                currentImageIndex > -1 || viewState.selectedResources.size < viewState.matisse.maxSelectable
            }
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(), contentWindowInsets = WindowInsets(
                left = 0.dp, right = 0.dp, top = 0.dp, bottom = 0.dp
            ), containerColor = colorResource(id = R.color.matisse_preview_page_background_color)
        ) { paddingValues ->
            if (previewResources.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues)
                ) {
                    HorizontalPager(
                        modifier = Modifier.fillMaxSize(),
                        count = previewResources.size,
                        state = pagerState,
                        key = { index ->
                            previewResources[index].key
                        }
                    ) { pageIndex ->
                        val mediaResource = previewResources[pageIndex]
                        AsyncImage(
                            modifier = Modifier
                                .align(alignment = Alignment.Center)
                                .verticalScroll(state = rememberScrollState())
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .navigationBarsPadding(),
                            model = mediaResource.uri,
                            contentScale = ContentScale.FillWidth,
                            contentDescription = mediaResource.displayName
                        )
                    }
                    MatisseCheckbox(
                        modifier = Modifier
                            .align(alignment = Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(top = 20.dp, end = 25.dp),
                        size = 28.dp,
                        text = if (currentImageIndex > -1) {
                            (currentImageIndex + 1).toString()
                        } else {
                            ""
                        },
                        checked = currentImageIndex > -1,
                        enabled = checkboxEnabled,
                        onClick = {
                            viewModel.onMediaCheckChanged(mediaResource = previewResources[pagerState.currentPage])
                        })
                }
            }
        }
    }
}