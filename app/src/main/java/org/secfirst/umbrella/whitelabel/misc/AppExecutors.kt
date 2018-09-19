package org.secfirst.umbrella.whitelabel.misc

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import org.jetbrains.anko.AnkoContext
import kotlin.coroutines.experimental.CoroutineContext

const val THREAD_COUNT = 3

/**
 * Global executor pools for the whole application.
 */
open class AppExecutors {
    companion object {
        val ioContext: CoroutineContext
            get() = DefaultDispatcher

        val networkContext: CoroutineContext
            get() = newFixedThreadPoolContext(THREAD_COUNT, "networkIO")

        val uiContext: CoroutineContext
            get() = UI
    }
}

