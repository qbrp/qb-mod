package org.qbrp.main.engine.players.physics

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.DataComponent

/**
 * # Сила
 * Абстрактная величина "силы", применяемая игроком для взаимодействия с игровыми объектами.
 * Используется для расчёта характеристик движения, таких как скорость передвижения, сила удара или высота прыжка.
 *
 * ## **Использования:**
 * - [org.qbrp.main.engine.items.props.PropEntity.tick]: В зависимости от значения силы игрок либо толкает объект [PropEntity],
 *   либо отталкивается от него при столкновении.

 * @param value Значение силы от 0 до 1, где 0 — отсутствие силы, а 1 — максимальная сила.
 */
@Serializable
class Strength(var value: Double): DataComponent {
    /**
     * @return Значение в ньютонах: **кг/см2**
     * **/
    fun toNewton(ctx: StrengthContext): Double {
        return when(ctx) {
            StrengthContext.MOVING -> 20f * value
            StrengthContext.PUSHING -> 1000f * value
        }
    }
}