package su.afk.yummy.tv.core.analytics

/** Короткое имя класса исключения для аналитики (или "unknown", если недоступно). */
fun Throwable.analyticsType(): String =
    this::class.java.simpleName.takeIf { it.isNotBlank() } ?: "unknown"
