package su.afk.yummy.tv.core.preferences.settings.datastore

import androidx.datastore.preferences.core.Preferences

/**
 * Читает enum [T], сохранённый по имени константы (`value.name`), с фолбэком на [default],
 * если ключа нет или строку не удалось распарсить (например, константу переименовали/удалили
 * в новой версии).
 *
 * Почему `inline` + `reified`: `enumValueOf<T>(name)` из stdlib резолвит константу по строке,
 * и ему нужен конкретный класс enum в рантайме. Обычный дженерик [T] стирается при компиляции
 * (type erasure), поэтому тип помечен `reified`, а функция — `inline`: на каждом месте вызова
 * компилятор подставляет тело с уже известным типом (`enumValueOf<AppTheme>(...)` и т.д.).
 * Альтернатива без `reified` — тащить `KClass`/`Array<T>` параметром на каждый вызов.
 */
internal inline fun <reified T : Enum<T>> Preferences.enum(
    key: Preferences.Key<String>,
    default: T,
): T = this[key]?.let { name -> runCatching { enumValueOf<T>(name) }.getOrNull() } ?: default
