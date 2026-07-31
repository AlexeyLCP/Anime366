package su.afk.yummy.tv.domain.messages.model

/**
 * Публичный общий (глобальный) чат. Сервер не возвращает его в списке личных диалогов
 * `/dialogs` — приложение добавляет его само первым пунктом. Сообщения читаются/пишутся
 * по обычному пути `/dialogs/0/messages`.
 */
const val GLOBAL_CHAT_USER_ID = 0

data class DialogSummary(
    val userId: Int,
    val nickname: String,
    val avatarUrl: String?,
    val roles: List<String>,
    val isBanned: Boolean,
    val lastMessage: String,
    val unreadCount: Int,
    val dateSeconds: Long,
    val lastOnlineSeconds: Long,
)
