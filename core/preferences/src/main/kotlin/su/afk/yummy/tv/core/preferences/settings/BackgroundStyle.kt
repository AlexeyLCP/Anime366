package su.afk.yummy.tv.core.preferences.settings

/**
 * Режим оформления интерфейса, выбираемый независимо от палитры-акцента [AppTheme].
 *
 * [SYSTEM] — следовать системной теме устройства; [LIGHT] — светлая схема с тёмным текстом;
 * [DARK] — тёмная схема (как исторически).
 */
enum class BackgroundStyle {
    SYSTEM,
    LIGHT,
    DARK,
}
