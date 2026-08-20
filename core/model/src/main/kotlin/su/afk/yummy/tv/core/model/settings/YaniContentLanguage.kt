package su.afk.yummy.tv.core.model.settings

enum class YaniContentLanguage(val apiCode: String) {
    RUSSIAN("ru"),
    ENGLISH("en"),
    UKRAINIAN("uk"),
    ;

    companion object {
        val DEFAULT: YaniContentLanguage = RUSSIAN

        fun fromPreferenceValue(value: String?): YaniContentLanguage? {
            if (value.isNullOrBlank()) return null
            return entries.firstOrNull { it.name == value }
                ?: entries.firstOrNull { it.apiCode == value }
        }
    }
}

fun String.withYaniContentLanguage(language: YaniContentLanguage): String =
    "${this}_lang_${language.apiCode}"
