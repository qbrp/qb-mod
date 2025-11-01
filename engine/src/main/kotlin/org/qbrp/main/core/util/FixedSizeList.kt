package org.qbrp.main.core.util

class FixedSizeList<T>(private val maxSize: Int) {
    private val list = ArrayDeque<T>()

    fun add(item: T) {
        if (list.size >= maxSize) {
            list.removeFirst()
        }
        list.addLast(item)
    }

    fun contains(item: T) = list.contains(item)

    fun toList(): List<T> = list.toList()
}