package utils.common

import kotlin.coroutines.*

fun launch(block: suspend () -> Unit) = launch(block, EmptyCoroutineContext)
fun launch(block: suspend () -> Unit, context: CoroutineContext) = block.startCoroutine(StandaloneCoroutine(context))

fun <T: Any> async(block: suspend () -> T ) = Promise.create<T> { resolve, reject ->
    block.startCoroutine(completion = object : Continuation<T> {
        override val context: CoroutineContext = EmptyCoroutineContext

        override fun resumeWith(result: Result<T>) {
            try{
                resolve(result.getOrThrow())
            } catch (e:Throwable){
                reject(e)
            }
        }
    })
}


private class StandaloneCoroutine(override val context: CoroutineContext): Continuation<Unit> {

    override fun resumeWith(result: Result<Unit>) {
        try{
            result.getOrThrow()
        } catch (exception:Throwable){
            println("Coroutine failed: ${exception.message}")
        }
    }
}

internal external fun setTimeout(callback: (args: Any) -> Unit, ms: Number): Number = definedExternally

suspend fun sleep(delay: TimeSpan): Unit = suspendCoroutine { cont -> setTimeout({
    cont.resume(Unit)
}, delay.milliseconds)
}