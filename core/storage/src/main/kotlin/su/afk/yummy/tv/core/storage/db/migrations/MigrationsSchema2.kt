package su.afk.yummy.tv.core.storage.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_profiles (
                profileKey TEXT NOT NULL,
                userId INTEGER NOT NULL,
                nickname TEXT NOT NULL,
                avatarUrl TEXT,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(profileKey)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_account_profiles_userId ON account_profiles(userId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_account_profiles_cachedAt ON account_profiles(cachedAt)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_list_pages (
                userId INTEGER NOT NULL,
                listId INTEGER NOT NULL,
                language TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(userId, listId, language)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_list_pages_cachedAt
            ON account_user_list_pages(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_list_items (
                userId INTEGER NOT NULL,
                listId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                animeId INTEGER NOT NULL,
                title TEXT NOT NULL,
                posterUrl TEXT,
                posterSmallUrl TEXT,
                posterMediumUrl TEXT,
                posterBigUrl TEXT,
                posterFullsizeUrl TEXT,
                posterMegaUrl TEXT,
                rating REAL,
                year INTEGER,
                userListId INTEGER,
                isFavorite INTEGER NOT NULL,
                PRIMARY KEY(userId, listId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_list_items_page
            ON account_user_list_items(userId, listId, language)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_anime_list_states (
                userId INTEGER NOT NULL,
                animeId INTEGER NOT NULL,
                listId INTEGER,
                isFavorite INTEGER NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(userId, animeId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_anime_list_states_cachedAt
            ON account_anime_list_states(cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_rating_bucket_caches (
                animeId INTEGER NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(animeId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_rating_bucket_caches_cachedAt
            ON account_rating_bucket_caches(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_rating_buckets (
                animeId INTEGER NOT NULL,
                position INTEGER NOT NULL,
                rating INTEGER NOT NULL,
                count INTEGER NOT NULL,
                PRIMARY KEY(animeId, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_rating_buckets_animeId
            ON account_rating_buckets(animeId)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_ratings (
                userId INTEGER NOT NULL,
                animeId INTEGER NOT NULL,
                rating INTEGER,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(userId, animeId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_ratings_cachedAt
            ON account_user_ratings(cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_list_stats_caches (
                animeId INTEGER NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(animeId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_list_stats_caches_cachedAt
            ON account_list_stats_caches(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_list_stats (
                animeId INTEGER NOT NULL,
                listId INTEGER NOT NULL,
                count INTEGER NOT NULL,
                PRIMARY KEY(animeId, listId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_list_stats_animeId
            ON account_list_stats(animeId)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_collection_pages (
                pageKey TEXT NOT NULL,
                language TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(pageKey)
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_account_collection_pages_language ON account_collection_pages(language)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_account_collection_pages_cachedAt ON account_collection_pages(cachedAt)"
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_collection_items (
                pageKey TEXT NOT NULL,
                position INTEGER NOT NULL,
                collectionId INTEGER NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                posterUrl TEXT,
                posterSmallUrl TEXT,
                posterMediumUrl TEXT,
                posterBigUrl TEXT,
                posterFullsizeUrl TEXT,
                posterMegaUrl TEXT,
                views INTEGER,
                PRIMARY KEY(pageKey, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_account_collection_items_pageKey ON account_collection_items(pageKey)"
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_video_subscription_caches (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(userId, language)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_video_subscription_caches_cachedAt
            ON account_video_subscription_caches(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_video_subscriptions (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                animeId INTEGER NOT NULL,
                animeUrl TEXT NOT NULL,
                playerId INTEGER,
                player TEXT NOT NULL,
                dubbing TEXT NOT NULL,
                posterUrl TEXT,
                title TEXT NOT NULL,
                PRIMARY KEY(userId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_video_subscriptions_userId_language
            ON account_video_subscriptions(userId, language)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_notification_pages (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                `limit` INTEGER NOT NULL,
                `offset` INTEGER NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(userId, language, `limit`, `offset`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_notification_pages_cachedAt
            ON account_notification_pages(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_notifications (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                `limit` INTEGER NOT NULL,
                `offset` INTEGER NOT NULL,
                position INTEGER NOT NULL,
                notificationId INTEGER NOT NULL,
                dateSeconds INTEGER NOT NULL,
                title TEXT NOT NULL,
                text TEXT NOT NULL,
                clickUri TEXT NOT NULL,
                type TEXT NOT NULL,
                subType TEXT NOT NULL,
                viewed INTEGER NOT NULL,
                objectId INTEGER,
                animeSlug TEXT,
                isNewEpisode INTEGER NOT NULL,
                PRIMARY KEY(userId, language, `limit`, `offset`, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_notifications_page
            ON account_notifications(userId, language, `limit`, `offset`)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_notification_count_caches (
                userId INTEGER NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(userId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_notification_count_caches_cachedAt
            ON account_notification_count_caches(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_notification_counts (
                userId INTEGER NOT NULL,
                position INTEGER NOT NULL,
                type TEXT NOT NULL,
                count INTEGER NOT NULL,
                PRIMARY KEY(userId, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_notification_counts_userId
            ON account_notification_counts(userId)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_notification_anime (
                slug TEXT NOT NULL,
                animeId INTEGER,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(slug)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_notification_anime_cachedAt
            ON account_notification_anime(cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_stats_caches (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(userId, language)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_stats_caches_cachedAt
            ON account_user_stats_caches(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_genre_stats (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                genreId INTEGER NOT NULL,
                title TEXT NOT NULL,
                count INTEGER NOT NULL,
                PRIMARY KEY(userId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_genre_stats_userId_language
            ON account_user_genre_stats(userId, language)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_rating_stats (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                rating INTEGER NOT NULL,
                count INTEGER NOT NULL,
                PRIMARY KEY(userId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_rating_stats_userId_language
            ON account_user_rating_stats(userId, language)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_list_watch_stats (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                listId INTEGER NOT NULL,
                title TEXT NOT NULL,
                href TEXT NOT NULL,
                seconds INTEGER NOT NULL,
                PRIMARY KEY(userId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_list_watch_stats_userId_language
            ON account_user_list_watch_stats(userId, language)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_type_stats (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                typeId INTEGER NOT NULL,
                title TEXT NOT NULL,
                shortName TEXT NOT NULL,
                count INTEGER NOT NULL,
                PRIMARY KEY(userId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_type_stats_userId_language
            ON account_user_type_stats(userId, language)
            """.trimIndent()
        )
    }
}

internal val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS cache")
    }
}

internal val MIGRATION_17_18 = object : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_profile_summary_caches (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                nickname TEXT NOT NULL,
                avatarUrl TEXT,
                bannerUrl TEXT,
                registerDateSeconds INTEGER NOT NULL,
                birthDateSeconds INTEGER NOT NULL,
                sex INTEGER NOT NULL,
                about TEXT NOT NULL,
                daysOnline INTEGER NOT NULL,
                watchingCount INTEGER NOT NULL,
                plannedCount INTEGER NOT NULL,
                completedCount INTEGER NOT NULL,
                droppedCount INTEGER NOT NULL,
                postponedCount INTEGER NOT NULL,
                favoriteCount INTEGER NOT NULL,
                friendsCount INTEGER NOT NULL,
                reviewsCount INTEGER NOT NULL,
                commentsCount INTEGER NOT NULL,
                postsCount INTEGER NOT NULL,
                collectionsCount INTEGER NOT NULL,
                PRIMARY KEY(userId, language)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_profile_summary_caches_cachedAt
            ON account_user_profile_summary_caches(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_profile_watch_types (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                typeId INTEGER NOT NULL,
                alias TEXT NOT NULL,
                title TEXT NOT NULL,
                shortName TEXT NOT NULL,
                spentSeconds INTEGER NOT NULL,
                PRIMARY KEY(userId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_profile_watch_types_userId_language
            ON account_user_profile_watch_types(userId, language)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_profile_watch_history (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                dateSeconds INTEGER NOT NULL,
                durationSeconds INTEGER NOT NULL,
                episodeCount INTEGER NOT NULL,
                PRIMARY KEY(userId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_profile_watch_history_userId_language
            ON account_user_profile_watch_history(userId, language)
            """.trimIndent()
        )
    }
}

internal val MIGRATION_18_19 = object : Migration(18, 19) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP INDEX IF EXISTS index_home_feed_caches_cachedAt")
        db.execSQL("DROP INDEX IF EXISTS index_home_feed_items_language_container")
        db.execSQL("ALTER TABLE home_feed_caches RENAME TO home_feed_caches_old")
        db.execSQL("ALTER TABLE home_feed_items RENAME TO home_feed_items_old")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS home_feed_caches (
                language TEXT NOT NULL,
                watchSignature TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(language, watchSignature)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_home_feed_caches_cachedAt
            ON home_feed_caches(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS home_feed_items (
                language TEXT NOT NULL,
                watchSignature TEXT NOT NULL,
                container TEXT NOT NULL,
                position INTEGER NOT NULL,
                itemId INTEGER NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                posterSmallUrl TEXT,
                posterMediumUrl TEXT,
                posterBigUrl TEXT,
                posterFullsizeUrl TEXT,
                posterMegaUrl TEXT,
                rating REAL,
                actionType TEXT NOT NULL,
                actionId INTEGER NOT NULL,
                PRIMARY KEY(language, watchSignature, container, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_home_feed_items_language_signature_container
            ON home_feed_items(language, watchSignature, container)
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO home_feed_caches (
                language,
                watchSignature,
                cachedAt
            )
            SELECT
                language,
                '',
                cachedAt
            FROM home_feed_caches_old
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO home_feed_items (
                language,
                watchSignature,
                container,
                position,
                itemId,
                title,
                description,
                posterSmallUrl,
                posterMediumUrl,
                posterBigUrl,
                posterFullsizeUrl,
                posterMegaUrl,
                rating,
                actionType,
                actionId
            )
            SELECT
                language,
                '',
                container,
                position,
                itemId,
                title,
                description,
                posterSmallUrl,
                posterMediumUrl,
                posterBigUrl,
                posterFullsizeUrl,
                posterMegaUrl,
                rating,
                actionType,
                actionId
            FROM home_feed_items_old
            """.trimIndent()
        )
        db.execSQL("DROP TABLE home_feed_caches_old")
        db.execSQL("DROP TABLE home_feed_items_old")
    }
}

internal val MIGRATION_19_20 = object : Migration(19, 20) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE home_feed_items ADD COLUMN episode TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE home_feed_items ADD COLUMN episodeUrl TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE home_feed_items ADD COLUMN positionMs INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE home_feed_items ADD COLUMN durationMs INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE home_feed_items ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE home_feed_items ADD COLUMN playerName TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE home_feed_items ADD COLUMN dubbing TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE home_feed_items ADD COLUMN screenshotUrl TEXT NOT NULL DEFAULT ''")
    }
}

internal val MIGRATION_20_21 = object : Migration(20, 21) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE library ADD COLUMN listUpdatedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE library ADD COLUMN favoriteUpdatedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("UPDATE library SET listUpdatedAt = addedAt WHERE listUpdatedAt = 0")
        db.execSQL("UPDATE library SET favoriteUpdatedAt = addedAt WHERE isFavorite = 1 AND favoriteUpdatedAt = 0")
        db.execSQL("ALTER TABLE account_user_list_items ADD COLUMN updatedAtSeconds INTEGER")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS continue_watching_suppressions (
                animeId INTEGER NOT NULL,
                suppressedAt INTEGER NOT NULL,
                PRIMARY KEY(animeId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_continue_watching_suppressions_suppressedAt
            ON continue_watching_suppressions(suppressedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS library_sync_states (
                userId INTEGER NOT NULL,
                syncedAt INTEGER NOT NULL,
                PRIMARY KEY(userId)
            )
            """.trimIndent()
        )
    }
}
