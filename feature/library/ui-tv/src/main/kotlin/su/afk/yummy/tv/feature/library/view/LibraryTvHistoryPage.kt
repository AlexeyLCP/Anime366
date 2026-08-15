package su.afk.yummy.tv.feature.library.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.flow.Flow
import su.afk.yummy.tv.core.designsystem.presenter.focus.tvFocusableClick
import su.afk.yummy.tv.core.model.anime.AnimeWatchProgress
import su.afk.yummy.tv.core.utils.episode.episodeGroupKey
import su.afk.yummy.tv.core.utils.kodik.KodikThumbnail
import su.afk.yummy.tv.core.utils.kodik.resolveContinueWatchingImageModel
import su.afk.yummy.tv.domain.library.model.WatchHistoryEntry
import su.afk.yummy.tv.feature.library.R
import su.afk.yummy.tv.feature.library.thumbnail.HistoryEpisodeThumbnail
import su.afk.yummy.tv.feature.library.utils.timingLabel
import su.afk.yummy.tv.feature.library.utils.watchedAtLabel

@Composable
internal fun LibraryTvHistoryPage(
    history: Flow<PagingData<WatchHistoryEntry>>,
    localProgress: ImmutableMap<String, AnimeWatchProgress>,
    isSignedIn: Boolean,
    gridFocusRequester: FocusRequester,
    onEntrySelected: (WatchHistoryEntry) -> Unit,
    onDetailsSelected: (WatchHistoryEntry) -> Unit,
) {
    if (!isSignedIn) {
        HistoryMessage(stringResource(R.string.library_history_sign_in))
        return
    }
    val items = history.collectAsLazyPagingItems()
    when {
        items.loadState.refresh is LoadState.Loading -> HistoryMessage(null, true)
        items.loadState.refresh is LoadState.Error -> HistoryMessage(stringResource(R.string.library_history_error))
        items.itemCount == 0 -> HistoryMessage(stringResource(R.string.library_history_empty))
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items.itemCount,
                key = { index ->
                    items[index]?.let { "${it.animeId}_${it.episode}_${it.watchedAtSeconds}" }
                        ?: index
                },
            ) { index ->
                items[index]?.let { entry ->
                    val cardFocusRequester =
                        remember { if (index == 0) gridFocusRequester else FocusRequester() }
                    val detailsFocusRequester = remember { FocusRequester() }
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(cardFocusRequester)
                            .focusProperties { right = detailsFocusRequester }
                            .tvFocusableClick(onClick = { onEntrySelected(entry) }),
                    ) {
                        Row(
                            Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            SubcomposeAsyncImage(
                                model = entry.screenshotUrl
                                    ?: localProgress["${entry.animeId}:${entry.episode.episodeGroupKey()}"]
                                        ?.let {
                                            resolveContinueWatchingImageModel(
                                                screenshotUrl = it.screenshotUrl,
                                                episodeUrl = it.episodeUrl,
                                                posterUrl = null,
                                                kodikThumbnailModel = ::KodikThumbnail,
                                            )
                                        }
                                    ?: HistoryEpisodeThumbnail(entry.animeId, entry.episode),
                                contentDescription = entry.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(90.dp),
                            ) {
                                val state by painter.state.collectAsStateWithLifecycle()
                                if (state is AsyncImagePainter.State.Success) {
                                    SubcomposeAsyncImageContent()
                                } else {
                                    AsyncImage(
                                        model = entry.posterUrl,
                                        contentDescription = entry.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                            }
                            Column(Modifier.weight(1f)) {
                                Text(entry.title, style = MaterialTheme.typography.titleLarge)
                                if (entry.episode.isNotBlank()) Text(
                                    stringResource(
                                        R.string.library_history_episode,
                                        entry.episode
                                    )
                                )
                                entry.timingLabel()?.let { Text(it) }
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        entry.watchedAtLabel().orEmpty(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    LibraryDetailsButton(
                                        onClick = { onDetailsSelected(entry) },
                                        modifier = Modifier
                                            .focusRequester(detailsFocusRequester)
                                            .focusProperties { left = cardFocusRequester },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryMessage(text: String?, loading: Boolean = false) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (loading) CircularProgressIndicator() else Text(text.orEmpty())
    }
}
