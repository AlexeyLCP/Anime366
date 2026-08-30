package su.afk.yummy.tv.feature.details.utils

fun String.dubbingKind(): String = substringBefore(" · ").ifBlank { this }

fun String.dubbingTeam(): String = substringAfter(" · ", missingDelimiterValue = this)
