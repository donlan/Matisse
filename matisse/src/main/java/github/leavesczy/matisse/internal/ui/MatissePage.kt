package github.leavesczy.matisse.internal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import github.leavesczy.matisse.MediaResource
import github.leavesczy.matisse.R
import github.leavesczy.matisse.internal.logic.MatisseState
import github.leavesczy.matisse.internal.logic.MatisseViewModel

/**
 * @Author: CZY
 * @Date: 2022/5/31 16:36
 * @Desc:
 */
@Composable
internal fun MatissePage(
    viewModel: MatisseViewModel,
    onRequestTakePicture: () -> Unit,
    onRequestPermission: () -> Unit,
    onRequestOpenFolder: () -> Unit
) {
    val images = viewModel.matisseViewState.pageSource?.collectAsLazyPagingItems()
    val matisseViewState = viewModel.matisseViewState
    val maxSelectable = matisseViewState.matisse.maxSelectable
    val selectedMediaResources = matisseViewState.selectedResources
    val allBucket = matisseViewState.allBucket
    val selectedBucket = matisseViewState.selectedBucket
    val supportCapture = selectedBucket.supportCapture
    val loadState = viewModel.matisseViewState.state
    val lazyGridState by remember(key1 = selectedBucket.id) {
        mutableStateOf(
            value = LazyGridState(
                firstVisibleItemIndex = 0,
                firstVisibleItemScrollOffset = 0
            )
        )
    }
    val context = LocalContext.current
    val localConfiguration = LocalConfiguration.current
    val localDensity = LocalDensity.current
    val spanCount = remember {
        context.resources.getInteger(R.integer.matisse_image_span_count)
    }
    val imageItemWidthPx = remember {
        with(localDensity) {
            (localConfiguration.screenWidthDp.dp.toPx() / spanCount).toInt()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            MatisseTopBar(
                allBucket = allBucket,
                selectedBucket = selectedBucket,
                onSelectBucket = {
                    viewModel.onSelectBucket(bucket = it)
                },
                onRequestOpenFolder = onRequestOpenFolder
            )
        },
        bottomBar = {
            MatisseBottomBar(
                previewButtonViewState = viewModel.previewButtonViewState,
                sureButtonViewState = viewModel.sureButtonViewState
            )
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            when (loadState) {
                MatisseState.ImagesError -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.matisse_load_error),
                                Modifier
                                    .wrapContentSize()
                                    .padding(start = 12.dp),
                                color = Color.Gray,
                            )
                            Button(
                                onClick = { viewModel.retryLoad() },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                        alpha = 0.5f
                                    )
                                )
                            ) {
                                Text(
                                    text = stringResource(id = R.string.matisse_retry),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        }
                    }
                }

                MatisseState.ImagesLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Loading...",
                                Modifier
                                    .wrapContentSize()
                                    .padding(start = 12.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }

                MatisseState.PermissionDenied -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.matisse_missing_permission),
                                Modifier
                                    .wrapContentSize()
                                    .padding(start = 12.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Button(
                                onClick = { onRequestPermission.invoke() },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                        alpha = 0.5f
                                    )
                                )
                            ) {
                                Text(
                                    text = stringResource(id = R.string.matisse_retry),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        }
                    }
                }

                MatisseState.PermissionRequesting -> {

                }

                MatisseState.ImagesEmpty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.matisse_load_empty),
                                Modifier
                                    .wrapContentSize()
                                    .padding(start = 12.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = innerPadding),
                        state = lazyGridState,
                        columns = GridCells.Fixed(count = spanCount),
                        contentPadding = PaddingValues(
                            bottom = 60.dp
                        )
                    ) {
                        if (supportCapture) {
                            item(key = "MatisseCapture",
                                contentType = "MatisseCapture",
                                content = {
                                    CaptureItem(onClick = onRequestTakePicture)
                                })
                        }
                        if (images != null) {
                            items(count = images.itemCount) { index ->
                                val media = images[index]!!
                                val i = selectedMediaResources.indexOf(element = media)
                                val isSelected = i > -1
                                val enabled =
                                    isSelected || selectedMediaResources.size < maxSelectable
                                AlbumItem(
                                    media = media,
                                    isSelected = isSelected,
                                    enabled = enabled,
                                    position = if (isSelected) {
                                        (i + 1).toString()
                                    } else {
                                        ""
                                    },
                                    itemWidthPx = imageItemWidthPx,
                                    isReachMaxItem = selectedMediaResources.size >= maxSelectable,
                                    onClickMedia = {
                                        viewModel.onClickMedia(mediaResource = media)
                                    },
                                    onClickCheckBox = {
                                        viewModel.onMediaCheckChanged(mediaResource = media)
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumItem(
    media: MediaResource,
    isSelected: Boolean,
    enabled: Boolean,
    position: String,
    itemWidthPx: Int,
    isReachMaxItem: Boolean,
    onClickMedia: () -> Unit,
    onClickCheckBox: () -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(all = 1.dp)
            .aspectRatio(ratio = 1f)
            .clip(shape = RoundedCornerShape(size = 2.dp))
            .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            .clickable(onClick = onClickMedia)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    other = if (isSelected) {
                        Modifier.drawMask(
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.3f
                            )
                        )
                    } else if (isReachMaxItem) {
                        Modifier.alpha(0.5f)
                    } else {
                        Modifier
                    }
                ),
            model = ImageRequest
                .Builder(context = context)
                .data(data = media.uri)
                .size(size = itemWidthPx)
                .crossfade(enable = false)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = media.displayName
        )
        MatisseCheckbox(
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(all = 3.dp),
            text = position,
            checked = isSelected,
            enabled = enabled,
            onClick = onClickCheckBox
        )
    }
}

@Composable
private fun CaptureItem(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(all = 1.dp)
            .aspectRatio(ratio = 1f)
            .clip(shape = RoundedCornerShape(size = 2.dp))
            .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
            .clickable(onClick = onClick)
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize(fraction = 0.5f)
                .align(alignment = Alignment.Center),
            imageVector = Icons.Filled.PhotoCamera,
            tint = MaterialTheme.colorScheme.background,
            contentDescription = "Capture"
        )
    }
}

private fun Modifier.drawBorder(color: Color): Modifier {
    return drawWithCache {
        val lineWidth = 3.dp.toPx()
        val topLeftPoint = lineWidth / 2f
        val rectSize = size.width - lineWidth
        onDrawWithContent {
            drawContent()
            drawRect(
                color = color,
                topLeft = Offset(topLeftPoint, topLeftPoint),
                size = Size(width = rectSize, height = rectSize),
                style = Stroke(width = lineWidth)
            )
        }
    }
}


private fun Modifier.drawMask(color: Color): Modifier {
    return drawWithCache {
        onDrawWithContent {
            drawContent()
            drawRect(
                color = color,
                topLeft = Offset(0f, 0f),
                size = size,
                style = Fill
            )
        }
    }
}