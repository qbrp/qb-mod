package org.qbrp.main.mechanics.tooltip

data class TooltipColorProfile(
    val background: Int = VANILLA_BACKGROUND_COLOR,
    val borderStartY: Int = VANILLA_START_Y_BORDER_COLOR,
    val borderEndY: Int = VANILLA_END_Y_BORDER_COLOR
) {
    companion object {
        private const val VANILLA_BACKGROUND_COLOR: Int = 0xF0100010.toInt()  // -267386864
        private const val VANILLA_START_Y_BORDER_COLOR: Int = 0x505000FF.toInt() // 1347420415
        private const val VANILLA_END_Y_BORDER_COLOR: Int = 0x5028007F.toInt()   // 1344798847

        private const val GRAY_BACKGROUND_COLOR: Int = 0xF0222222.toInt()   // полупрозрачный тёмно-серый
        private const val GRAY_START_Y_BORDER_COLOR: Int = 0x50666666.toInt() // верхняя/начальная граница
        private const val GRAY_END_Y_BORDER_COLOR: Int = 0x50333333.toInt()   // нижняя/конечная граница

        // Готовые профили
        val VANILLA = TooltipColorProfile(
            background = VANILLA_BACKGROUND_COLOR,
            borderStartY = VANILLA_START_Y_BORDER_COLOR,
            borderEndY = VANILLA_END_Y_BORDER_COLOR
        )

        val GRAY = TooltipColorProfile(
            background = GRAY_BACKGROUND_COLOR,
            borderStartY = GRAY_START_Y_BORDER_COLOR,
            borderEndY = GRAY_END_Y_BORDER_COLOR
        )
    }
}