package su.afk.yummy.tv.data.home.tv

import su.afk.yummy.tv.core.tv.api.TvChannelContent
import su.afk.yummy.tv.core.tv.api.TvChannelContentProvider
import su.afk.yummy.tv.data.home.mapper.toTvChannelContent
import su.afk.yummy.tv.domain.home.model.HomeFeedSectionType
import su.afk.yummy.tv.domain.home.usecase.GetHomeFeedUseCase
import su.afk.yummy.tv.domain.home.usecase.RefreshHomeFeedUseCase
import javax.inject.Inject

/**
 * Наполняет системный preview-канал Android TV секцией новинок главной ленты,
 * а при её отсутствии — первой доступной секцией.
 */
internal class HomeTvChannelContentProvider @Inject constructor(
    private val getHomeFeed: GetHomeFeedUseCase,
    private val refreshHomeFeed: RefreshHomeFeedUseCase,
) : TvChannelContentProvider {

    override suspend fun newReleases(): List<TvChannelContent> {
        val feed = getHomeFeed()
        val items = feed.sections
            .firstOrNull { it.type == HomeFeedSectionType.NEW_RELEASES }
            ?.items
            ?: feed.sections.firstOrNull()?.items
            ?: emptyList()
        return items.mapNotNull { it.toTvChannelContent() }
    }

    override suspend fun refresh() {
        refreshHomeFeed()
    }
}
