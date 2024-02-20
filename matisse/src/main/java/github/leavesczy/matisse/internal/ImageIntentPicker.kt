package github.leavesczy.matisse.internal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
private fun isSystemPickerEnable(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

class PickImages(@androidx.annotation.IntRange(from = 1, to = 9) private val maxPickNum: Int = 1) :
    ActivityResultContract<Unit, List<Uri>?>() {
    @CallSuper
    override fun createIntent(context: Context, input: Unit): Intent {
        if (isSystemPickerEnable()) {
            return Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                if (maxPickNum > 1) {
                    putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxPickNum)
                }
                type = "image/*"
            }
        }
        return Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("image/*")
    }


    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri>? {
        if (isSystemPickerEnable()) {
            return intent.takeIf { resultCode == Activity.RESULT_OK }?.run {
                val images = ArrayList<Uri>()
                if (maxPickNum == 1) {
                    data?.let { images.add(it) }
                } else {
                    clipData?.let {
                        for (i in 0 until it.itemCount) {
                            images.add(it.getItemAt(i).uri)
                        }
                    }
                }
                images
            }
        }
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.data?.run { listOf(this) }
    }
}