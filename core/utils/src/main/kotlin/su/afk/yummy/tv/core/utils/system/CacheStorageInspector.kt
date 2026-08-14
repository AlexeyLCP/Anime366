package su.afk.yummy.tv.core.utils.system

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/** Размер одной верхнеуровневой папки (или файла) кэша приложения. */
data class CacheStorageEntry(
    /** Имя папки/файла (например `image_cache`); по нему UI подбирает подпись. */
    val id: String,
    val sizeBytes: Long,
)

/** Снимок дискового кэша приложения: записи по убыванию размера и суммарный размер. */
data class CacheStorageReport(
    val entries: List<CacheStorageEntry>,
    val totalBytes: Long,
)

/**
 * Считает размеры верхнеуровневых записей в кэш-каталогах приложения. Диагностический
 * инструмент: сканирует всё, что реально лежит на диске, а не заранее известный список,
 * поэтому «раздувшаяся» папка будет видна, даже если её имя нам неизвестно.
 */
@Singleton
class CacheStorageInspector @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun inspect(): CacheStorageReport = withContext(Dispatchers.IO) {
        val roots = buildList {
            add(context.cacheDir)
            add(context.filesDir)
            context.externalCacheDir?.let(::add)
            // Каталог `databases` лежит рядом с БД, самого файла может ещё не быть.
            context.getDatabasePath("placeholder").parentFile?.let(::add)
        }

        val entries = roots
            .asSequence()
            .filterNotNull()
            .flatMap { it.listFiles()?.asSequence() ?: emptySequence() }
            // Дедупликация на случай пересечения каталогов.
            .distinctBy { runCatching { it.canonicalPath }.getOrDefault(it.absolutePath) }
            .map { CacheStorageEntry(id = it.name, sizeBytes = it.sizeRecursive()) }
            .filter { it.sizeBytes > 0 }
            .sortedByDescending { it.sizeBytes }
            .toList()

        CacheStorageReport(
            entries = entries,
            totalBytes = entries.sumOf { it.sizeBytes },
        )
    }

    private fun File.sizeRecursive(): Long =
        if (isFile) {
            length()
        } else {
            walkBottomUp().filter { it.isFile }.sumOf { it.length() }
        }
}
