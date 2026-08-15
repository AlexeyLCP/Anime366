package su.afk.yummy.tv.core.storage.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE watch_progress ADD COLUMN videoId INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE library ADD COLUMN listId INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE library ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS library_new (
                animeId INTEGER NOT NULL,
                title TEXT NOT NULL,
                posterSmallUrl TEXT,
                posterMediumUrl TEXT,
                posterBigUrl TEXT,
                posterFullsizeUrl TEXT,
                posterMegaUrl TEXT,
                addedAt INTEGER NOT NULL,
                listId INTEGER NOT NULL,
                isFavorite INTEGER NOT NULL,
                PRIMARY KEY(animeId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO library_new (
                animeId,
                title,
                posterSmallUrl,
                posterMediumUrl,
                posterBigUrl,
                posterFullsizeUrl,
                posterMegaUrl,
                addedAt,
                listId,
                isFavorite
            )
            SELECT
                animeId,
                title,
                CASE WHEN posterUrl IS NULL THEN NULL ELSE replace(replace(replace(replace(replace(replace(replace(posterUrl, '/medium/', '/small/'), '/big/', '/small/'), '/full/', '/small/'), '/huge/', '/small/'), '/mega/', '/small/'), '.jpg', '.webp'), '.avif', '.webp') END,
                CASE WHEN posterUrl IS NULL THEN NULL ELSE replace(replace(replace(replace(replace(replace(replace(posterUrl, '/small/', '/medium/'), '/big/', '/medium/'), '/full/', '/medium/'), '/huge/', '/medium/'), '/mega/', '/medium/'), '.jpg', '.webp'), '.avif', '.webp') END,
                CASE WHEN posterUrl IS NULL THEN NULL ELSE replace(replace(replace(replace(replace(replace(replace(posterUrl, '/small/', '/big/'), '/medium/', '/big/'), '/full/', '/big/'), '/huge/', '/big/'), '/mega/', '/big/'), '.jpg', '.webp'), '.avif', '.webp') END,
                CASE WHEN posterUrl IS NULL THEN NULL ELSE replace(replace(replace(replace(replace(replace(replace(posterUrl, '/small/', '/full/'), '/medium/', '/full/'), '/big/', '/full/'), '/huge/', '/full/'), '/mega/', '/full/'), '.webp', '.jpg'), '.avif', '.jpg') END,
                CASE WHEN posterUrl IS NULL THEN NULL ELSE replace(replace(replace(replace(replace(replace(replace(posterUrl, '/small/', '/mega/'), '/medium/', '/mega/'), '/big/', '/mega/'), '/full/', '/mega/'), '/huge/', '/mega/'), '.webp', '.avif'), '.jpg', '.avif') END,
                addedAt,
                listId,
                isFavorite
            FROM library
            """.trimIndent()
        )
        db.execSQL("DROP TABLE library")
        db.execSQL("ALTER TABLE library_new RENAME TO library")
    }
}

internal val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE INDEX IF NOT EXISTS index_watch_progress_updatedAt ON watch_progress(updatedAt)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_library_addedAt ON library(addedAt)")
    }
}

internal val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) = Unit
}

internal val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_details (
                animeId INTEGER NOT NULL,
                language TEXT NOT NULL,
                animeUrl TEXT NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                posterSmallUrl TEXT,
                posterMediumUrl TEXT,
                posterBigUrl TEXT,
                posterFullsizeUrl TEXT,
                posterMegaUrl TEXT,
                ratingAverage REAL,
                ratingCounters INTEGER,
                ratingKinopoisk REAL,
                ratingShikimori REAL,
                ratingMyAnimeList REAL,
                year INTEGER,
                ageRating TEXT,
                views INTEGER,
                status TEXT,
                type TEXT,
                episodesCount INTEGER,
                episodesAired INTEGER,
                episodesNextDateEpochSeconds INTEGER,
                episodesPrevDateEpochSeconds INTEGER,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(animeId, language)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_anime_details_cachedAt ON anime_details(cachedAt)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_detail_titles (
                animeId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                title TEXT NOT NULL,
                PRIMARY KEY(animeId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_detail_titles_animeId_language
            ON anime_detail_titles(animeId, language)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_detail_named_items (
                animeId INTEGER NOT NULL,
                language TEXT NOT NULL,
                kind TEXT NOT NULL,
                position INTEGER NOT NULL,
                itemId INTEGER,
                title TEXT NOT NULL,
                PRIMARY KEY(animeId, language, kind, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_detail_named_items_animeId_language_kind
            ON anime_detail_named_items(animeId, language, kind)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_viewing_order (
                animeId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                relatedAnimeId INTEGER NOT NULL,
                title TEXT NOT NULL,
                relation TEXT,
                type TEXT,
                episodesCount INTEGER,
                posterSmallUrl TEXT,
                posterMediumUrl TEXT,
                posterBigUrl TEXT,
                posterFullsizeUrl TEXT,
                posterMegaUrl TEXT,
                year INTEGER,
                rating REAL,
                PRIMARY KEY(animeId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_viewing_order_animeId_language
            ON anime_viewing_order(animeId, language)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_screenshots (
                animeId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                screenshotId INTEGER,
                episode TEXT,
                smallUrl TEXT,
                fullUrl TEXT,
                PRIMARY KEY(animeId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_screenshots_animeId_language
            ON anime_screenshots(animeId, language)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_video_caches (
                animeId INTEGER NOT NULL,
                language TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(animeId, language)
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_anime_video_caches_cachedAt ON anime_video_caches(cachedAt)"
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_videos (
                animeId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                videoId INTEGER NOT NULL,
                episode TEXT NOT NULL,
                dubbing TEXT NOT NULL,
                player TEXT NOT NULL,
                playerId INTEGER,
                iframeUrl TEXT NOT NULL,
                durationSeconds INTEGER,
                views INTEGER,
                openingStartMs INTEGER,
                openingEndMs INTEGER,
                endingStartMs INTEGER,
                endingEndMs INTEGER,
                PRIMARY KEY(animeId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_videos_animeId_language
            ON anime_videos(animeId, language)
            """.trimIndent()
        )
    }
}

