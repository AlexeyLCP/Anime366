package su.afk.yummy.tv.feature.settings.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.stringResource
import su.afk.yummy.tv.core.model.settings.AppTheme
import su.afk.yummy.tv.core.model.settings.BackgroundStyle
import su.afk.yummy.tv.core.model.settings.DetailsButtonAction
import su.afk.yummy.tv.core.model.settings.LibraryContinueWatchingCardSize
import su.afk.yummy.tv.core.model.settings.PlayerBufferProfile
import su.afk.yummy.tv.core.model.settings.PlayerSubtitleBackground
import su.afk.yummy.tv.core.model.settings.PlayerSubtitleOffset
import su.afk.yummy.tv.core.model.settings.PlayerSubtitleTextColor
import su.afk.yummy.tv.core.model.settings.PlayerSubtitleTextSize
import su.afk.yummy.tv.core.model.settings.PosterCardSize
import su.afk.yummy.tv.core.model.settings.PosterQuality
import su.afk.yummy.tv.core.model.settings.PreferredPlayer
import su.afk.yummy.tv.core.model.settings.PreferredVideoQuality
import su.afk.yummy.tv.core.model.settings.PreviewCacheSize
import su.afk.yummy.tv.core.model.settings.YaniContentLanguage
import su.afk.yummy.tv.core.preferences.interface_mode.AppInterfaceMode
import su.afk.yummy.tv.feature.settings.R
import su.afk.yummy.tv.feature.settings.model.DetailsButtonOrderItem

@Composable
internal fun AppInterfaceMode.label(): String = stringResource(
    when (this) {
        AppInterfaceMode.MOBILE -> R.string.settings_interface_mobile
        AppInterfaceMode.TV -> R.string.settings_interface_tv
    },
)

@Composable
internal fun AppInterfaceMode.hint(): String = stringResource(
    when (this) {
        AppInterfaceMode.MOBILE -> R.string.settings_interface_mobile_hint
        AppInterfaceMode.TV -> R.string.settings_interface_tv_hint
    },
)

@Composable
internal fun List<DetailsButtonAction>.toDetailsButtonOrderItems(): List<DetailsButtonOrderItem> =
    buildList {
        var index = 0
        while (index <= this@toDetailsButtonOrderItems.lastIndex) {
            val action = this@toDetailsButtonOrderItems[index]
            val nextAction = this@toDetailsButtonOrderItems.getOrNull(index + 1)
            if (action == DetailsButtonAction.LIBRARY && nextAction == DetailsButtonAction.FAVORITE) {
                add(
                    DetailsButtonOrderItem(
                        key = "LIBRARY_FAVORITE",
                        action = DetailsButtonAction.LIBRARY,
                        label = stringResource(R.string.settings_details_button_library_favorite),
                    ),
                )
                index += 2
            } else if (action != DetailsButtonAction.FAVORITE) {
                add(
                    DetailsButtonOrderItem(
                        key = action.name,
                        action = action,
                        label = action.label(),
                    ),
                )
                index += 1
            } else {
                index += 1
            }
        }
    }

@Composable
internal fun AppTheme.label(): String = stringResource(
    when (this) {
        AppTheme.WARM_AMBER -> R.string.settings_theme_warm_amber
        AppTheme.SAKURA -> R.string.settings_theme_sakura
        AppTheme.MINT -> R.string.settings_theme_mint
        AppTheme.OCEAN -> R.string.settings_theme_ocean
        AppTheme.GRAPHITE -> R.string.settings_theme_graphite
    },
)

@Composable
internal fun AppTheme.hint(): String = stringResource(
    when (this) {
        AppTheme.WARM_AMBER -> R.string.settings_theme_warm_amber_hint
        AppTheme.SAKURA -> R.string.settings_theme_sakura_hint
        AppTheme.MINT -> R.string.settings_theme_mint_hint
        AppTheme.OCEAN -> R.string.settings_theme_ocean_hint
        AppTheme.GRAPHITE -> R.string.settings_theme_graphite_hint
    },
)

