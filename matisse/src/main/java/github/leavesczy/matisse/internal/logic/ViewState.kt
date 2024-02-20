package github.leavesczy.matisse.internal.logic

import android.net.Uri
import androidx.paging.PagingData
import github.leavesczy.matisse.Matisse
import github.leavesczy.matisse.MediaResource
import kotlinx.coroutines.flow.Flow

/**
 * @Author: leavesCZY
 * @Date: 2022/5/30 23:24
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal enum class MatisseState {
    PermissionRequesting,
    PermissionDenied,
    ImagesLoading,
    ImagesLoaded,
    ImagesEmpty,
    ImagesError,
}

internal data class MediaBucket(
    val id: String,
    val displayName: String,
    val displayIcon: Uri,
    val resources: List<MediaResource>,
    val supportCapture: Boolean,
    val pageSource: Flow<PagingData<MediaResource>>? = null
)

internal data class MatisseViewState(
    val matisse: Matisse,
    val state: MatisseState,
    val allBucket: List<MediaBucket>,
    val selectedBucket: MediaBucket,
    val selectedResources: List<MediaResource>,
    val pageSource: Flow<PagingData<MediaResource>>? = null
)

internal data class MatissePreviewButtonViewState(
    val text: String,
    val clickable: Boolean,
    val onClick: () -> Unit
)

internal data class MatisseSureButtonViewState(
    val text: String,
    val clickable: Boolean,
    val onClick: () -> Unit
)

internal data class MatissePreviewViewState(
    val matisse: Matisse,
    val visible: Boolean,
    val initialPage: Int,
    val previewResources: List<MediaResource>,
    val selectedResources: List<MediaResource>,
)

internal sealed class MatisseAction {

    class OnSure(val resources: List<MediaResource>) : MatisseAction()

}