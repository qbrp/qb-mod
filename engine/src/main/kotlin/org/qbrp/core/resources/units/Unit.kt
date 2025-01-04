package org.qbrp.core.resources.units
import java.nio.file.Path

abstract class Unit(var path: Path) {
    open fun handle(): Unit { return this }
}