package su.afk.yummy.tv.core.utils.coroutines

import kotlinx.coroutines.CancellationException

suspend fun <T> runSuspendCatching(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        Result.failure(error)
    }
