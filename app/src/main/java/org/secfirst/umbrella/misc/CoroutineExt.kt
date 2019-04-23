package org.secfirst.umbrella.misc

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

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
fun launchSilent(context: CoroutineContext = Dispatchers.Default,
                 block: suspend CoroutineScope.() -> Unit) {

    GlobalScope.launch(context, CoroutineStart.DEFAULT, block)
}

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
fun <T> runBlockingSilent(context: CoroutineContext = EmptyCoroutineContext, block: suspend CoroutineScope.() -> T) {
    runBlocking(context, block)
}

//suspend fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> {
//    return async(CommonPool) { block() }
//}

//suspend fun <T> asyncAwait(block: suspend CoroutineScope.() -> T): T {
//    return async(block).await()
//}