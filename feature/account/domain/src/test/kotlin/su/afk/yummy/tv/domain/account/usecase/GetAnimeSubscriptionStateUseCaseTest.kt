package su.afk.yummy.tv.domain.account.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import su.afk.yummy.tv.domain.account.model.SubscriptionKeys
import su.afk.yummy.tv.domain.account.model.VideoSubscription
import su.afk.yummy.tv.domain.account.model.VideoSubscriptionSelection
import su.afk.yummy.tv.domain.account.repository.VideoSubscriptionRepository

/**
 * `/users/{id}/lists/subs` не возвращает озвучку подписки, поэтому серверный список работает как
 * счётчик подписок на каждом балансере, а какая именно озвучка выбрана — знает локальная запись.
 */
class GetAnimeSubscriptionStateUseCaseTest {

    private val kodikKey = SubscriptionKeys.playerKey(4, "Kodik")
    private val anilibria = SubscriptionKeys.dubbingKey("Озвучка AniLibria")
    private val anidub = SubscriptionKeys.dubbingKey("Озвучка AniDUB")

    @Test
    fun `local selections confirmed by the server survive`() = runTest {
        val repository = FakeRepository(
            subscriptions = listOf(subscription(), subscription()),
            selections = listOf(
                selection(anilibria, videoId = 10),
                selection(anidub, videoId = 20)
            ),
        )

        val state = GetAnimeSubscriptionStateUseCase(repository)(userId = 1, animeId = ANIME_ID)

        assertEquals(
            setOf("$kodikKey|$anilibria", "$kodikKey|$anidub"),
            state.subscribedKeys,
        )
        assertEquals(10, state.videoIdsByKey["$kodikKey|$anilibria"])
    }

    @Test
    fun `unsubscribing elsewhere clears the local selections of that balancer`() = runTest {
        val repository = FakeRepository(
            subscriptions = emptyList(),
            selections = listOf(selection(anilibria, videoId = 10)),
        )

        val state = GetAnimeSubscriptionStateUseCase(repository)(userId = 1, animeId = ANIME_ID)

        assertEquals(emptySet<String>(), state.subscribedKeys)
        assertEquals(listOf(kodikKey), repository.removedPlayers)
    }

    @Test
    fun `a partial unsubscribe elsewhere drops the oldest selection`() = runTest {
        val repository = FakeRepository(
            subscriptions = listOf(subscription()),
            selections = listOf(
                selection(anilibria, videoId = 10, updatedAt = 1),
                selection(anidub, videoId = 20, updatedAt = 2),
            ),
        )

        val state = GetAnimeSubscriptionStateUseCase(repository)(userId = 1, animeId = ANIME_ID)

        assertEquals(setOf("$kodikKey|$anidub"), state.subscribedKeys)
        assertEquals(listOf(anilibria), repository.removedDubbings)
    }

    @Test
    fun `subscriptions of other titles are ignored`() = runTest {
        val repository = FakeRepository(
            subscriptions = listOf(subscription(animeId = ANIME_ID + 1)),
            selections = listOf(selection(anilibria, videoId = 10)),
        )

        val state = GetAnimeSubscriptionStateUseCase(repository)(userId = 1, animeId = ANIME_ID)

        assertEquals(emptySet<String>(), state.subscribedKeys)
    }

    private fun subscription(animeId: Int = ANIME_ID) = VideoSubscription(
        animeId = animeId,
        animeUrl = "saga-o-vinlande",
        playerId = 4,
        player = "Kodik",
        dubbing = "",
        posterUrl = null,
        title = "Сага о Винланде",
    )

    private fun selection(dubbingKey: String, videoId: Int, updatedAt: Long = 0) =
        VideoSubscriptionSelection(
            animeId = ANIME_ID,
            playerKey = kodikKey,
            dubbingKey = dubbingKey,
            videoId = videoId,
            updatedAt = updatedAt,
        )

    private class FakeRepository(
        private val subscriptions: List<VideoSubscription>,
        private val selections: List<VideoSubscriptionSelection>,
    ) : VideoSubscriptionRepository {
        val removedPlayers = mutableListOf<String>()
        val removedDubbings = mutableListOf<String>()

        override suspend fun getSubscriptions(userId: Int) = subscriptions

        override suspend fun setSubscribed(videoId: Int, subscribed: Boolean) = true

        override suspend fun getSelections(userId: Int, animeId: Int) = selections

        override suspend fun saveSelection(userId: Int, selection: VideoSubscriptionSelection) =
            Unit

        override suspend fun removeSelection(
            userId: Int,
            animeId: Int,
            playerKey: String,
            dubbingKey: String,
        ) {
            removedDubbings += dubbingKey
        }

        override suspend fun removeSelectionsForPlayer(
            userId: Int,
            animeId: Int,
            playerKey: String,
        ) {
            removedPlayers += playerKey
        }
    }

    private companion object {
        const val ANIME_ID = 1274
    }
}
