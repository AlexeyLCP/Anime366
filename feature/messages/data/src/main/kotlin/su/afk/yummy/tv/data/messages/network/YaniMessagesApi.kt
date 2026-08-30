package su.afk.yummy.tv.data.messages.network

import su.afk.yummy.tv.data.messages.dto.YaniBooleanResponseDto
import su.afk.yummy.tv.data.messages.dto.YaniDialogsResponseDto
import su.afk.yummy.tv.data.messages.dto.YaniMessageHistoryResponseDto
import su.afk.yummy.tv.data.messages.dto.YaniMessageResponseDto
import su.afk.yummy.tv.data.messages.dto.YaniMessagesResponseDto
import su.afk.yummy.tv.data.messages.dto.YaniReadMessagesResponseDto
import javax.inject.Inject

class YaniMessagesApi @Inject constructor() {
    suspend fun dialogs(limit: Int, offset: Int, needUserId: Int?): YaniDialogsResponseDto =
        YaniDialogsResponseDto()

    suspend fun messages(userId: Int, limit: Int, startFrom: Int): YaniMessagesResponseDto =
        YaniMessagesResponseDto()

    suspend fun sendMessage(userId: Int, text: String, answerMessageId: Int = 0): YaniMessageResponseDto =
        YaniMessageResponseDto()

    suspend fun markRead(userId: Int): YaniReadMessagesResponseDto = YaniReadMessagesResponseDto()

    suspend fun editMessage(messageId: Int, text: String): YaniMessageResponseDto = YaniMessageResponseDto()

    suspend fun deleteMessage(messageId: Int): YaniMessageResponseDto = YaniMessageResponseDto()

    suspend fun restoreMessage(messageId: Int): YaniMessageResponseDto = YaniMessageResponseDto()

    suspend fun messageHistory(messageId: Int): YaniMessageHistoryResponseDto =
        YaniMessageHistoryResponseDto()

    suspend fun claimMessage(messageId: Int): YaniBooleanResponseDto = YaniBooleanResponseDto()

    suspend fun banUser(userId: Int): YaniBooleanResponseDto = YaniBooleanResponseDto()

    suspend fun unbanUser(userId: Int): YaniBooleanResponseDto = YaniBooleanResponseDto()
}