@Composable
internal fun BackgroundStyle.label(): String = stringResource(
    when (this) {
        BackgroundStyle.SYSTEM -> R.string.settings_background_system
        BackgroundStyle.LIGHT -> R.string.settings_background_light
        BackgroundStyle.DARK -> R.string.settings_background_dark
    },
)

@Composable
internal fun BackgroundStyle.hint(): String = stringResource(
    when (this) {
        BackgroundStyle.SYSTEM -> R.string.settings_background_system_hint
        BackgroundStyle.LIGHT -> R.string.settings_background_light_hint
        BackgroundStyle.DARK -> R.string.settings_background_dark_hint
    },
)

@Composable
internal fun DetailsButtonAction.label(): String = stringResource(
    when (this) {
        DetailsButtonAction.WATCH -> R.string.settings_details_button_watch
        DetailsButtonAction.LIBRARY -> R.string.settings_details_button_library
        DetailsButtonAction.FAVORITE -> R.string.settings_details_button_favorite
        DetailsButtonAction.EPISODES -> R.string.settings_details_button_episodes
        DetailsButtonAction.SUBSCRIPTIONS -> R.string.settings_details_button_subscriptions
        DetailsButtonAction.FULL_DETAILS -> R.string.settings_details_button_full_details
        DetailsButtonAction.TRAILERS -> R.string.settings_details_button_trailers
        DetailsButtonAction.SIMILAR -> R.string.settings_details_button_similar
        DetailsButtonAction.VIEWING_ORDER -> R.string.settings_details_button_viewing_order
        DetailsButtonAction.RATING -> R.string.settings_details_button_rating
        DetailsButtonAction.COLLECTIONS -> R.string.settings_details_button_collections
        DetailsButtonAction.COMMENTS -> R.string.settings_details_button_comments
        DetailsButtonAction.REVIEWS -> R.string.settings_details_button_reviews
        DetailsButtonAction.BLOGGER_VIDEOS -> R.string.settings_details_button_blogger_videos
        DetailsButtonAction.SCREENSHOTS -> R.string.settings_details_button_screenshots
    },
)

@Composable
internal fun PosterCardSize.label(): String = stringResource(
    when (this) {
        PosterCardSize.COMPACT -> R.string.settings_poster_card_size_compact
        PosterCardSize.STANDARD -> R.string.settings_poster_card_size_standard
        PosterCardSize.LARGE -> R.string.settings_poster_card_size_large
    },
)

@Composable
internal fun PosterCardSize.hint(): String = stringResource(
    when (this) {
        PosterCardSize.COMPACT -> R.string.settings_poster_card_size_compact_hint
        PosterCardSize.STANDARD -> R.string.settings_poster_card_size_standard_hint
        PosterCardSize.LARGE -> R.string.settings_poster_card_size_large_hint
    },
)

@Composable
internal fun LibraryContinueWatchingCardSize.label(): String = stringResource(
    when (this) {
        LibraryContinueWatchingCardSize.COMPACT ->
            R.string.settings_library_continue_watching_card_size_compact

        LibraryContinueWatchingCardSize.STANDARD ->
            R.string.settings_library_continue_watching_card_size_standard

        LibraryContinueWatchingCardSize.LARGE ->
            R.string.settings_library_continue_watching_card_size_large
    },
)

@Composable
internal fun LibraryContinueWatchingCardSize.hint(): String = stringResource(
    when (this) {
        LibraryContinueWatchingCardSize.COMPACT ->
            R.string.settings_library_continue_watching_card_size_compact_hint

        LibraryContinueWatchingCardSize.STANDARD ->
            R.string.settings_library_continue_watching_card_size_standard_hint

        LibraryContinueWatchingCardSize.LARGE ->
            R.string.settings_library_continue_watching_card_size_large_hint
    },
)

@Composable
internal fun PosterQuality.label(): String = stringResource(
    when (this) {
        PosterQuality.LOW -> R.string.settings_poster_quality_low
        PosterQuality.STANDARD -> R.string.settings_poster_quality_standard
        PosterQuality.MEGA -> R.string.settings_poster_quality_mega
        PosterQuality.HIGH -> R.string.settings_poster_quality_high
    },
)

