package org.qbrp.main.engine.physics.system.entity

interface MutableCompoundBody : Body {
    fun addShape(shape: Shape, transform: Transform): Int
    fun removeShape(index: Int)
}