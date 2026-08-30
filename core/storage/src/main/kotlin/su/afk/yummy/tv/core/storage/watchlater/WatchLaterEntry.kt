package su.afk.yummy.tv.core.storage.watchlater

import androidx.room.Entity
import androidx.room.Index

/**
 * Серия, отложенная пользователем «на потом». Список чисто локальный — на сервере такого
 * понятия нет, поэтому вместе с ключом храним и метаданные для карточки, чтобы экран
 * отложенных рисовался без похода в сеть.
 *
 * [episode] хранится нормализованным (см. `episodeGroupKey`): разные озвучки присылают
 * номер по-разному («01» против «1»), иначе одна и та же серия попадёт в список дважды.
 */
@Entity(
    tableName = "watch_later",
    primaryKeys = ["animeId", "episode"],
    indices = [
        Index(value = ["addedAt"], name = "index_watch_later_addedAt"),
    ],
)
data class WatchLaterEntry(
    val animeId: Int,
    val episode: String,
    val animeTitle: String = "",
    val posterUrl: String = "",
    val screenshotUrl: String = "",
    val addedAt: Long = System.currentTimeMillis(),
)
