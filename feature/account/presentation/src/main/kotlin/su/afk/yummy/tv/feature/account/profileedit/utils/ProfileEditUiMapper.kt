package su.afk.yummy.tv.feature.account.profileedit.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal fun Long.toIsoDate(): String = if (this <= 0L) "" else
    Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).toLocalDate()
        .format(DateTimeFormatter.ISO_LOCAL_DATE)
