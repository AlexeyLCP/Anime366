package su.afk.yummy.tv.core.utils.formatting

import android.text.format.DateUtils

fun Long.formatRelativeDateTime(): String =
    DateUtils.getRelativeTimeSpanString(
        this * 1_000L,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
    ).toString()
