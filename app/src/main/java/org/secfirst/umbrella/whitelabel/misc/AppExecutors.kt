package org.secfirst.umbrella.whitelabel.misc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlin.coroutines.CoroutineContext

const val THREAD_COUNT = 3

/**
 * Global executor pools for the whole application.
 */
open class AppExecutors {
    companion object {
        val ioContext: kotlin.coroutines.CoroutineContext
            get() = Dispatchers.Default

        val networkContext: CoroutineContext
            get() = newFixedThreadPoolContext(THREAD_COUNT, "")

        val uiContext: CoroutineContext
            get() = Dispatchers.Main
    }
}

