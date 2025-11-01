package org.qbrp.main.engine.props.components

/**
 *  Полные габариты куба, в метрах
 *  @see org.qbrp.main.engine.physics.system.jolt.JoltShapeFactory.boxDimensions Преобразование в половинную форму
 *  **/
interface BoxDimensions {
    val length: Double
    val width: Double
    val height: Double

    fun laying(): Dimensions {
        val dims = listOf(length, width, height).sortedDescending()
        val (layingLength, layingWidth, layingHeight) = dims
        return Dimensions(layingLength, layingWidth, layingHeight)
    }

    fun requireLay(): Boolean {
        return laying() != this
    }

    fun randomSide() = listOf(length, width, height).random()
}