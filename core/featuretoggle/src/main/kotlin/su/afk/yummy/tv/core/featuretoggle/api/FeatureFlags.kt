package su.afk.yummy.tv.core.featuretoggle.api

object FeatureFlags {
    val all: Set<FeatureFlag<*>> = emptySet()

    val minSupportedAppVersion = FeatureFlag.StringFlag(
        key = "app_min_supported_version",
        defaultValue = "",
    )

    /** Версия/идентификатор объявления. Смена значения снова показывает диалог всем. Пустой = выключено. */
    val announcementId = FeatureFlag.StringFlag(
        key = "announcement_id",
        defaultValue = "",
    )

    /** Заголовок объявления. Пустой = заголовок скрыт. */
    val announcementTitle = FeatureFlag.StringFlag(
        key = "announcement_title",
        defaultValue = "",
    )

    /** Текст объявления. Пустой = диалог не показывается. */
    val announcementMessage = FeatureFlag.StringFlag(
        key = "announcement_message",
        defaultValue = "",
    )

    /** Текст кнопки объявления. Пустой = локальный фолбэк. */
    val announcementButton = FeatureFlag.StringFlag(
        key = "announcement_button",
        defaultValue = "",
    )
}
