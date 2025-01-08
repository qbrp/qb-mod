package org.qbrp.plasmo.model.priority

object Priorities {
    private val list = mutableListOf<Priority>(
        Priority("player"),
        Priority("world")
    )
    fun getIndex(priority: Priority): Int = list.indexOf(priority)
    fun lowest() = list.last()
    fun highest() = list.first()
}