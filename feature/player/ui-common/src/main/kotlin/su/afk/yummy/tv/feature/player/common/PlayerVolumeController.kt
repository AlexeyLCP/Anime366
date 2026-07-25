package su.afk.yummy.tv.feature.player.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import su.afk.yummy.tv.core.preferences.settings.SettingsStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Внутренняя («продвинутая») громкость плеера — множитель 0.0–1.0, который UI применяет
 * напрямую к [androidx.media3.common.Player.setVolume]. В отличие от
 * [PlayerSystemVolumeController] не трогает системную громкость Android и даёт плавный шаг 1%.
 *
 * Синглтон на весь процесс: источник истины желаемого уровня. Уровень восстанавливается из
 * DataStore при старте и сохраняется обратно (с дебаунсом), чтобы переживать перезапуск
 * приложения, смену серий и переподключение плеера.
 */
@OptIn(FlowPreview::class)
@Singleton
class PlayerVolumeController @Inject constructor(
    settingsStore: SettingsStore,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _volume = MutableStateFlow(DEFAULT_VOLUME)

    /** Текущий уровень как множитель 0f..1f для передачи в `player.volume`. */
    val volume: StateFlow<Float> = _volume.asStateFlow()

    init {
        // Восстанавливаем сохранённый уровень при старте.
        scope.launch {
            val storedPercent = settingsStore.advancedPlayerVolumePercent.first()
            _volume.value = (storedPercent / 100f).coerceIn(MIN_VOLUME, MAX_VOLUME)
        }
        // Сохраняем изменения уровня (с дебаунсом, чтобы не писать на каждый шаг ползунка).
        scope.launch {
            _volume
                .map { (it * 100f).roundToInt() }
                .distinctUntilChanged()
                .drop(1)
                .debounce(PERSIST_DEBOUNCE_MS)
                .collect { percent -> settingsStore.setAdvancedPlayerVolumePercent(percent) }
        }
    }

    fun currentPercent(): Int = (_volume.value * 100f).roundToInt()

    fun setPercent(percent: Int) = setVolume(percent / 100f)

    /** Сдвиг на [deltaPercent] процентов, возвращает итоговый уровень в процентах. */
    fun stepBy(deltaPercent: Int): Int {
        setPercent(currentPercent() + deltaPercent)
        return currentPercent()
    }

    private fun setVolume(value: Float) {
        _volume.value = value.coerceIn(MIN_VOLUME, MAX_VOLUME)
    }

    companion object {
        const val MIN_VOLUME = 0f
        const val MAX_VOLUME = 1f
        const val DEFAULT_VOLUME = 1f
        const val MAX_PERCENT = 100
        private const val PERSIST_DEBOUNCE_MS = 500L
    }
}
