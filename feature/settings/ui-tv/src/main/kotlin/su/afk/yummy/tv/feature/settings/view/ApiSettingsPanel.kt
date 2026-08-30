package su.afk.yummy.tv.feature.settings.view

import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.yummy.tv.feature.settings.R
import su.afk.yummy.tv.feature.settings.utils.restoreCategoryFocusOnLeft

@Composable
internal fun ApiSettingsPanel(
    token: String,
    upFocusRequester: FocusRequester,
    contentFocusRequester: FocusRequester? = null,
    restoreUpToTab: Boolean = true,
    cacheStorageSize: Long = 0L,
    onTokenChanged: (String) -> Unit,
    onShowCacheStorage: () -> Unit = {},
) {
    val context = LocalContext.current
    val host = token.ifBlank { ANIME365_MIRRORS.first() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AboutRow(
            label = stringResource(R.string.settings_yani_application_token_label),
            hint = host,
            modifier = Modifier
                .then(
                    if (contentFocusRequester != null) {
                        Modifier.focusRequester(contentFocusRequester)
                    } else {
                        Modifier
                    },
                )
                .then(
                    if (restoreUpToTab) {
                        Modifier.restoreCategoryFocusOnLeft(upFocusRequester)
                    } else {
                        Modifier
                    },
                ),
            onClick = { onTokenChanged(nextAnime365Mirror(host)) },
        )
        Text(
            text = stringResource(R.string.settings_yani_application_token_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        AboutRow(
            label = stringResource(R.string.settings_cache_storage_title),
            hint = Formatter.formatShortFileSize(context, cacheStorageSize),
            onClick = onShowCacheStorage,
        )
    }
}

private val ANIME365_MIRRORS = listOf(
    "anime-365.ru",
    "smotret-anime.org",
    "smotret-anime.app",
    "smotret-anime.net",
)

private fun nextAnime365Mirror(current: String): String {
    val index = ANIME365_MIRRORS.indexOf(current.trim().lowercase()).let { if (it < 0) 0 else it }
    return ANIME365_MIRRORS[(index + 1) % ANIME365_MIRRORS.size]
}
