package com.simple.wallet.utils.exts

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.simple.core.utils.extentions.resumeActive
import com.simple.coreapp.utils.extentions.isActive
import com.simple.coreapp.utils.extentions.offerActive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Flow<T>.debounceInternal(timeOut: Long): Flow<T> {

    var time = 0L

    return flatMapLatest { data ->

        channelFlow {

            delay(timeOut - (System.currentTimeMillis() - time))

            time = System.currentTimeMillis()

            offerActive(data)

            awaitClose {
            }
        }
    }
}

suspend fun <T> Flow<T>.launchCollect(coroutineScope: CoroutineScope, collector: FlowCollector<T>) = coroutineScope.launch {

    this@launchCollect.collect(collector)
}

fun <T> Flow<T>.launchCollect(coroutineScope: CoroutineScope, context: CoroutineContext = EmptyCoroutineContext, start: CoroutineStart = CoroutineStart.DEFAULT, collector: FlowCollector<T>) = coroutineScope.launch(context, start) {

    this@launchCollect.collect(collector)
}


fun CoroutineScope.launchSchedule(timeSchedule: Long, context: CoroutineContext = EmptyCoroutineContext, start: CoroutineStart = CoroutineStart.DEFAULT, block: suspend CoroutineScope.() -> Unit): Job = launch(context, start) {

    scheduleIfResume {

        block.invoke(this)

        timeSchedule
    }
}

fun CoroutineScope.launchSchedule(context: CoroutineContext = EmptyCoroutineContext, start: CoroutineStart = CoroutineStart.DEFAULT, block: suspend CoroutineScope.() -> Long): Job = launch(context, start) {

    scheduleIfResume {

        block.invoke(this)
    }
}

suspend inline fun scheduleIfResume(crossinline cause: () -> Boolean = { true }, crossinline block: suspend () -> Long) {

    while (cause() && isActive()) {

        waitResume()

        delay(block())
    }
}

suspend fun waitResume() = withContext(Dispatchers.Main.immediate) {

    val lifecycle = ProcessLifecycleOwner.get().lifecycle

    if (lifecycle.currentState === Lifecycle.State.RESUMED) {

        return@withContext
    }


    var lifecycleEventObserver: LifecycleEventObserver? = null

    try {

        suspendCancellableCoroutine<Boolean> { continuation ->

            lifecycleEventObserver = LifecycleEventObserver { a, event ->

                if (event == Lifecycle.Event.ON_RESUME) {

                    continuation.resumeActive(true)
                }
            }

            lifecycle.addObserver(lifecycleEventObserver!!)
        }
    } finally {

        lifecycleEventObserver?.let { lifecycle.removeObserver(it) }
    }
}
