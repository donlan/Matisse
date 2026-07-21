package github.leavesczy.matisse

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.IntentCompat
import github.leavesczy.matisse.internal.MatisseActivity

/**
 * @Author: CZY
 * @Date: 2022/6/2 15:30
 * @Desc:
 */
class MatisseContract : ActivityResultContract<Matisse, List<MediaResource>>() {

    companion object {

        private const val keyRequest = "keyRequest"

        private const val keyResult = "keyResult"

        internal fun getRequest(intent: Intent): Matisse {
            return IntentCompat.getParcelableExtra(intent, keyRequest, Matisse::class.java)!!
        }

        internal fun buildResult(selectedMediaResources: List<MediaResource>): Intent {
            val data = Intent()
            val resources = arrayListOf<Parcelable>().apply {
                addAll(selectedMediaResources)
            }
            data.putParcelableArrayListExtra(keyResult, resources)
            return data
        }

    }

    override fun createIntent(context: Context, input: Matisse): Intent {
        val intent = Intent(context, MatisseActivity::class.java)
        intent.putExtra(keyRequest, input)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<MediaResource> {
        return if (resultCode == Activity.RESULT_OK && intent != null) {
            IntentCompat.getParcelableArrayListExtra(
                intent,
                keyResult,
                MediaResource::class.java
            ) ?: emptyList()
        } else {
            emptyList()
        }
    }

}
