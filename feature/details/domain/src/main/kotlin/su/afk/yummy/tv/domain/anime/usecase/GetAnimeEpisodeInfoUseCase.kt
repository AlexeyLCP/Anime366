package su.afk.yummy.tv.domain.anime.usecase

import su.afk.yummy.tv.core.model.anime.AnimeEpisodeInfo
import su.afk.yummy.tv.domain.anime.repository.AnimeRepository
import javax.inject.Inject

/** Названия и описания серий по номеру серии; пустая мапа, если данных нет. */
class GetAnimeEpisodeInfoUseCase @Inject constructor(
    private val animeRepository: AnimeRepository,
) {
    suspend operator fun invoke(animeId: Int): Map<String, AnimeEpisodeInfo> =
        animeRepository.getAnimeEpisodeInfo(animeId)
}
