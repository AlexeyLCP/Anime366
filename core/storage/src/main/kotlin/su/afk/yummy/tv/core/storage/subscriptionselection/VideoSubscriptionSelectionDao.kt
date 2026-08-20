package su.afk.yummy.tv.core.storage.subscriptionselection

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VideoSubscriptionSelectionDao {

    @Query(
        "SELECT * FROM video_subscription_selection " +
                "WHERE userId = :userId AND animeId = :animeId ORDER BY updatedAt"
    )
    suspend fun getForAnime(userId: Int, animeId: Int): List<VideoSubscriptionSelectionEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entry: VideoSubscriptionSelectionEntry)

    @Query(
        "DELETE FROM video_subscription_selection " +
                "WHERE userId = :userId AND animeId = :animeId " +
                "AND playerKey = :playerKey AND dubbingKey = :dubbingKey"
    )
    suspend fun delete(userId: Int, animeId: Int, playerKey: String, dubbingKey: String)

    @Query(
        "DELETE FROM video_subscription_selection " +
                "WHERE userId = :userId AND animeId = :animeId AND playerKey = :playerKey"
    )
    suspend fun deleteForPlayer(userId: Int, animeId: Int, playerKey: String)

    @Query("DELETE FROM video_subscription_selection WHERE userId = :userId")
    suspend fun deleteForUser(userId: Int)
}
