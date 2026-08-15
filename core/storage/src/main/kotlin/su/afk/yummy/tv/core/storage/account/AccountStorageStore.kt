package su.afk.yummy.tv.core.storage.account

internal class AccountStorageStore(private val dao: AccountStorageDao) : AccountStorage {

    override suspend fun getProfile(profileKey: String): AccountProfileEntry? =
        dao.getProfile(profileKey)

    override suspend fun saveProfile(entry: AccountProfileEntry) {
        dao.insertProfile(entry)
    }

    override suspend fun deleteProfile(profileKey: String) {
        dao.deleteProfile(profileKey)
    }

    override suspend fun getUserList(
        userId: Int,
        listId: Int,
        language: String,
    ): AccountUserListCache? =
        dao.getUserList(userId, listId, language)

    override suspend fun saveUserList(cache: AccountUserListCache) {
        dao.replaceUserList(cache)
    }

    override suspend fun saveUserLists(caches: List<AccountUserListCache>) {
        dao.replaceUserLists(caches)
    }

    override suspend fun hasUserListCache(userId: Int): Boolean =
        dao.hasUserListPages(userId)

    override suspend fun deleteUserLists(userId: Int) {
        dao.deleteUserLists(userId)
    }

    override suspend fun getAnimeListState(userId: Int, animeId: Int): AccountAnimeListStateEntry? =
        dao.getAnimeListState(userId, animeId)

    override suspend fun saveAnimeListState(entry: AccountAnimeListStateEntry) {
        dao.insertAnimeListState(entry)
    }

    override suspend fun getRatingBuckets(animeId: Int): AccountRatingBucketsCache? =
        dao.getRatingBuckets(animeId)

    override suspend fun saveRatingBuckets(cache: AccountRatingBucketsCache) {
        dao.replaceRatingBuckets(cache)
    }

    override suspend fun deleteRatingBuckets(animeId: Int) {
        dao.deleteRatingBucketsCache(animeId)
    }

    override suspend fun getUserRating(userId: Int, animeId: Int): AccountUserRatingEntry? =
        dao.getUserRating(userId, animeId)

    override suspend fun saveUserRating(entry: AccountUserRatingEntry) {
        dao.insertUserRating(entry)
    }

    override suspend fun getListStats(animeId: Int): AccountListStatsCache? =
        dao.getListStats(animeId)

    override suspend fun saveListStats(cache: AccountListStatsCache) {
        dao.replaceListStats(cache)
    }

    override suspend fun invalidateListStats(animeId: Int) {
        dao.invalidateListStats(animeId)
    }

    override suspend fun getCollections(pageKey: String): AccountCollectionsPageCache? =
        dao.getCollections(pageKey)

    override suspend fun saveCollections(
        cache: AccountCollectionsPageCache,
        prunePagesCachedBefore: Long?,
    ) {
        dao.replaceCollections(cache, prunePagesCachedBefore)
    }

    override suspend fun invalidateCollections() {
        dao.invalidateCollections()
    }

    override suspend fun getVideoSubscriptions(
        userId: Int,
        language: String,
    ): AccountVideoSubscriptionsCache? =
        dao.getVideoSubscriptions(userId, language)

    override suspend fun saveVideoSubscriptions(cache: AccountVideoSubscriptionsCache) {
        dao.replaceVideoSubscriptions(cache)
    }

    override suspend fun deleteVideoSubscriptions(userId: Int) {
        dao.deleteVideoSubscriptionsForUser(userId)
    }

    override suspend fun getNotifications(
        userId: Int,
        language: String,
        limit: Int,
        offset: Int,
    ): AccountNotificationsPageCache? =
        dao.getNotifications(userId, language, limit, offset)

    override suspend fun saveNotifications(
        cache: AccountNotificationsPageCache,
        prunePagesCachedBefore: Long?,
    ) {
        dao.replaceNotifications(cache, prunePagesCachedBefore)
    }

    override suspend fun deleteNotifications(userId: Int) {
        dao.deleteNotificationsForUser(userId)
    }

    override suspend fun getUserFriends(
        userId: Int,
        language: String,
        limit: Int,
        offset: Int,
    ): AccountUserFriendsPageCache? =
        dao.getUserFriendsPage(userId, language, limit, offset)

    override suspend fun saveUserFriends(cache: AccountUserFriendsPageCache) {
        dao.replaceUserFriendsPage(cache)
    }

    override suspend fun deleteUserFriends(userId: Int) {
        dao.deleteUserFriendsContentForUser(userId)
    }

    override suspend fun getUserReviews(
        userId: Int,
        language: String,
        limit: Int,
        offset: Int,
    ): AccountUserReviewsPageCache? =
        dao.getUserReviewsPage(userId, language, limit, offset)

    override suspend fun saveUserReviews(cache: AccountUserReviewsPageCache) {
        dao.replaceUserReviewsPage(cache)
    }

    override suspend fun getUserPosts(
        userId: Int,
        language: String,
        limit: Int,
        offset: Int,
    ): AccountUserPostsPageCache? =
        dao.getUserPostsPage(userId, language, limit, offset)

    override suspend fun saveUserPosts(cache: AccountUserPostsPageCache) {
        dao.replaceUserPostsPage(cache)
    }

    override suspend fun getNotificationCounts(userId: Int): AccountNotificationCountsCache? =
        dao.getNotificationCounts(userId)

    override suspend fun saveNotificationCounts(cache: AccountNotificationCountsCache) {
        dao.replaceNotificationCounts(cache)
    }

    override suspend fun deleteNotificationCounts(userId: Int) {
        dao.deleteNotificationCountCache(userId)
        dao.deleteNotificationCounts(userId)
    }

    override suspend fun getNotificationAnime(slug: String): AccountNotificationAnimeEntry? =
        dao.getNotificationAnime(slug)

    override suspend fun saveNotificationAnime(entry: AccountNotificationAnimeEntry) {
        dao.insertNotificationAnime(entry)
    }

    override suspend fun getUserStats(userId: Int, language: String): AccountUserStatsCache? =
        dao.getUserStats(userId, language)

    override suspend fun saveUserStats(cache: AccountUserStatsCache) {
        dao.replaceUserStats(cache)
    }

    override suspend fun getUserProfileSummary(
        userId: Int,
        language: String,
    ): AccountUserProfileSummaryCache? =
        dao.getUserProfileSummary(userId, language)

    override suspend fun saveUserProfileSummary(cache: AccountUserProfileSummaryCache) {
        dao.replaceUserProfileSummary(cache)
    }

    override suspend fun deleteUserProfileSummary(userId: Int) {
        dao.deleteUserProfileSummaryForUser(userId)
    }

    override suspend fun clearUserScoped(userId: Int) {
        dao.clearUserScoped(userId)
    }
}
