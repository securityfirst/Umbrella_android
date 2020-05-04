package org.secfirst.umbrella.misc

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Global executor pools for the whole application.
 */
open class AppExecutors {
    companion object {
        val ioContext: CoroutineContext
            get() = Dispatchers.IO

        val uiContext: CoroutineContext
            get() = Dispatchers.Main
    }
}

