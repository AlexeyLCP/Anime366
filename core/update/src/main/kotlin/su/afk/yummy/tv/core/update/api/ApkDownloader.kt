package su.afk.yummy.tv.core.update.api

import java.io.File

interface ApkDownloader {
    suspend fun download(url: String, onProgress: (Float) -> Unit): File
}
