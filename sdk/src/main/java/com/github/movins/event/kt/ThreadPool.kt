package com.github.movins.event.kt;

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

/**
 * 设置协程异常策略的上下文元素
 */
object CoroutineExceptionHandlerWithReleaseUploadAndDebugThrow : CoroutineExceptionHandler {
    override val key: CoroutineContext.Key<*> = CoroutineExceptionHandler

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        if (exception !is CancellationException) {//如果是SupervisorJob就不会传播取消异常,而Job会传播
            exception.upload()
        }
    }
}

/**
 * effect : 线程池工厂
 *          submit比execute方法好在,都能执行任务,并且可以拿到返回值,可以取消,可以捕获内部的异常;
 *          返回值还有异常都是通过返回值.get()来操作的,调用.get()后就会跑在当前线程?如果任务没执行完就会阻塞当前线程;
 *          取消就是返回值.cancel()
 * warning: 目前使用自定义替换原生api,参考:https://mp.weixin.qq.com/s/kE-g1dsZt6fSCK7u60frTw
 */
object ThreadPool {
    var singleThreadExecutor: ExecutorService? = null
    private var cacheThreadExecutor: ExecutorService? = null
    fun getCacheThreadExecutor(): ExecutorService {
        checkCacheThreadPool()
        return cacheThreadExecutor!!
    }

    private var fixedThreadExecutor: ExecutorService? = null
    private var timeThreadExecutor: ScheduledExecutorService? = null

    /**
     * 线程类型
     */
    enum class ThreadType {
        /**
         * 单例线程,选择这种方式会按照启动的先后顺序串行执行
         */
        SINGLE,

        /**
         * 缓存线程池,选择这种方式会和其他任务并行执行,且无上限
         */
        CACHE,

        /**
         * 固定长度的线程池,达到设定的线程数量后,后面的任务等待空闲线程执行完毕后再执行
         */
        FIXED,

        /**
         * 跑在主线程中
         */
        MAIN
    }

    /**
     * 提交任务到单例线程池,如果有多个任务同时提交,则会按照顺序线性执行
     * 需要线性执行的任务或一般不会交叉执行的任务
     */
    fun submitToSingleThreadPool(runnable: EmptyListener): Future<*> {
        checkSingleThreadPool()
        return singleThreadExecutor!!.submit(runnable)
    }

    fun submitToSingleThreadPool(runnable: Runnable): Future<*> {
        checkSingleThreadPool()
        return singleThreadExecutor!!.submit(runnable)
    }

    /**
     * 提交任务到缓存线程池,可以接收无限多任务,在执行时如果有空闲线程则回收利用,没有则新建
     * 适用于大量的需要立即处理并且耗时较少的任务
     */
    fun submitToCacheThreadPool(runnable: EmptyListener): Future<*> {
        checkCacheThreadPool()
        return cacheThreadExecutor!!.submit(runnable)
    }

    fun submitToCacheThreadPool(runnable: Runnable): Future<*> {
        checkCacheThreadPool()
        return cacheThreadExecutor!!.submit(runnable)
    }

    /**
     * 提交任务到定长线程池,可以接收多任务,如果超过了长度,则等待线程空闲后依次执行
     */
    @Deprecated("尽量不要使用此方法, 如果使用请使用 [submitToTimeThreadPoolNoTime],因为功能较少,且同时使用多个线程池比较浪费")
    fun submitToFixedThreadPool(runnable: EmptyListener): Future<*> {
        checkFixedThreadPool()
        return fixedThreadExecutor!!.submit(runnable)
    }

    /**
     * 提交任务到周期定长线程池,可以接收多任务,如果超过了长度,则等待线程空闲后依次执行
     */
    fun submitToTimeThreadPoolNoTime(runnable: EmptyListener): Future<*> {
        checkTimeThreadPool()
        return timeThreadExecutor!!.submit(runnable)
    }

    /**
     * 周期执行任务,中断任务可以用:返回值.cancel(true)
     *
     * @param time 间隔时间
     */
    fun submitToTimeThreadPool(runnable: EmptyListener, time: Long): ScheduledFuture<*> {
        checkTimeThreadPool()
        return timeThreadExecutor!!.schedule(runnable, time, TimeUnit.MILLISECONDS)
    }

    /**
     * 周期执行任务,中断任务可以用:返回值.cancel(true)
     *
     * @param time      间隔时间
     * @param firstTime 第一次执行时间
     */
    fun submitToTimeThreadPool(runnable: EmptyListener, time: Long, firstTime: Long): ScheduledFuture<*> {
        checkTimeThreadPool()
        return timeThreadExecutor!!.scheduleAtFixedRate(runnable, firstTime, time, TimeUnit.MILLISECONDS)
    }

    //检查单例线程池
    fun checkSingleThreadPool() {
        if (singleThreadExecutor == null)
            singleThreadExecutor = createSingleThreadPool()
    }

    init {
        checkSingleThreadPool()
    }

    //检查缓存线程池,可以同时跑100个线程,其他线程在后面等待,可以根据项目性质调整大小
    private fun checkCacheThreadPool() {
        if (cacheThreadExecutor == null)
            cacheThreadExecutor = createCacheThreadPool()
    }

    //创建单例线程池
    private fun createSingleThreadPool(): ThreadPoolExecutor = /*Executors.newSingleThreadExecutor()*/
            MThreadPool(1, 1,
                    15L, TimeUnit.SECONDS,
                    LinkedBlockingQueue())

