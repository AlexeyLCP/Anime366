package su.afk.yummy.tv.data.update.mapper

import su.afk.yummy.tv.data.update.dto.GitHubReleaseDto
import su.afk.yummy.tv.domain.update.model.AppRelease

/**
 * Релиз без приложенного APK установить нельзя, поэтому такой релиз маппится в null —
 * для вызывающего это «обновления нет».
 */
internal fun GitHubReleaseDto.toDomain(): AppRelease? {
    val apkUrl = assets.firstOrNull { it.browserDownloadUrl.endsWith(".apk", ignoreCase = true) }
        ?.browserDownloadUrl ?: return null
    return AppRelease(
        version = tagName.trimStart('v'),
        changelog = body.orEmpty(),
        apkUrl = apkUrl,
    )
}
