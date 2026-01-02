package github.leavesczy.matisse.samples

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatDelegate
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.network.okhttp.OkHttpNetworkFetcherFactory

/**
 * @Author: leavesCZY
 * @Date: 2022/5/29 21:10
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MatisseApplication : Application(), SingletonImageLoader.Factory {

    private val Context.isSystemInDarkTheme: Boolean
        get() = resources.configuration.isSystemInDarkTheme

    private val Configuration.isSystemInDarkTheme: Boolean
        get() = uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(
            if (isSystemInDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }


    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context = context)
            .components {
                if (SDK_INT >= 28) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }.build()
    }

}