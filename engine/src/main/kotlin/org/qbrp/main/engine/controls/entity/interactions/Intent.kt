package org.qbrp.main.engine.controls.entity.interactions

enum class Intent(val color: Int, val letter: String) {
    DEFAULT(0xFFFFFFFF.toInt(), "О"),
    FINE(   0xFFADD8E6.toInt(), "Т"),
    ROUGH(  0xFFFFB3B3.toInt(), "Г");
}