@Composable
internal fun PosterQuality.hint(): String = stringResource(
    when (this) {
        PosterQuality.LOW -> R.string.settings_poster_quality_low_hint
        PosterQuality.STANDARD -> R.string.settings_poster_quality_standard_hint
        PosterQuality.MEGA -> R.string.settings_poster_quality_mega_hint
        PosterQuality.HIGH -> R.string.settings_poster_quality_high_hint
    },
)

@Composable
internal fun PreferredPlayer.label(): String = when (this) {
    PreferredPlayer.NONE -> stringResource(R.string.settings_preferred_player_none)
    PreferredPlayer.KODIK -> stringResource(R.string.settings_preferred_player_kodik)
    PreferredPlayer.AKSOR -> stringResource(R.string.settings_preferred_player_aksor)
    PreferredPlayer.ALLOHA -> stringResource(R.string.settings_preferred_player_alloha)
    PreferredPlayer.CVH -> stringResource(R.string.settings_preferred_player_cvh)
    PreferredPlayer.VK -> stringResource(R.string.settings_preferred_player_vk)
    PreferredPlayer.RUTUBE -> stringResource(R.string.settings_preferred_player_rutube)
}

@Composable
internal fun PreferredPlayer.hint(): String = when (this) {
    PreferredPlayer.NONE -> stringResource(R.string.settings_preferred_player_none_hint)
    PreferredPlayer.KODIK -> stringResource(R.string.settings_preferred_player_kodik_hint)
    PreferredPlayer.AKSOR -> stringResource(R.string.settings_preferred_player_aksor_hint)
    PreferredPlayer.ALLOHA -> stringResource(R.string.settings_preferred_player_alloha_hint)
    PreferredPlayer.CVH -> stringResource(R.string.settings_preferred_player_cvh_hint)
    PreferredPlayer.VK -> stringResource(R.string.settings_preferred_player_vk_hint)
    PreferredPlayer.RUTUBE -> stringResource(R.string.settings_preferred_player_rutube_hint)
}

@Composable
internal fun PreferredVideoQuality.label(): String = stringResource(
    when (this) {
        PreferredVideoQuality.BEST -> R.string.settings_preferred_video_quality_best
        PreferredVideoQuality.P2160 -> R.string.settings_preferred_video_quality_2160
        PreferredVideoQuality.P1440 -> R.string.settings_preferred_video_quality_1440
        PreferredVideoQuality.P1080 -> R.string.settings_preferred_video_quality_1080
        PreferredVideoQuality.P720 -> R.string.settings_preferred_video_quality_720
        PreferredVideoQuality.P480 -> R.string.settings_preferred_video_quality_480
        PreferredVideoQuality.P360 -> R.string.settings_preferred_video_quality_360
    },
)

@Composable
internal fun PreferredVideoQuality.hint(): String = stringResource(
    when (this) {
        PreferredVideoQuality.BEST -> R.string.settings_preferred_video_quality_best_hint
        PreferredVideoQuality.P2160,
        PreferredVideoQuality.P1440,
        PreferredVideoQuality.P1080,
        PreferredVideoQuality.P720,
        PreferredVideoQuality.P480,
        PreferredVideoQuality.P360 -> R.string.settings_preferred_video_quality_fallback_hint
    },
)

@Composable
internal fun PreviewCacheSize.label(): String = stringResource(
    when (this) {
        PreviewCacheSize.MB_50 -> R.string.settings_cache_size_min
        PreviewCacheSize.MB_100 -> R.string.settings_cache_size_standard
        PreviewCacheSize.MB_200 -> R.string.settings_cache_size_large
        PreviewCacheSize.MB_300 -> R.string.settings_cache_size_max
    },
)

@Composable
internal fun PreviewCacheSize.hint(): String = stringResource(
    when (this) {
        PreviewCacheSize.MB_50 -> R.string.settings_cache_size_50
        PreviewCacheSize.MB_100 -> R.string.settings_cache_size_100
        PreviewCacheSize.MB_200 -> R.string.settings_cache_size_200
        PreviewCacheSize.MB_300 -> R.string.settings_cache_size_300
    },
)

