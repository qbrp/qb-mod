package org.qbrp.main.engine.containers.v2.components

sealed class Insertion {
    data class Hole(val length: Float, val width: Float, val height: Float): Insertion()
}