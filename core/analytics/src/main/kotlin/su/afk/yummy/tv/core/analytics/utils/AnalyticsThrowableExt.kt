package su.afk.yummy.tv.core.analytics.utils

import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/** Короткое имя класса исключения для аналитики (или "unknown", если недоступно). */
fun Throwable.analyticsType(): String =
    this::class.java.simpleName.takeIf { it.isNotBlank() } ?: "unknown"

/**
 * Стоит ли репортить исключение в аналитику ошибок.
 *
 * Отмена корутины ([CancellationException], в т.ч. обфусцированный `JobCancellationException`
 * «Job was cancelled») и сетевые/оффлайн-ошибки ([IOException]: нет DNS, обрыв соединения,
 * таймаут) — не баги приложения, а внешние обстоятельства, поэтому шумят в логах впустую.
 * Ср. политику в `ErrorHandlerUseCaseImpl`.
 */
fun Throwable.isReportableError(): Boolean =
    this !is CancellationException && this !is IOException
