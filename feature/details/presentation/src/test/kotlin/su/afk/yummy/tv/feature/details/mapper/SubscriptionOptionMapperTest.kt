package su.afk.yummy.tv.feature.details.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.domain.account.model.AnimeSubscriptionState
import su.afk.yummy.tv.domain.account.model.SubscriptionKeys

/**
 * Регресс на подписки: раньше состояние восстанавливалось сравнением названий озвучек «на contains»,
 * из-за чего одна подписка подсвечивала все озвучки с похожим названием. Названия здесь — реальные,
 * из `GET /anime/{id}/videos`.
 */
class SubscriptionOptionMapperTest {

    private val videos = listOf(
        video(id = 10, episode = "1", dubbing = SUBS, player = KODIK, playerId = 4),
        video(id = 11, episode = "2", dubbing = SUBS, player = KODIK, playerId = 4),
        video(id = 20, episode = "1", dubbing = SUBS_WAKANIM, player = KODIK, playerId = 4),
        video(id = 30, episode = "1", dubbing = SUBS_YAKUSUB, player = KODIK, playerId = 4),
        video(id = 40, episode = "1", dubbing = SUBS, player = CVH, playerId = 3),
    )

    @Test
    fun `subscription lights up only its own dubbing`() {
        val options = videos.toSubscriptionOptions(
            state = AnimeSubscriptionState(subscribedKeys = setOf(key(4, KODIK, SUBS))),
        )

        val subscribed = options.filter { it.isSubscribed }
        assertEquals(1, subscribed.size)
        assertEquals(SUBS, subscribed.single().dubbing)
        assertEquals(KODIK, subscribed.single().player)
    }

    @Test
    fun `same dubbing on another balancer stays unsubscribed`() {
        val options = videos.toSubscriptionOptions(
            state = AnimeSubscriptionState(subscribedKeys = setOf(key(4, KODIK, SUBS))),
        )

        val cvh = options.single { it.player == CVH }
        assertFalse(cvh.isSubscribed)
    }

    @Test
    fun `stored video id is used for the subscription request`() {
        val subscriptionKey = key(4, KODIK, SUBS)
        val options = videos.toSubscriptionOptions(
            state = AnimeSubscriptionState(
                subscribedKeys = setOf(subscriptionKey),
                videoIdsByKey = mapOf(subscriptionKey to 10),
            ),
        )

        assertEquals(10, options.single { it.key == subscriptionKey }.subscriptionVideoId)
    }

    @Test
    fun `without a stored video id the latest episode is used`() {
        val options = videos.toSubscriptionOptions()

        assertEquals(11, options.single { it.key == key(4, KODIK, SUBS) }.subscriptionVideoId)
    }

    @Test
    fun `pending state wins while the request is in flight`() {
        val subscriptionKey = key(4, KODIK, SUBS_WAKANIM)
        val options = videos.toSubscriptionOptions(
            pendingStates = mapOf(subscriptionKey to true),
        )

        assertTrue(options.single { it.key == subscriptionKey }.isSubscribed)
    }

    @Test
    fun `videos are grouped into one option per dubbing and balancer`() {
        val options = videos.toSubscriptionOptions()

        assertEquals(4, options.size)
        assertEquals(2, options.single { it.key == key(4, KODIK, SUBS) }.episodesCount)
    }

    private fun key(playerId: Int, player: String, dubbing: String): String =
        SubscriptionKeys.subscriptionKey(playerId, player, dubbing)

    private fun video(
        id: Int,
        episode: String,
        dubbing: String,
        player: String,
        playerId: Int,
    ) = AnimeVideo(
        id = id,
        episode = episode,
        dubbing = dubbing,
        player = player,
        playerId = playerId,
        iframeUrl = "https://example.test/$id",
        durationSeconds = null,
    )

    private companion object {
        const val KODIK = "Плеер Kodik"
        const val CVH = "Плеер CVH"
        const val SUBS = "Субтитры"
        const val SUBS_WAKANIM = "Субтитры Wakanim"
        const val SUBS_YAKUSUB = "Субтитры YakuSub Studio"
    }
}
