package su.afk.yummy.tv.feature.messages.mobile.utils

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import java.util.Date

/**
 * Форматирует время сообщения: сегодняшние — только время («9:24»), более старые — дата и время
 * («31.07 9:24»). Время берётся через [DateFormat.getTimeFormat] — уважает 12/24-часовой формат
 * устройства и локаль.
 */
internal fun Long.formatMessageDate(context: Context): String {
    val millis = this * 1_000L
    val time = DateFormat.getTimeFormat(context).format(Date(millis))
    if (DateUtils.isToday(millis)) return time
    val date = DateUtils.formatDateTime(
        context,
        millis,
        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_NO_YEAR,
    )
    return "$date $time"
}
