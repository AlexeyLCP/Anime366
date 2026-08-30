package su.afk.yummy.tv.data.collection.network

import su.afk.yummy.tv.data.collection.dto.YaniCollectionDetailResponseDto
import su.afk.yummy.tv.data.collection.dto.YaniCollectionListResponseDto
import su.afk.yummy.tv.data.collection.dto.YaniCollectionMutationResponseDto
import su.afk.yummy.tv.data.collection.dto.YaniCollectionVoteBodyDto
import su.afk.yummy.tv.data.collection.dto.YaniCollectionVoteResponseDto
import su.afk.yummy.tv.data.collection.dto.YaniCreateCollectionBodyDto
import su.afk.yummy.tv.data.collection.dto.YaniCreateCollectionResponseDto
import su.afk.yummy.tv.data.collection.dto.YaniUpdateCollectionBodyDto

class YaniCollectionApi(
    @Suppress("unused") private val clientProvider: su.afk.yummy.tv.core.network.yani.YaniHttpClientProvider,
) {
    suspend fun getCollection(id: Int): YaniCollectionDetailResponseDto = YaniCollectionDetailResponseDto()

    suspend fun getCollections(limit: Int, offset: Int): YaniCollectionListResponseDto =
        YaniCollectionListResponseDto()

    suspend fun createCollection(body: YaniCreateCollectionBodyDto): YaniCreateCollectionResponseDto =
        YaniCreateCollectionResponseDto()

    suspend fun updateCollection(
        id: Int,
        body: YaniUpdateCollectionBodyDto,
    ): YaniCollectionMutationResponseDto = YaniCollectionMutationResponseDto()

    suspend fun deleteCollection(id: Int): YaniCollectionMutationResponseDto =
        YaniCollectionMutationResponseDto()

    suspend fun voteCollection(id: Int, body: YaniCollectionVoteBodyDto): YaniCollectionVoteResponseDto =
        YaniCollectionVoteResponseDto()

    suspend fun removeCollectionVote(id: Int): YaniCollectionVoteResponseDto =
        YaniCollectionVoteResponseDto()
}
