package org.qbrp.core.resources.units
import java.nio.file.Path
import kotlin.io.path.pathString

abstract class Unit(var path: Path) {
    open fun initFile(): Unit { return this }

    fun pathString(): String {
        return path.pathString
    }
}