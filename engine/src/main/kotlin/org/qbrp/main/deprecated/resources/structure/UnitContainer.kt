package org.qbrp.deprecated.resources.structure
import java.nio.file.Path
import java.util.UUID

class UnitContainer(path: Path, uuid: String = UUID.randomUUID().toString() ) : Branch(path.resolve(uuid))