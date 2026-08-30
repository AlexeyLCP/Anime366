package su.afk.yummy.tv.data.bloggers.network

import su.afk.yummy.tv.data.bloggers.dto.BloggerDetailsDto
import su.afk.yummy.tv.data.bloggers.dto.BloggerDetailsResponseDto
import su.afk.yummy.tv.data.bloggers.dto.BloggerSubscriptionDto
import su.afk.yummy.tv.data.bloggers.dto.BloggerSubscriptionResponseDto
import su.afk.yummy.tv.data.bloggers.dto.BloggerVideoDto
import su.afk.yummy.tv.data.bloggers.dto.BloggerVideoReactionDto
import su.afk.yummy.tv.data.bloggers.dto.BloggerVideoReactionResponseDto
import su.afk.yummy.tv.data.bloggers.dto.BloggerVideoResponseDto
import su.afk.yummy.tv.data.bloggers.dto.BloggerVideosResponseDto
import su.afk.yummy.tv.data.bloggers.dto.BloggersResponseDto
import javax.inject.Inject

class YaniBloggerVideosApi @Inject constructor() {
    suspend fun getVideos(
        category: String,
        bloggerId: Int?,
        sort: String,
        limit: Int,
        offset: Int,
    ): BloggerVideosResponseDto = BloggerVideosResponseDto()

    suspend fun getAnimeVideos(animeId: Int, limit: Int, offset: Int): BloggerVideosResponseDto =
        BloggerVideosResponseDto()

    suspend fun getDirectory(limit: Int): BloggersResponseDto = BloggersResponseDto()

    suspend fun getBlogger(id: Int): BloggerDetailsResponseDto =
        BloggerDetailsResponseDto(BloggerDetailsDto(id = id))

    suspend fun getVideo(id: Int): BloggerVideoResponseDto =
        BloggerVideoResponseDto(BloggerVideoDto(id = id))

    suspend fun subscribe(id: Int): BloggerSubscriptionResponseDto =
        BloggerSubscriptionResponseDto(BloggerSubscriptionDto())

    suspend fun unsubscribe(id: Int): BloggerSubscriptionResponseDto =
        BloggerSubscriptionResponseDto(BloggerSubscriptionDto())

    suspend fun vote(id: Int, action: String): BloggerVideoReactionResponseDto =
        BloggerVideoReactionResponseDto(BloggerVideoReactionDto())

    suspend fun removeVote(id: Int): BloggerVideoReactionResponseDto =
        BloggerVideoReactionResponseDto(BloggerVideoReactionDto())
}
