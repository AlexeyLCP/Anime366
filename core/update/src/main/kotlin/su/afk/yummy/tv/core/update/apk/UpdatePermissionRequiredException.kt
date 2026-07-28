package su.afk.yummy.tv.core.update.apk

/**
 * Установка обновления требует, чтобы пользователь сначала разрешил установку приложений
 * из неизвестных источников. Это не сбой приложения, а ожидаемый шаг UX (код сам открывает
 * системные настройки), поэтому такой случай попадает в аналитику как обычное событие
 * `update_error`, но НЕ репортится как non-fatal ошибка в AppMetrica.
 */
class UpdatePermissionRequiredException(message: String) : IllegalStateException(message)
