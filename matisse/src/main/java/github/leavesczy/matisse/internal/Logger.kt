package github.leavesczy.matisse.internal

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize

/**
 * @author: liangguidong
 * @date: 1/24/26 PM4:22
 * @lastModifyUser: liangguidong
 * @lastModifyDate: 1/24/26 PM4:22
 * @description:
 */
interface Logger : Parcelable {

    fun d(tag: String, content: () -> String)

    fun e(tag: String, t: Throwable)

    fun e(tag: String, content: () -> String)

    fun w(tag: String, content: () -> String)

    fun i(tag: String, content: () -> String)

    companion object {
        val DEFAULT: Logger = LoggerImpl()
    }
}

@Parcelize
private class LoggerImpl : Logger {

    override fun d(tag: String, content: () -> String) {
        Log.d(tag, content())
    }

    override fun e(tag: String, t: Throwable) {
        t.printStackTrace()
        Log.e(tag, t.message ?: "")
    }

    override fun e(tag: String, content: () -> String) {
        Log.e(tag, content())
    }

    override fun w(tag: String, content: () -> String) {
        Log.w(tag, content())
    }

    override fun i(tag: String, content: () -> String) {
        Log.i(tag, content())
    }
}