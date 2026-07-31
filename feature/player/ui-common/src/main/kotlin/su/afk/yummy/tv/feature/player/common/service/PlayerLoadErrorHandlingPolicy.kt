package su.afk.yummy.tv.feature.player.common.service

import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy

/**
 * Политика обработки ошибок загрузки для «тихого переподключения под буфер».
 *
 * Идея: пока в буфере есть данные впереди, плеер продолжает играть (STATE_READY), а загрузчик молча
 * ретраит упавшую загрузку в фоне — юзер обрыва не видит. Спиннер завязан на STATE_BUFFERING и
 * появится сам, только когда буфер опустеет, а поток так и не поднялся.
 *
 * Для не-Alloha источников ([PlayerPlaybackConfig.silentReconnectEnabled]) продлеваем окно ретраев
 * (дефолтные ~3 попытки сдаются слишком быстро). Заведомо мёртвые HTTP-ответы (протухшая подписанная
 * CVH-ссылка и т.п.) не ретраим — быстрый фатал, чтобы сработал перерезолв источника со свежим URL.
 * Для Alloha/офлайна поведение дефолтное (у Alloha свой fresh-session recovery по onPlayerError).
 */
@UnstableApi
internal class PlayerLoadErrorHandlingPolicy(
    private val playbackConfig: PlayerPlaybackConfig,
) : DefaultLoadErrorHandlingPolicy() {

    override fun getMinimumLoadableRetryCount(dataType: Int): Int =
        if (playbackConfig.silentReconnectEnabled()) {
            SILENT_RETRY_COUNT
        } else {
            super.getMinimumLoadableRetryCount(dataType)
        }

    override fun getRetryDelayMsFor(loadErrorInfo: LoadErrorHandlingPolicy.LoadErrorInfo): Long {
        val exception = loadErrorInfo.exception
        // Мёртвый/протухший ресурс — ретраить один и тот же URL бессмысленно: отдаём фатально,
        // чтобы приложение перерезолвило источник (получило свежую ссылку).
        if (exception is HttpDataSource.InvalidResponseCodeException &&
            exception.responseCode in FATAL_RESPONSE_CODES
        ) {
            return C.TIME_UNSET
        }
        // Сетевые сбои/таймауты/5xx — дефолтный бэкофф; число попыток ограничено
        // getMinimumLoadableRetryCount выше.
        return super.getRetryDelayMsFor(loadErrorInfo)
    }

    private companion object {
        // Достаточно, чтобы окно фоновых ретраев заведомо перекрыло буфер (maxBufferMs = 60 c);
        // после исчерпания — фатал → перерезолв/оверлей, чтобы не зависнуть на спиннере навсегда.
        const val SILENT_RETRY_COUNT = 20

        // 4xx, при которых повтор той же ссылки заведомо не поможет (кроме 408/429 — таймаут/лимит,
        // их оставляем ретраить дефолтно).
        val FATAL_RESPONSE_CODES = setOf(400, 401, 403, 404, 410)
    }
}
