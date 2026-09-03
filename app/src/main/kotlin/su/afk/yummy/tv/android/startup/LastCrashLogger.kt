package su.afk.yummy.tv.android.startup

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import su.afk.yummy.tv.BuildConfig
import java.io.File

object LastCrashLogger {
    private const val FILE_NAME = "last_crash.txt"
    private const val MAX_CHARS = 4000

    fun install(context: Context) {
        val app = context.applicationContext
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            runCatching { write(app, thread, error) }
            previous?.uncaughtException(thread, error)
        }
    }

    fun consume(context: Context): String? {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.isFile) return null
        val text = runCatching { file.readText() }.getOrNull()
        file.delete()
        return text?.takeIf { it.isNotBlank() }
    }

    private fun write(context: Context, thread: Thread, error: Throwable) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val mem = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(mem)
        File(context.filesDir, FILE_NAME).writeText(
            buildString {
                appendLine(BuildConfig.VERSION_NAME)
                appendLine("abi=${Build.SUPPORTED_ABIS.joinToString()}")
                appendLine("sdk=${Build.VERSION.SDK_INT}")
                appendLine("device=${Build.MANUFACTURER} ${Build.MODEL}")
                appendLine("lowRam=${am?.isLowRamDevice} memClass=${am?.memoryClass}")
                appendLine("totalMem=${mem.totalMem} availMem=${mem.availMem}")
                appendLine("thread=${thread.name}")
                appendLine(error.stackTraceToString())
            }.take(MAX_CHARS),
        )
    }
}
