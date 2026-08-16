package su.afk.yummy.tv.core.preferences.settings.model

/** Размер субтитров в процентах от дефолтного размера Media3. */
enum class PlayerSubtitleTextSize(val percent: Int) {
    PERCENT_75(75),
    PERCENT_100(100),
    PERCENT_125(125),
    PERCENT_150(150),
    PERCENT_200(200);

    val scale: Float get() = percent / 100f
}

/**
 * Цвета заданы как ARGB-int, а не через `android.graphics.Color`: модуль настроек общий
 * и не должен тянуть Android-типы в модель.
 */
enum class PlayerSubtitleTextColor(val argb: Int) {
    WHITE(0xFFFFFFFF.toInt()),
    YELLOW(0xFFFFEB3B.toInt()),
    CYAN(0xFF80DEEA.toInt()),
    GREEN(0xFF9CCC65.toInt()),
}

enum class PlayerSubtitleBackground(val argb: Int) {
    NONE(0x00000000),
    TRANSLUCENT(0x99000000.toInt()),
    SOLID(0xFF000000.toInt()),
}

/** Оформление субтитров: применяется глобально ко всем источникам, реально сабы отдаёт Alloha. */
data class PlayerSubtitleStyleSettings(
    val textSize: PlayerSubtitleTextSize = PlayerSubtitleTextSize.PERCENT_100,
    val textColor: PlayerSubtitleTextColor = PlayerSubtitleTextColor.WHITE,
    val background: PlayerSubtitleBackground = PlayerSubtitleBackground.TRANSLUCENT,
)
