package su.afk.yummy.tv.data.posts.network

import su.afk.yummy.tv.data.posts.dto.YaniPostCategoriesResponseDto
import su.afk.yummy.tv.data.posts.dto.YaniPostDetailsResponseDto
import su.afk.yummy.tv.data.posts.dto.YaniPostVoteResponseDto
import su.afk.yummy.tv.data.posts.dto.YaniPostsResponseDto
import javax.inject.Inject

class YaniPostsApi @Inject constructor() {
    suspend fun categories(): YaniPostCategoriesResponseDto = YaniPostCategoriesResponseDto()

    suspend fun posts(category: String?, sort: String, limit: Int, skip: Int): YaniPostsResponseDto =
        YaniPostsResponseDto()

    suspend fun details(postId: Int): YaniPostDetailsResponseDto = YaniPostDetailsResponseDto()

    suspend fun vote(postId: Int, action: Int): YaniPostVoteResponseDto = YaniPostVoteResponseDto()

    suspend fun removeVote(postId: Int): YaniPostVoteResponseDto = YaniPostVoteResponseDto()
}
