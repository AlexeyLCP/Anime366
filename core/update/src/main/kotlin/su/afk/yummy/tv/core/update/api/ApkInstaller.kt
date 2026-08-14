package su.afk.yummy.tv.core.update.api

import java.io.File

interface ApkInstaller {
    suspend fun install(apkFile: File)
}