internal val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_recommendation_caches (
                animeId INTEGER NOT NULL,
                language TEXT NOT NULL,
                fromAi INTEGER NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(animeId, language, fromAi)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_recommendation_caches_cachedAt
            ON anime_recommendation_caches(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_recommendations (
                animeId INTEGER NOT NULL,
                language TEXT NOT NULL,
                fromAi INTEGER NOT NULL,
                position INTEGER NOT NULL,
                recommendationAnimeId INTEGER NOT NULL,
                title TEXT NOT NULL,
                posterSmallUrl TEXT,
                posterMediumUrl TEXT,
                posterBigUrl TEXT,
                posterFullsizeUrl TEXT,
                posterMegaUrl TEXT,
                rating REAL,
                type TEXT,
                year INTEGER,
                PRIMARY KEY(animeId, language, fromAi, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_recommendations_animeId_language_fromAi
            ON anime_recommendations(animeId, language, fromAi)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_trailer_caches (
                animeId INTEGER NOT NULL,
                language TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(animeId, language)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_trailer_caches_cachedAt
            ON anime_trailer_caches(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_trailers (
                animeId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                iframeUrl TEXT NOT NULL,
                PRIMARY KEY(animeId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_trailers_animeId_language
            ON anime_trailers(animeId, language)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS home_feed_caches (
                language TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(language)
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
                PRIMARY KEY(language, container, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_home_feed_items_language_container
            ON home_feed_items(language, container)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_top_pages (
                type TEXT NOT NULL,
                language TEXT NOT NULL,
                `limit` INTEGER NOT NULL,
                `offset` INTEGER NOT NULL,
                responseSize INTEGER NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(type, language, `limit`, `offset`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_top_pages_cachedAt
            ON anime_top_pages(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_top_items (
                type TEXT NOT NULL,
                language TEXT NOT NULL,
                `limit` INTEGER NOT NULL,
                `offset` INTEGER NOT NULL,
                position INTEGER NOT NULL,
                animeId INTEGER NOT NULL,
                title TEXT NOT NULL,
                posterUrl TEXT,
                rating REAL,
                PRIMARY KEY(type, language, `limit`, `offset`, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_top_items_page
            ON anime_top_items(type, language, `limit`, `offset`)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_schedule_caches (
                language TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(language)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_schedule_caches_cachedAt
            ON anime_schedule_caches(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_schedule_items (
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                animeId INTEGER NOT NULL,
                title TEXT NOT NULL,
                posterUrl TEXT,
                nextDateEpochSeconds INTEGER,
                previousDateEpochSeconds INTEGER,
                airedEpisodes INTEGER,
                totalEpisodes INTEGER,
                PRIMARY KEY(language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_anime_schedule_items_language
            ON anime_schedule_items(language)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS search_pages (
                pageKey TEXT NOT NULL,
                language TEXT NOT NULL,
                `limit` INTEGER NOT NULL,
                `offset` INTEGER NOT NULL,
                responseSize INTEGER NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(pageKey)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_search_pages_language ON search_pages(language)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_search_pages_cachedAt ON search_pages(cachedAt)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS search_items (
                pageKey TEXT NOT NULL,
                position INTEGER NOT NULL,
                animeId INTEGER NOT NULL,
                title TEXT NOT NULL,
                posterUrl TEXT,
                rating REAL,
                PRIMARY KEY(pageKey, position)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_search_items_pageKey ON search_items(pageKey)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS search_filter_options (
                language TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(language)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_search_filter_options_cachedAt
            ON search_filter_options(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS search_genre_groups (
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                groupId INTEGER NOT NULL,
                title TEXT NOT NULL,
                PRIMARY KEY(language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_search_genre_groups_language
            ON search_genre_groups(language)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS search_genres (
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                genreId TEXT NOT NULL,
                title TEXT NOT NULL,
                groupId INTEGER NOT NULL,
                PRIMARY KEY(language, position)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_search_genres_language ON search_genres(language)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS search_types (
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                typeId TEXT NOT NULL,
                title TEXT NOT NULL,
                PRIMARY KEY(language, position)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_search_types_language ON search_types(language)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS collection_details (
                collectionId INTEGER NOT NULL,
                language TEXT NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                views INTEGER NOT NULL,
                posterUrl TEXT,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(collectionId, language)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_collection_details_cachedAt
            ON collection_details(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS collection_anime_items (
                collectionId INTEGER NOT NULL,
                language TEXT NOT NULL,
                position INTEGER NOT NULL,
                animeId INTEGER NOT NULL,
                title TEXT NOT NULL,
                posterUrl TEXT,
                rating REAL,
                PRIMARY KEY(collectionId, language, position)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_collection_anime_items_collectionId_language
            ON collection_anime_items(collectionId, language)
            """.trimIndent()
        )
    }
}
