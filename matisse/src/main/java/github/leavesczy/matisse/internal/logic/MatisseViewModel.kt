package github.leavesczy.matisse.internal.logic

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import github.leavesczy.matisse.Matisse
import github.leavesczy.matisse.MediaResource
import github.leavesczy.matisse.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2022/6/1 19:19
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MatisseViewModel(
    application: Application,
    private val matisse: Matisse
) : AndroidViewModel(application) {

    companion object {
        const val DEFAULT_BUCKET_ID = "&__defaultBucketId__&"
    }

    private fun defaultBucket(): MediaBucket {
        return MediaBucket(
            id = DEFAULT_BUCKET_ID,
            displayName = getString(R.string.matisse_default_bucket_name),
            displayIcon = Uri.EMPTY,
            resources = emptyList(),
            supportCapture = matisse.captureStrategy.isEnabled(),
            pageSource = Pager(PagingConfig(20)) {
                ImagesSource(
                    context.contentResolver,
                    null
                )
            }.flow
        )
    }

    private val buckets by lazy {
        ArrayList<MediaBucket>().apply {
            add(defaultBucket())
        }
    }

    private val context: Context
        get() = getApplication()


    private val permissionRequestingViewState = kotlin.run {
        MatisseViewState(
            matisse = matisse,
            state = MatisseState.PermissionRequesting,
            selectedResources = emptyList(),
            allBucket = buckets.toList(),
            selectedBucket = buckets.first(),
            pageSource = buckets.first().pageSource
        )
    }


    private val permissionDeniedViewState =
        permissionRequestingViewState.copy(state = MatisseState.PermissionDenied)

    private val imageLoadingViewState =
        permissionRequestingViewState.copy(state = MatisseState.ImagesLoading)

    private val imageEmptyViewState =
        permissionRequestingViewState.copy(state = MatisseState.ImagesEmpty)

    var matisseViewState by mutableStateOf(value = permissionRequestingViewState)
        private set

    var previewButtonViewState by mutableStateOf(value = buildPreviewButtonViewState())
        private set

    var sureButtonViewState by mutableStateOf(value = buildSureButtonViewState())
        private set

    var matissePreviewViewState by mutableStateOf(
        value = MatissePreviewViewState(
            matisse = matisse,
            visible = false,
            initialPage = 0,
            selectedResources = emptyList(),
            previewResources = emptyList()
        )
    )
        private set


    private val _matisseAction = MutableSharedFlow<MatisseAction>()

    val matisseAction: SharedFlow<MatisseAction> = _matisseAction

    private var tempImageUriForTakePicture: Uri? = null

    fun onRequestReadImagesPermission() {
        matisseViewState = permissionRequestingViewState
    }

    fun onRequestReadImagesPermissionResult(granted: Boolean) {
        viewModelScope.launch(context = Dispatchers.Main.immediate) {
            if (granted) {
                loadResources()
            } else {
                matisseViewState = permissionDeniedViewState
                showToast(message = getString(R.string.matisse_on_read_external_storage_permission_denied))
            }
            previewButtonViewState = buildPreviewButtonViewState()
            sureButtonViewState = buildSureButtonViewState()
            dismissPreviewPage()
        }
    }

    private suspend fun loadResources() {
        matisseViewState = imageLoadingViewState.copy()
        kotlin.runCatching {
            buckets.clear()
            buckets.add(defaultBucket())
            MediaProvider.loadAllBuckets(context).forEach {
                buckets.add(it.copy(pageSource = Pager(PagingConfig(20)) {
                    ImagesSource(
                        context.contentResolver,
                        it.id
                    )
                }.flow))
            }
            matisseViewState = imageLoadingViewState.copy(
                state = MatisseState.ImagesLoaded,
                allBucket = buckets.toList()
            )
            onSelectBucket(buckets.first())
        }.onFailure {
            it.printStackTrace()
            matisseViewState = imageLoadingViewState.copy(
                state = MatisseState.ImagesError,
            )
        }
    }

    fun onSelectBucket(bucket: MediaBucket) {
        matisseViewState =
            matisseViewState.copy(
                selectedBucket = bucket,
                pageSource = bucket.pageSource ?: Pager(PagingConfig(20)) {
                    ImagesSource(
                        context.contentResolver,
                        bucket.id.takeIf { it != DEFAULT_BUCKET_ID })
                }.flow
            )
    }

    fun onMediaCheckChanged(mediaResource: MediaResource) {
        val selectedResources = matisseViewState.selectedResources.toMutableList()
        val alreadySelected = selectedResources.contains(element = mediaResource)
        if (alreadySelected) {
            selectedResources.remove(element = mediaResource)
        } else {
            val maxSelectable = matisse.maxSelectable
            if (maxSelectable == 1) {
                selectedResources.clear()
                selectedResources.add(element = mediaResource)
            } else if (selectedResources.size >= maxSelectable) {
                showToast(
                    message = String.format(
                        getString(R.string.matisse_limit_the_number_of_pictures),
                        matisse.maxSelectable
                    )
                )
                return
            } else {
                selectedResources.add(element = mediaResource)
            }
        }
        matisseViewState = matisseViewState.copy(selectedResources = selectedResources)
        previewButtonViewState = buildPreviewButtonViewState()
        sureButtonViewState = buildSureButtonViewState()
        if (matissePreviewViewState.visible) {
            matissePreviewViewState =
                matissePreviewViewState.copy(selectedResources = selectedResources)
        }
        if (!alreadySelected && matisse.maxSelectable == 1 && matisse.singleConfirmDirectly) {
            onClickSureButton()
            return
        }
    }

    fun onClickMedia(mediaResource: MediaResource) {
        val previewResources = matisseViewState.selectedBucket.resources
        val selectedResources = matisseViewState.selectedResources
        val initialPage = previewResources.indexOf(element = mediaResource)
        matissePreviewViewState = if (initialPage < 0) {
            matissePreviewViewState.copy(
                visible = true,
                initialPage = 0,
                selectedResources = selectedResources,
                previewResources = listOf(mediaResource),
            )
        } else {
            matissePreviewViewState.copy(
                visible = true,
                initialPage = initialPage,
                selectedResources = selectedResources,
                previewResources = previewResources,
            )
        }
    }

    private fun onClickPreviewButton() {
        val selectedResources = matisseViewState.selectedResources.toList()
        if (selectedResources.isNotEmpty()) {
            matissePreviewViewState = matissePreviewViewState.copy(
                visible = true,
                initialPage = 0,
                selectedResources = selectedResources,
                previewResources = selectedResources,
            )
        }
    }

    private fun onClickSureButton() {
        viewModelScope.launch(context = Dispatchers.Main.immediate) {
            onSure(resources = matisseViewState.selectedResources)
        }
    }

    suspend fun createImageUriForTakePicture(): Uri? {
        tempImageUriForTakePicture =
            matisse.captureStrategy.createImageUri(context = context)
        return tempImageUriForTakePicture
    }

    fun onTakeFromFolder(uri: Uri) {
        viewModelScope.launch {
            val resource =
                matisse.captureStrategy.loadResource(context = context, imageUri = uri)
            if (resource != null) {
                onSure(resources = listOf(resource))
            }
        }
    }

    fun takePictureResult(successful: Boolean) {
        viewModelScope.launch(context = Dispatchers.Main.immediate) {
            val imageUri = tempImageUriForTakePicture
            if (imageUri != null) {
                tempImageUriForTakePicture = null
                if (successful) {
                    val resource =
                        matisse.captureStrategy.loadResource(context = context, imageUri = imageUri)
                    if (resource != null) {
                        onSure(resources = listOf(resource))
                    }
                } else {
                    matisse.captureStrategy.onTakePictureCanceled(
                        context = context,
                        imageUri = imageUri
                    )
                }
            }
        }
    }

    private suspend fun onSure(resources: List<MediaResource>) {
        _matisseAction.emit(value = MatisseAction.OnSure(resources = resources))
    }

    fun dismissPreviewPage() {
        if (matissePreviewViewState.visible) {
            matissePreviewViewState = matissePreviewViewState.copy(
                visible = false,
                selectedResources = emptyList(),
                previewResources = emptyList()
            )
        }
    }

    private fun buildPreviewButtonViewState(): MatissePreviewButtonViewState {
        val selectedMedia = matisseViewState.selectedResources
        return MatissePreviewButtonViewState(
            text = getString(R.string.matisse_preview),
            clickable = selectedMedia.isNotEmpty(),
            onClick = ::onClickPreviewButton
        )
    }

    private fun buildSureButtonViewState(): MatisseSureButtonViewState {
        val selectedMedia = matisseViewState.selectedResources
        return MatisseSureButtonViewState(
            text = String.format(
                getString(R.string.matisse_sure),
                selectedMedia.size,
                matisse.maxSelectable
            ),
            clickable = selectedMedia.isNotEmpty(),
            onClick = ::onClickSureButton
        )
    }

    private fun getString(@StringRes strId: Int): String {
        return context.getString(strId)
    }

    private fun showToast(message: String) {
        if (message.isNotBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun retryLoad() {

    }

}