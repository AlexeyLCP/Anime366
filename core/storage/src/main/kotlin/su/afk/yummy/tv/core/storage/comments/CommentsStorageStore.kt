package su.afk.yummy.tv.core.storage.comments

internal class CommentsStorageStore(private val dao: CommentsStorageDao) : CommentsStorage {

    override suspend fun getPage(
        scopeType: String,
        ownerId: Int,
        sort: String,
        limit: Int,
        skip: Int,
    ): CommentsPageCache? = dao.getPage(scopeType, ownerId, sort, limit, skip)

    override suspend fun savePage(cache: CommentsPageCache, prunePagesCachedBefore: Long?) {
        dao.replacePage(cache, prunePagesCachedBefore)
    }

    override suspend fun invalidateScope(scopeType: String, ownerId: Int) {
        dao.invalidateScope(scopeType, ownerId)
    }

    override suspend fun invalidateScopePrefix(scopePrefix: String, ownerId: Int) {
        dao.invalidateScopePrefix(scopePrefix, ownerId)
    }

    override suspend fun deleteComment(commentId: Int) {
        dao.invalidatePagesContainingComment(commentId)
    }

    override suspend fun updateComment(entry: CommentItemEntry) {
        dao.updateComment(
            commentId = entry.commentId,
            text = entry.text,
            authorName = entry.authorName,
            avatarSmallUrl = entry.avatarSmallUrl,
            avatarBigUrl = entry.avatarBigUrl,
            avatarFullUrl = entry.avatarFullUrl,
            childrenCount = entry.childrenCount,
            likes = entry.likes,
            dislikes = entry.dislikes,
            vote = entry.vote,
            roles = entry.roles,
            deletedAtEpochSeconds = entry.deletedAtEpochSeconds,
        )
    }

    override suspend fun updateVote(commentId: Int, likes: Int, dislikes: Int, vote: Int) {
        dao.updateVote(commentId, likes, dislikes, vote)
    }
}
