package su.afk.yummy.tv.data.posts.repository

import su.afk.yummy.tv.core.network.UserScopedCache
import su.afk.yummy.tv.core.utils.toHttpsUrl
import su.afk.yummy.tv.data.posts.dto.YaniPostAuthorDto
import su.afk.yummy.tv.data.posts.dto.YaniPostCategoriesResponseDto
import su.afk.yummy.tv.data.posts.dto.YaniPostCategoryDto
import su.afk.yummy.tv.data.posts.dto.YaniPostDetailsDto
import su.afk.yummy.tv.data.posts.dto.YaniPostDetailsResponseDto
import su.afk.yummy.tv.data.posts.dto.YaniPostSummaryDto
import su.afk.yummy.tv.data.posts.dto.YaniPostVoteResultDto
import su.afk.yummy.tv.data.posts.dto.YaniPostsResponseDto
import su.afk.yummy.tv.data.posts.network.YaniPostsApi
import su.afk.yummy.tv.domain.posts.model.PostAuthor
import su.afk.yummy.tv.domain.posts.model.PostCategory
import su.afk.yummy.tv.domain.posts.model.PostDetails
import su.afk.yummy.tv.domain.posts.model.PostReaction
import su.afk.yummy.tv.domain.posts.model.PostSummary
import su.afk.yummy.tv.domain.posts.model.PostVote
import su.afk.yummy.tv.domain.posts.model.RelatedPostAnime
import su.afk.yummy.tv.domain.posts.repository.PostsRepository
import javax.inject.Inject

class YaniPostsRepository @Inject constructor(
    private val api: YaniPostsApi,
    private val cache: UserScopedCache,
) : PostsRepository {
    override suspend fun categories() = cache.cached<YaniPostCategoriesResponseDto>(
        namespace = POST_CACHE_NAMESPACE,
        key = "categories",
        ttlMs = POST_CATEGORIES_TTL_MS,
    ) { api.categories() }.response.map(YaniPostCategoryDto::domain)

    override suspend fun posts(category: String?, sort: String, limit: Int, skip: Int) =
        cache.cached<YaniPostsResponseDto>(
            namespace = POST_CACHE_NAMESPACE,
            key = "feed:${category.orEmpty()}:$sort:$limit:$skip",
            ttlMs = POST_FEED_TTL_MS,
        ) { api.posts(category, sort, limit, skip) }.response.map(YaniPostSummaryDto::domain)

    override suspend fun details(postId: Int) = cache.cached<YaniPostDetailsResponseDto>(
        namespace = POST_CACHE_NAMESPACE,
        key = "detail:$postId",
        ttlMs = POST_DETAIL_TTL_MS,
    ) { api.details(postId) }.response.domain()

    override suspend fun vote(postId: Int, vote: PostVote): PostReaction {
        val result = api.vote(postId, vote.action).response.domain(vote)
        // Голос затрагивает только конкретный пост: чистим его деталь, а не весь namespace.
        cache.delete(POST_CACHE_NAMESPACE, "detail:$postId")
        return result
    }

    override suspend fun removeVote(postId: Int): PostReaction {
        val result = api.removeVote(postId).response.domain(PostVote.NONE)
        cache.delete(POST_CACHE_NAMESPACE, "detail:$postId")
        return result
    }
}

private const val POST_FEED_TTL_MS = 2 * 60 * 1000L
private const val POST_DETAIL_TTL_MS = 5 * 60 * 1000L
private const val POST_CATEGORIES_TTL_MS = 6 * 60 * 60 * 1000L
private const val POST_CACHE_NAMESPACE = "posts"

private fun YaniPostCategoryDto.domain() = PostCategory(id, title, uri)
private fun YaniPostAuthorDto.domain() = PostAuthor(
    id,
    nickname,
    (avatars?.full ?: avatars?.big ?: avatars?.small)?.toHttpsUrl(),
)

private fun YaniPostSummaryDto.domain() = PostSummary(
    id,
    title,
    previewImage?.toHttpsUrl(),
    contentPreview,
    user.domain(),
    category.domain(),
    createdAt,
)

private fun YaniPostVoteResultDto.domain(vote: PostVote) = PostReaction(likes, dislikes, vote)
private fun YaniPostDetailsDto.domain() = PostDetails(
    id = id,
    title = title,
    contentHtml = content,
    previewImageUrl = previewImage?.toHttpsUrl(),
    author = user.domain(),
    category = category.domain(),
    createdAt = createdAt,
    editedAt = editedAt,
    relatedAnime = animes.map {
        RelatedPostAnime(
            it.animeId,
            it.title,
            it.poster?.run { mega ?: huge ?: big ?: medium ?: small }?.toHttpsUrl(),
            it.year,
            it.rating?.average,
        )
    },
    reaction = PostReaction(
        likes.likes,
        likes.dislikes,
        PostVote.entries.firstOrNull { it.action == likes.vote } ?: PostVote.NONE),
    views = views,
    comments = comments,
)
