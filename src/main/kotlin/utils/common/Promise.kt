
package utils.common

fun <T> ArrayList<T>.queue(v: T) = add(0,v)
fun <T> ArrayList<T>.dequeue(): T = removeAt(size-1)
fun <T> ArrayList<T>.isNotEmpty() = !isEmpty()


class Promise<T : Any?>(var parent: Promise<*>?) {
    private var resolved: Boolean = false
    private var resolvedValue: T? = null
    private var resolvedException: Throwable? = null
    private val callbacks = ArrayList<(T) -> Any>()
    private val failcallbacks = ArrayList<(Throwable) -> Any>()


    class Deferred<T : Any?> {
        val promise: Promise<T> = Promise<T>(null)

        fun resolve(value: T) {
            this.promise.resolve(value)
        }

        fun progress(value: Double) {
            this.promise.progress(value)
        }

        fun reject(value: Throwable) {
            this.promise.reject(value)
        }
    }

    private constructor(parent: Promise<*>?, value: Throwable):this(null)
    {
        resolved = true
        resolvedException = value
    }


    companion object {

        fun setAsyncExecutor(executor: ((() -> Unit) -> Unit))
        {
            eventLoop =executor
        }

        internal var eventLoop: ((() -> Unit) -> Unit) = { launch{it()} }

        fun <T : Any> invoke(callback: (resolve: (value: T) -> Unit, reject: (error: Throwable) -> Unit) -> Unit): Promise<T> {
            val deferred = Deferred<T>()
            callback({ deferred.resolve(it) }, { deferred.reject(it) })
            return deferred.promise
        }

        fun <T : Any> create(callback: (resolve: (value: T) -> Unit, reject: (error: Throwable) -> Unit) -> Unit): Promise<T> {
            val deferred = Deferred<T>()
            callback({ deferred.resolve(it) }, { deferred.reject(it) })
            return deferred.promise
        }

        fun <T : Any> sequence(vararg promises: () -> Promise<T>): Promise<List<T>> {
            return sequence(promises.toList())
        }

        fun <T : Any> sequence(promises: Iterable<() -> Promise<T>>): Promise<List<T>> {
            val items = promises.toMutableList()
            if (items.size == 0) return resolved(listOf<T>())
            val out = ArrayList<T>(items.size)
            val deferred = Deferred<List<T>>()
            fun step() {
                if (items.isEmpty()) {
                    deferred.resolve(out)
                } else {
                    val promiseGenerator = items.removeAt(0)
                    val promise = promiseGenerator()
                    promise.then {
                        out.add(it)
                        step()
                    }.fail {
                        deferred.reject(it)
                    }
                }
            }
            eventLoop { step() }
            return deferred.promise
        }

        fun chain(): Promise<Unit> = resolved(Unit)

        fun <T : Any> resolved(value: T): Promise<T> {
            val deferred = Deferred<T>()
            deferred.resolve(value)
            return deferred.promise
        }

        fun <T : Any> rejected(value: Throwable): Promise<T> {
            return Promise(null,value)
        }

        fun <T : Any> all(vararg promises: Promise<T>): Promise<List<T>> {
            return all(promises.toList())
        }

        fun <T : Any> all(promises: Iterable<Promise<T>>): Promise<List<T>> {
            val promiseList = promises.toList()
            var count = 0
            val total = promiseList.size

            val out = arrayListOf<T?>()
            val deferred = Deferred<List<T>>()
            for (n in 0 until total) out.add(null)

            fun checkDone() {
                if (count >= total) {
                    deferred.resolve(out.map { it!! })
                }
            }

            promiseList.indices.forEach {
                val index = it
                val promise = promiseList[index]
                promise.then {
                    out[index] = it
                    count++
                    checkDone()
                }
            }

            checkDone()

            return deferred.promise
        }

        fun <T : Any> forever(): Promise<T> {
            return Deferred<T>().promise
        }

        fun <T : Any> any(vararg promises: Promise<T>): Promise<T> {
            val deferred = Deferred<T>()
            for (promise in promises) {
                promise.then { deferred.resolve(it) }.fail { deferred.reject(it) }
            }
            return deferred.promise
        }
    }

    private fun resolve(value: T) {
        if (resolved) return
        resolved = true
        resolvedValue = value
        parent = null
        flush()
    }

    private fun reject(value: Throwable) {
        if (resolved) return
        resolved = true
        resolvedException = value
        parent = null

        // @TODO: Check why this fails!
        if (failcallbacks.isEmpty() && callbacks.isEmpty()) {
            println("Promise.reject(): Not capturated: $value")
            //throw value //A rejected promise without a .then or .fail would cause an exception
        }

        flush()
    }

    private fun progress(value: Double) {

    }

    private fun flush() {
        if (!resolved || (callbacks.isEmpty() && failcallbacks.isEmpty())) return

        val resolvedValue = this.resolvedValue
        if (resolvedValue != null) {
            while (callbacks.isNotEmpty()) {
                val callback = callbacks.dequeue()
                eventLoop{
                    callback(resolvedValue)
                }
            }
        } else if (resolvedException != null) {
            while (failcallbacks.isNotEmpty()) {
                val failcallback = failcallbacks.dequeue()
                eventLoop{
                    failcallback(resolvedException!!)
                }
            }
        }
        if (resolvedValue==null && resolvedException == null) {
            //printExceptionAsWarn(Exception("Promise was accepted or rejected with a not valid (null) value"))

        }
    }

    public fun <T2 : Any?> pipe(callback: (value: T) -> Promise<T2>): Promise<T2> {
        try {
            val out = Promise<T2>(this)
            this.failcallbacks.queue {
                out.reject(it)
            }
            this.callbacks.queue({
                callback(it)
                        .then { out.resolve(it) }
                        .fail { out.fail { it } }
            })
            return out
        } finally {
            flush()
        }
    }

    public fun <T2 : Any?> then(callback: (value: T) -> T2): Promise<T2> {
        try {
            val out = Promise<T2>(this)
            this.failcallbacks.queue {
                out.reject(it)
            }
            this.callbacks.queue {
                try {
                    out.resolve(callback(it))
                } catch (t: Throwable) {
                    println("then catch:$t")
                    println(t.message)
                    out.reject(t)
                }
            }
            return out
        } finally {
            flush()
        }
    }

    public fun <T2 : Any?> fail(failcallback: (throwable: Throwable) -> T2): Promise<T2> {
        try {
            val out = Promise<T2>(this)
            this.failcallbacks.queue {
                try {
                    out.resolve(failcallback(it))
                } catch (t: Throwable) {
                    println("fail catch:$t")
                    println(t.message)
                    out.reject(t)
                }
            }
            return out
        } finally {
            flush()
        }
    }

    fun always(callback: () -> Unit): Promise<T> {
        then { callback() }.fail { callback() }
        return this
    }
}