package su.afk.yummy.tv.core.storage.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_21_22 = object : Migration(21, 22) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS remote_continue_watching (
                accountKey TEXT NOT NULL,
                language TEXT NOT NULL,
                animeId INTEGER NOT NULL,
                targetKey TEXT NOT NULL,
                episode TEXT NOT NULL,
                videoId INTEGER NOT NULL,
                episodeUrl TEXT NOT NULL,
                positionMs INTEGER NOT NULL,
                durationMs INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL,
                animeTitle TEXT NOT NULL,
                posterUrl TEXT NOT NULL,
                playerName TEXT NOT NULL,
                dubbing TEXT NOT NULL,
                screenshotUrl TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(accountKey, language, animeId, targetKey)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_remote_continue_watching_account_language_updatedAt
            ON remote_continue_watching(accountKey, language, updatedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_remote_continue_watching_animeId
            ON remote_continue_watching(animeId)
            """.trimIndent()
        )
    }
}

internal val MIGRATION_22_23 = object : Migration(22, 23) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS comment_pages (
                scopeType TEXT NOT NULL,
                ownerId INTEGER NOT NULL,
                sort TEXT NOT NULL,
                `limit` INTEGER NOT NULL,
                skip INTEGER NOT NULL,
                responseSize INTEGER NOT NULL,
                isModerator INTEGER NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(scopeType, ownerId, sort, `limit`, skip)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_comment_pages_cachedAt
            ON comment_pages(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_comment_pages_scope
            ON comment_pages(scopeType, ownerId)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS comment_items (
                scopeType TEXT NOT NULL,
                ownerId INTEGER NOT NULL,
                sort TEXT NOT NULL,
                `limit` INTEGER NOT NULL,
                skip INTEGER NOT NULL,
                position INTEGER NOT NULL,
                commentId INTEGER NOT NULL,
                authorId INTEGER NOT NULL,
                authorName TEXT NOT NULL,
                avatarSmallUrl TEXT,
                avatarBigUrl TEXT,
                avatarFullUrl TEXT,
                text TEXT NOT NULL,
                createdAtEpochSeconds INTEGER NOT NULL,
                parentId INTEGER,
                childrenCount INTEGER NOT NULL,
                likes INTEGER NOT NULL,
                dislikes INTEGER NOT NULL,
                vote INTEGER NOT NULL,
                roles TEXT NOT NULL,
                deletedAtEpochSeconds INTEGER,
                PRIMARY KEY(scopeType, ownerId, sort, `limit`, skip, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_comment_items_page
            ON comment_items(scopeType, ownerId, sort, `limit`, skip)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_comment_items_commentId
            ON comment_items(commentId)
            """.trimIndent()
        )
    }
}

