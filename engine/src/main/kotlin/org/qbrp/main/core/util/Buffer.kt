package org.qbrp.main.core.util

import java.util.concurrent.atomic.AtomicReference

interface Buffer<T> {
    fun read(): T
    fun write(block: T.() -> Unit)
    fun swap()
}

fun <T> Buffer(initial: T) = BufferImpl(initial)

class BufferImpl<T>(initial: T) : Buffer<T> {
    private var front = AtomicReference(initial)
    private var back = AtomicReference(initial)

    override fun read(): T = front.get()

    override fun write(block: (T) -> Unit) = block(back.get())

    override fun swap() {
        val tmp = front.get()
        front.set(back.get())
        back.set(tmp)
    }
}