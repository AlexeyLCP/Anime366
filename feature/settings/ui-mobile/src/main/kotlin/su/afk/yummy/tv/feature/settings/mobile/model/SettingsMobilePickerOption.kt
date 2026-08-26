package su.afk.yummy.tv.feature.settings.mobile.model

import androidx.compose.ui.graphics.Color

internal data class SettingsMobilePickerOption<T>(
    val value: T,
    val label: String,
    val hint: String = "",
    /** Переопределяет цвет текста подписи — например, чтобы показать реальный цвет субтитров. */
    val labelColor: Color? = null,
)
