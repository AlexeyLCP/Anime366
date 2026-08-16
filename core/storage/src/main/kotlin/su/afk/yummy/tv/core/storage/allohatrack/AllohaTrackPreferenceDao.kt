package su.afk.yummy.tv.core.storage.allohatrack

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AllohaTrackPreferenceDao {

    @Query(
        "SELECT * FROM alloha_track_preference " +
                "WHERE animeId = :animeId AND dubbing = :dubbing AND player = :player"
    )
    suspend fun get(animeId: Int, dubbing: String, player: String): AllohaTrackPreferenceEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entry: AllohaTrackPreferenceEntry)
}
