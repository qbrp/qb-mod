package org.qbrp.main.engine.controls.entity.conditions

sealed class AvailableResult {
    object Yes : AvailableResult()
    class No(val message: String) : AvailableResult()
}