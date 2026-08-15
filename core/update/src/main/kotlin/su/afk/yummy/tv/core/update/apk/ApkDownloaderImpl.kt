package su.afk.yummy.tv.core.update.apk

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.afk.yummy.tv.core.update.api.ApkDownloader
import java.io.File
import javax.inject.Inject

internal class ApkDownloaderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val httpClient: HttpClient,
) : ApkDownloader {

    override suspend fun download(url: String, onProgress: (Float) -> Unit): File =
        withContext(Dispatchers.IO) {
            val outputFile = File(context.cacheDir, "update.apk")

            httpClient.prepareGet(url).execute { response ->
                val contentLength = response.contentLength() ?: -1L
                var bytesRead = 0L
                val buffer = ByteArray(8192)
                val channel = response.bodyAsChannel()

                outputFile.outputStream().use { output ->
                    while (!channel.isClosedForRead) {
                        val read = channel.readAvailable(buffer)
                        if (read <= 0) continue
                        output.write(buffer, 0, read)
                        bytesRead += read
                        if (contentLength > 0) {
                            onProgress(bytesRead.toFloat() / contentLength.toFloat())
                        }
                    }
                }
            }

            outputFile
        }
}