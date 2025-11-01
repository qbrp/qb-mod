package org.qbrp.main.engine.containers.components

sealed class Insertion {
    data class Hole(val length: Float, val width: Float, val height: Float): Insertion()
}