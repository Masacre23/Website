package utils.common



open class Signal<T> {
    inner protected class Node(val value: (T) -> Unit) {
        fun dispose() {
            handlers.remove(this)
            onces.remove(this)
        }
    }
    inner protected class DoNode(val value: (T) -> Boolean){
        fun dispose() {
            donces.remove(this)
        }
    }

    protected val handlers = mutableSetOf<Node>()
    protected val onces = mutableSetOf<Node>()
    protected val donces = mutableSetOf<DoNode>()

    fun add(handler: (T) -> Unit) {
        val node = Node(handler)
        handlers.add(node)
    }

    open fun dispatch(value: T) {
        for (handler in handlers) handler.value.invoke(value)
        val dispatchedOnces = onces.toList()
        for (handler in dispatchedOnces) handler.value.invoke(value)
        onces.removeAll(dispatchedOnces)

        donces.removeAll(donces.toList().filter { it.value.invoke(value) })
    }

    fun hasHandlers() = (handlers.size>0 || onces.size >0)

    fun dispose() {
        handlers.clear()
        removeOnces()
    }

    fun once(handler: (T) -> Unit) {
        val node = Node(handler)
        onces.add(node)
    }
    fun removeOnces() {
        onces.clear()
    }
}

fun Signal<Unit>.dispatch() = dispatch(Unit)

