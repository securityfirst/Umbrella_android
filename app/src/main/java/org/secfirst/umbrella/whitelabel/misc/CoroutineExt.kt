package org.secfirst.umbrella.whitelabel.misc

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Equivalent to [launch] but return [Unit] instead of [Job].
 *
 * Mainly for usage when you want to lift [launch] to return. Example:
 *
 * ```
 * override fun loadData() = launchSilent {
 *     // code
 * }
 * ```
 */
fun launchSilent(
        context: CoroutineContext = Dispatchers.Default,
        block: suspend CoroutineScope.() -> Unit
) {
    runBlocking(context, block)
}

/**
 * Equivalent to [runBlocking] but return [Unit] instead of [T].
 *
 * Mainly for usage when you want to lift [runBlocking] to return. Example:
 *
 * ```
 * override fun loadData() = runBlockingSilent {
 *     // code
 * }
 * ```
 */
fun <T> runBlockingSilent(context: CoroutineContext = Dispatchers.Default, block: suspend CoroutineScope.() -> T) {
    runBlocking(context, block)
}

//suspend fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> {
//    return async(CommonPool) { block() }
//}
//
//suspend fun <T> asyncAwait(block: suspend CoroutineScope.() -> T): T {
//    return async(block).await()
//}