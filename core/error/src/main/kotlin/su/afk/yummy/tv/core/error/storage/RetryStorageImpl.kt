package su.afk.yummy.tv.core.error.storage

import su.afk.yummy.tv.core.error.api.RetryStorage
import javax.inject.Inject

internal class RetryStorageImpl @Inject constructor() : RetryStorage {

    private val storage = mutableMapOf<String, () -> Unit>()

    override fun put(key: String, action: () -> Unit) {
        storage[key] = action
    }

    override fun consume(key: String): (() -> Unit)? {
        val action = storage[key]
        storage.remove(key)
        return action
    }

    override fun remove(key: String) {
        storage.remove(key)
    }
}