internal val MIGRATION_23_24 = object : Migration(23, 24) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_profile_content_pages (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                contentType TEXT NOT NULL,
                `limit` INTEGER NOT NULL,
                `offset` INTEGER NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(userId, language, contentType, `limit`, `offset`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_profile_content_pages_cachedAt
            ON account_user_profile_content_pages(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_profile_content_pages_scope
            ON account_user_profile_content_pages(userId, language, contentType)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_friends (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                `limit` INTEGER NOT NULL,
                `offset` INTEGER NOT NULL,
                position INTEGER NOT NULL,
                friendId INTEGER NOT NULL,
                nickname TEXT NOT NULL,
                avatarUrl TEXT,
                lastOnlineSeconds INTEGER NOT NULL,
                status TEXT NOT NULL,
                PRIMARY KEY(userId, language, `limit`, `offset`, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_friends_page
            ON account_user_friends(userId, language, `limit`, `offset`)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_reviews (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                `limit` INTEGER NOT NULL,
                `offset` INTEGER NOT NULL,
                position INTEGER NOT NULL,
                reviewId INTEGER NOT NULL,
                animeId INTEGER NOT NULL,
                animeTitle TEXT NOT NULL,
                animePosterUrl TEXT,
                textPreview TEXT NOT NULL,
                rating REAL,
                likes INTEGER NOT NULL,
                dislikes INTEGER NOT NULL,
                commentsCount INTEGER NOT NULL,
                updatedAtSeconds INTEGER NOT NULL,
                PRIMARY KEY(userId, language, `limit`, `offset`, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_reviews_page
            ON account_user_reviews(userId, language, `limit`, `offset`)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS account_user_posts (
                userId INTEGER NOT NULL,
                language TEXT NOT NULL,
                `limit` INTEGER NOT NULL,
                `offset` INTEGER NOT NULL,
                position INTEGER NOT NULL,
                postId INTEGER NOT NULL,
                title TEXT NOT NULL,
                previewImageUrl TEXT,
                contentPreview TEXT NOT NULL,
                categoryTitle TEXT NOT NULL,
                createdAtSeconds INTEGER NOT NULL,
                PRIMARY KEY(userId, language, `limit`, `offset`, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_account_user_posts_page
            ON account_user_posts(userId, language, `limit`, `offset`)
            """.trimIndent()
        )
    }
}

internal val MIGRATION_24_25 = object : Migration(24, 25) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS collection_catalog_pages (
                pageKey TEXT NOT NULL,
                language TEXT NOT NULL,
                pageLimit INTEGER NOT NULL,
                pageOffset INTEGER NOT NULL,
                responseSize INTEGER NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(pageKey)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_collection_catalog_pages_language
            ON collection_catalog_pages(language)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_collection_catalog_pages_cachedAt
            ON collection_catalog_pages(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS collection_catalog_items (
                pageKey TEXT NOT NULL,
                position INTEGER NOT NULL,
                collectionId INTEGER NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                posterUrl TEXT,
                PRIMARY KEY(pageKey, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_collection_catalog_items_pageKey
            ON collection_catalog_items(pageKey)
            """.trimIndent()
        )
    }
}

internal val MIGRATION_25_26 = object : Migration(25, 26) {
    override fun migrate(db: SupportSQLiteDatabase) {
        if (!db.hasColumn(
                tableName = "collection_catalog_pages",
                columnName = "responseSize"
            )
        ) {
            db.execSQL(
                """
                ALTER TABLE collection_catalog_pages
                ADD COLUMN responseSize INTEGER NOT NULL DEFAULT 0
                """.trimIndent()
            )
        }
        db.execSQL(
            """
            UPDATE collection_catalog_pages
            SET responseSize = pageLimit
            WHERE responseSize = 0
            """.trimIndent()
        )
    }
}

internal val MIGRATION_26_27 = object : Migration(26, 27) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            ALTER TABLE collection_catalog_items
            ADD COLUMN likes INTEGER NOT NULL DEFAULT 0
            """.trimIndent()
        )
        db.execSQL("UPDATE collection_catalog_pages SET cachedAt = 0")
    }
}

internal val MIGRATION_27_28 = object : Migration(27, 28) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            ALTER TABLE collection_details
            ADD COLUMN likes INTEGER NOT NULL DEFAULT 0
            """.trimIndent()
        )
        db.execSQL(
            """
            ALTER TABLE collection_details
            ADD COLUMN dislikes INTEGER NOT NULL DEFAULT 0
            """.trimIndent()
        )
        db.execSQL(
            """
            ALTER TABLE collection_details
            ADD COLUMN vote INTEGER NOT NULL DEFAULT 0
            """.trimIndent()
        )
        db.execSQL("UPDATE collection_details SET cachedAt = 0")
    }
}

internal val MIGRATION_28_29 = object : Migration(28, 29) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE account_user_list_items ADD COLUMN userRating INTEGER")
        db.execSQL("ALTER TABLE library ADD COLUMN userRating INTEGER")
    }
}

internal val MIGRATION_29_30 = object : Migration(29, 30) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE anime_videos ADD COLUMN watchedEndTimeSeconds INTEGER")
        db.execSQL("ALTER TABLE anime_videos ADD COLUMN watchedDateSeconds INTEGER")
    }
}

internal val MIGRATION_30_31 = object : Migration(30, 31) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS video_downloads (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                animeId INTEGER NOT NULL,
                animeTitle TEXT NOT NULL,
                posterUrl TEXT NOT NULL,
                episode TEXT NOT NULL,
                videoId INTEGER NOT NULL,
                playerName TEXT NOT NULL,
                playerId INTEGER,
                dubbing TEXT NOT NULL,
                iframeUrl TEXT NOT NULL,
                screenshotUrl TEXT NOT NULL,
                qualityLabel TEXT NOT NULL,
                streamUrl TEXT NOT NULL,
                headersJson TEXT NOT NULL,
                cacheKey TEXT NOT NULL,
                status TEXT NOT NULL,
                progress REAL NOT NULL,
                bytesDownloaded INTEGER NOT NULL,
                totalBytes INTEGER,
                errorMessage TEXT,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS index_video_downloads_duplicate_key
            ON video_downloads(animeId, videoId, iframeUrl, qualityLabel)
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_video_downloads_status ON video_downloads(status)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_video_downloads_updatedAt ON video_downloads(updatedAt)")
    }
}