    //创建缓存线程池,可以同时跑cpu线程个数个线程,其他线程在后面等待,可以根据项目性质调整大小
    private fun createCacheThreadPool(): ThreadPoolExecutor = /*Executors.newCachedThreadPool()*/
            MThreadPool(
                    1,//corePoolSize：即使处于空闲状态依然保留在池中的线程数（核心），除非设置了allowCoreThreadTimeOut，当 allowCoreThreadTimeOut 设置为 true 时，核心线程超时后也会被销毁。
                    maxOf(16, (Runtime.getRuntime().availableProcessors() * 2)),//maximumPoolSize：池中允许的最大线程数；比cpu核心数多一个,io密集型适合cpu核心数*2,cpu密集型适合cpu核心数,通用适合cpu核心数+1或cpu核心数*1.5
                    30L,//keepAliveTime：线程池空闲时线程的存活时长；
                    TimeUnit.SECONDS,//unit：keepAliveTime的时间单位；
                    SynchronousQueue(),//workQueue：存放任务的队列:
                    //      ArrayBlockingQueue：有界队列，一个用数组实现的有界阻塞队列，按FIFO排序量。
                    //      LinkedBlockingQueue：可设置容量的队列，基于链表结构的阻塞队列，按FIFO排序任务，容量可以选择进行设置，不设置的话，将是一个无边界的阻塞队列，最大长度为Integer.MAX_VALUE，吞吐量通常要高于ArrayBlockingQuene；newFixedThreadPool线程池使用了这个队列（这里有坑）。
                    //      DelayQueue：延迟队列，是一个任务定时周期的延迟执行的队列。根据指定的执行时间从小到大排序，否则根据插入到队列的先后排序。newScheduledThreadPool线程池使用了这个队列。
                    //      PriorityBlockingQueue：优先级队列，具有优先级的无界阻塞队列。
                    //      SynchronousQueue：同步队列，一个不存储元素的阻塞队列，每个插入操作必须等到另一个线程调用移除操作，否则插入操作一直处于阻塞状态，吞吐量通常要高于LinkedBlockingQuene，newCachedThreadPool线程池使用了这个队列。
                    //threadFactory：执行程序创建新线程时要使用的工厂。
                    //handler:拒绝策略,在队列（workQueue）和线程池达到最大线程数（maximumPoolSize）均满时仍有任务的情况下的处理方式。默认拒绝策略会直接报异常:
                    //      ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException,线程池的默认拒绝策略
                    //      ThreadPoolExecutor.DiscardPolicy:丢弃任务但不抛出异常
                    //      ThreadPoolExecutor.DiscardOldestPolicy:丢弃队列前的任务并重新提交被拒绝的任务
                    //      ThreadPoolExecutor.CallerRunsPolicy:由本次提交任务的线程来处理该任务
                    //设置allowCoreThreadTimeout=true（默认false）时，核心线程会超时关闭
                    object : ThreadFactory {
                        private val group = System.getSecurityManager()?.threadGroup
                                ?: Thread.currentThread().threadGroup
                        private val threadNumber = AtomicInteger(1)
                        private val exceptionHandler = Thread.UncaughtExceptionHandler { t: Thread, e: Throwable ->
                            e.upload()
                        }

                        //线程池工厂
                        override fun newThread(r: Runnable?): Thread {
                            val t = Thread(group, r, "TP${threadNumber.getAndIncrement()}ByLt", 0)
                            if (t.isDaemon) t.isDaemon = false
                            if (t.priority != Thread.NORM_PRIORITY) t.priority = Thread.NORM_PRIORITY
                            t.uncaughtExceptionHandler = exceptionHandler
                            return t
                        }
                    }
            ) { r, executor ->//线程池已满的策略
                "线程池已满 ${r?.javaClass?.name}".e()
                if (executor?.isShutdown == false)
                    submitToTimeThreadPool({//设置如果任务已满,就在一段时间后重新提交
                        if (!executor.isShutdown)
                            executor.execute(r)
                    }, 50)
            }

    //检查定长线程池
    private fun checkFixedThreadPool() {
        if (fixedThreadExecutor == null)
            fixedThreadExecutor = Executors.newFixedThreadPool(3)
    }

    //检查周期定长线程池
    private fun checkTimeThreadPool() {
        if (timeThreadExecutor == null)
            timeThreadExecutor = Executors.newScheduledThreadPool(3)
    }

    //可以捕获到submit方式的异常的线程池
    class MThreadPool(corePoolSize: Int, maximumPoolSize: Int, keepAliveTime: Long, unit: TimeUnit, workQueue: BlockingQueue<Runnable>, threadFactory: ThreadFactory = Executors.defaultThreadFactory(), handler: RejectedExecutionHandler = AbortPolicy()) : ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler) {
        init {
            allowCoreThreadTimeOut(true)
        }

        override fun afterExecute(r: Runnable?, t: Throwable?) {
            if (t != null)
                CoroutineExceptionHandlerWithReleaseUploadAndDebugThrow.handleException(CoroutineName(Thread.currentThread().name), t)
            try {
                if (r is Future<*>) {
                    r.get()
                }
            } catch (e: Throwable) {
                val cause = e.cause
                if (cause != null)
                    CoroutineExceptionHandlerWithReleaseUploadAndDebugThrow.handleException(CoroutineName(Thread.currentThread().name), cause)
            }
        }
    }
}
