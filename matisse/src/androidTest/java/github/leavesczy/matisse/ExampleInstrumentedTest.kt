package github.leavesczy.matisse

import android.app.Activity
import android.content.Intent
import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("github.leavesczy.matisse.test", appContext.packageName)
    }

    @Test
    fun requestSurvivesIntentParcelRoundTrip() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val request = Matisse(maxSelectable = 3)
        val intent = MatisseContract().createIntent(context, request).parcelRoundTrip()

        assertEquals(request, MatisseContract.getRequest(intent))
    }

    @Test
    fun resultSurvivesIntentParcelRoundTrip() {
        val resources = listOf(
            MediaResource(
                id = 1,
                uri = android.net.Uri.parse("content://matisse/test"),
                displayName = "test.jpg",
                mimeType = "image/jpeg",
                width = 100,
                height = 200,
                orientation = 0,
                size = 300,
                path = "/test.jpg",
                bucketId = "test",
                bucketDisplayName = "Test"
            )
        )
        val intent = MatisseContract.buildResult(resources).parcelRoundTrip()

        assertEquals(resources, MatisseContract().parseResult(Activity.RESULT_OK, intent))
    }

    private fun Intent.parcelRoundTrip(): Intent {
        val parcel = Parcel.obtain()
        return try {
            writeToParcel(parcel, 0)
            parcel.setDataPosition(0)
            Intent.CREATOR.createFromParcel(parcel)
        } finally {
            parcel.recycle()
        }
    }
}
