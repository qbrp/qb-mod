package org.qbrp.core.resources.units
import java.nio.file.Path

abstract class ResourceUnit(var path: Path) {
    open fun handle(): ResourceUnit { return this }

}