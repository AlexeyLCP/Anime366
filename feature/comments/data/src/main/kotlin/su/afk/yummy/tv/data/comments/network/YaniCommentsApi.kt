package su.afk.yummy.tv.data.comments.network

import su.afk.yummy.tv.data.comments.dto.YaniBooleanResponseDto
import su.afk.yummy.tv.data.comments.dto.YaniClaimCommentBodyDto
import su.afk.yummy.tv.data.comments.dto.YaniCommentResponseDto
import su.afk.yummy.tv.data.comments.dto.YaniCommentsResponseDto
import su.afk.yummy.tv.data.comments.dto.YaniPatchCommentBodyDto
import su.afk.yummy.tv.data.comments.dto.YaniPostCommentBodyDto
import su.afk.yummy.tv.data.comments.dto.YaniVoteCommentBodyDto
import su.afk.yummy.tv.data.comments.dto.YaniVoteCommentResponseDto

class YaniCommentsApi(
    @Suppress("unused") private val clientProvider: su.afk.yummy.tv.core.network.yani.YaniHttpClientProvider,
) {
    suspend fun getComments(
        targetType: String,
        targetId: Int,
        limit: Int,
        skip: Int,
        sort: String,
    ): YaniCommentsResponseDto = YaniCommentsResponseDto()

    suspend fun getCommentChildren(commentId: Int, skip: Int): YaniCommentsResponseDto =
        YaniCommentsResponseDto()

    suspend fun addComment(
        targetType: String,
        targetId: Int,
        body: YaniPostCommentBodyDto,
    ): YaniCommentResponseDto = YaniCommentResponseDto()

    suspend fun updateComment(commentId: Int, body: YaniPatchCommentBodyDto): YaniCommentResponseDto =
        YaniCommentResponseDto()

    suspend fun deleteComment(commentId: Int): YaniBooleanResponseDto = YaniBooleanResponseDto()

    suspend fun voteComment(commentId: Int, body: YaniVoteCommentBodyDto): YaniVoteCommentResponseDto =
        YaniVoteCommentResponseDto()

    suspend fun removeCommentVote(commentId: Int): YaniVoteCommentResponseDto = YaniVoteCommentResponseDto()

    suspend fun reportComment(commentId: Int, body: YaniClaimCommentBodyDto): YaniBooleanResponseDto =
        YaniBooleanResponseDto()
}
