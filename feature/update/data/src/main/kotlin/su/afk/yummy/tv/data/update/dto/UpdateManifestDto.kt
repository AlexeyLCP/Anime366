package su.afk.yummy.tv.data.update.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class UpdateManifestDto(
    val version: String,
    val apkUrl: String,
    val changelog: String = "",
)
