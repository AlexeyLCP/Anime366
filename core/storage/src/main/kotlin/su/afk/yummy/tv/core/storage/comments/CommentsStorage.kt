package su.afk.yummy.tv.core.storage.comments

/** Абстракция над локальным кэшем комментариев — позволяет подменять реализацию в тестах. */
interface CommentsStorage {

    suspend fun getPage(
        scopeType: String,
        ownerId: Int,
        sort: String,
        limit: Int,
        skip: Int,
    ): CommentsPageCache?

    suspend fun savePage(cache: CommentsPageCache, prunePagesCachedBefore: Long? = null)

    suspend fun invalidateScope(scopeType: String, ownerId: Int)

    suspend fun invalidateScopePrefix(scopePrefix: String, ownerId: Int)

    suspend fun deleteComment(commentId: Int)

    suspend fun updateComment(entry: CommentItemEntry)

    suspend fun updateVote(commentId: Int, likes: Int, dislikes: Int, vote: Int)
}
