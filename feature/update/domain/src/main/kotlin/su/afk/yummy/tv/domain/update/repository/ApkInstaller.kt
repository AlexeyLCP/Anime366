package su.afk.yummy.tv.domain.update.repository

import java.io.File

interface ApkInstaller {
    suspend fun install(apkFile: File)
}
