package com.github.movins.event.kt;

import kotlinx.coroutines.*

private object CoroutineUtil {
    init {
        ThreadPool.checkSingleThreadPool()
    }

    val dispatchersSingle = ThreadPool.singleThreadExecutor!!.asCoroutineDispatcher()
    val dispatchersCache = ThreadPool.getCacheThreadExecutor().asCoroutineDispatcher()
}

/**
 * 单例协程上下文
 */
val Dispatchers.Single: ExecutorCoroutineDispatcher
    get() = CoroutineUtil.dispatchersSingle

/**
 * io和cpu线程都放在这里,可以减少等待的线程
 */
val Dispatchers.Cache: ExecutorCoroutineDispatcher
    get() = CoroutineUtil.dispatchersCache

//在主线程后同步回调
suspend fun <T> withMain(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Main.immediate, block)
//在子线程后同步回调
suspend fun <T> withIO(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Cache, block)
//在单例子线程后同步回调
suspend fun <T> withSingle(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Single, block)
