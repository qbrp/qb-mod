package org.qbrp.main.core.game.model

interface StateEntry {
    val state: State?
    fun requireState(): State = state ?: throw NullPointerException("Компонент $this не помещен в какое-либо состояние, и оно не может быть передано")
}