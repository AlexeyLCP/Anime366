package su.afk.yummy.tv.data.messages.repository

import su.afk.yummy.tv.data.messages.dto.YaniDialogDto
import su.afk.yummy.tv.data.messages.dto.YaniMessageDto
import su.afk.yummy.tv.data.messages.dto.YaniMessageHistoryEntryDto
import su.afk.yummy.tv.data.messages.mapper.domain
import su.afk.yummy.tv.data.messages.network.YaniMessagesApi
import su.afk.yummy.tv.domain.messages.repository.MessagesRepository
import javax.inject.Inject

class YaniMessagesRepository @Inject constructor(
    private val api: YaniMessagesApi,
) : MessagesRepository {
    override suspend fun dialogs(limit: Int, offset: Int, needUserId: Int?) =
        api.dialogs(limit, offset, needUserId).response.dialogs
            .filter { it.userId > 0 }
            .map(YaniDialogDto::domain)

    override suspend fun messages(userId: Int, limit: Int, startFrom: Int) =
        api.messages(userId, limit, startFrom).response
            .filter { it.id > 0 }
            .map(YaniMessageDto::domain)

    override suspend fun sendMessage(userId: Int, text: String, answerMessageId: Int) =
        api.sendMessage(userId, text, answerMessageId).response.domain()

    override suspend fun markRead(userId: Int) = api.markRead(userId).response.ok

    override suspend fun editMessage(messageId: Int, text: String) =
        api.editMessage(messageId, text).response.domain()

    override suspend fun deleteMessage(messageId: Int) =
        api.deleteMessage(messageId).response.domain()

    override suspend fun restoreMessage(messageId: Int) =
        api.restoreMessage(messageId).response.domain()

    override suspend fun messageHistory(messageId: Int) =
        api.messageHistory(messageId).response.map(YaniMessageHistoryEntryDto::domain)

    override suspend fun claimMessage(messageId: Int) = api.claimMessage(messageId).response

    override suspend fun setUserBanned(userId: Int, banned: Boolean) =
        if (banned) api.banUser(userId).response else api.unbanUser(userId).response
}
