package su.afk.yummy.tv.core.utils.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/** Fire-and-forget scope для собственного жизненного цикла компонента: SupervisorJob + Dispatchers.IO. */
fun ioScope(context: CoroutineContext = EmptyCoroutineContext): CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.IO + context)

/** Fire-and-forget scope для собственного жизненного цикла компонента: SupervisorJob + Dispatchers.Default. */
fun defaultScope(context: CoroutineContext = EmptyCoroutineContext): CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.Default + context)