@Composable
internal fun YaniContentLanguage.label(): String = stringResource(
    when (this) {
        YaniContentLanguage.RUSSIAN -> R.string.settings_content_language_russian
        YaniContentLanguage.ENGLISH -> R.string.settings_content_language_english
        YaniContentLanguage.UKRAINIAN -> R.string.settings_content_language_ukrainian
    },
)

/** Дружелюбная подпись папки кэша; неизвестные имена показываются как есть. */
@Composable
internal fun cacheStorageFolderLabel(id: String): String = when (id) {
    "image_cache" -> stringResource(R.string.settings_cache_storage_folder_images)
    "player_streaming_cache" -> stringResource(R.string.settings_cache_storage_folder_player)
    "video_download_cache" -> stringResource(R.string.settings_cache_storage_folder_downloads)
    "video_exports" -> stringResource(R.string.settings_cache_storage_folder_exports)
    else -> if (id.startsWith("yummy_cache.db")) {
        stringResource(R.string.settings_cache_storage_folder_database)
    } else {
        id
    }
}

/**
 * Возврат фокуса из панели контента к списку категорий слева (D-pad влево).
 * Навешивается на первый пункт панели; между строками контента вверх/вниз работает штатно.
 */
internal fun Modifier.restoreCategoryFocusOnLeft(
    focusRequester: FocusRequester,
    enabled: Boolean = true,
): Modifier = if (enabled) {
    focusProperties { left = focusRequester }
} else {
    this
}

@Composable
internal fun PlayerSubtitleTextSize.label(): String =
    stringResource(R.string.settings_subtitle_percent, percent)

@Composable
internal fun PlayerSubtitleTextSize.hint(): String =
    stringResource(R.string.settings_subtitle_size_hint, percent)

@Composable
internal fun PlayerSubtitleOffset.label(): String =
    stringResource(R.string.settings_subtitle_percent, percent)

@Composable
internal fun PlayerSubtitleOffset.hint(): String =
    stringResource(R.string.settings_subtitle_offset_hint, percent)

@Composable
internal fun PlayerSubtitleTextColor.label(): String = stringResource(
    when (this) {
        PlayerSubtitleTextColor.WHITE -> R.string.settings_subtitle_color_white
        PlayerSubtitleTextColor.YELLOW -> R.string.settings_subtitle_color_yellow
        PlayerSubtitleTextColor.CYAN -> R.string.settings_subtitle_color_cyan
        PlayerSubtitleTextColor.GREEN -> R.string.settings_subtitle_color_green
    },
)

@Composable
internal fun PlayerSubtitleBackground.label(): String = stringResource(
    when (this) {
        PlayerSubtitleBackground.NONE -> R.string.settings_subtitle_background_none
        PlayerSubtitleBackground.TRANSLUCENT -> R.string.settings_subtitle_background_translucent
        PlayerSubtitleBackground.SOLID -> R.string.settings_subtitle_background_solid
    },
)

@Composable
internal fun PlayerBufferProfile.label(): String = stringResource(
    when (this) {
        PlayerBufferProfile.MINIMAL -> R.string.settings_player_buffer_minimal
        PlayerBufferProfile.SMALL -> R.string.settings_player_buffer_small
        PlayerBufferProfile.MEDIUM -> R.string.settings_player_buffer_medium
        PlayerBufferProfile.LARGE -> R.string.settings_player_buffer_large
    },
)

@Composable
internal fun PlayerBufferProfile.hint(): String = stringResource(
    when (this) {
        PlayerBufferProfile.MINIMAL -> R.string.settings_player_buffer_minimal_hint
        PlayerBufferProfile.SMALL -> R.string.settings_player_buffer_small_hint
        PlayerBufferProfile.MEDIUM -> R.string.settings_player_buffer_medium_hint
        PlayerBufferProfile.LARGE -> R.string.settings_player_buffer_large_hint
    },
)
