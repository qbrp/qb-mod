package org.qbrp.core.assets.common.files

import org.qbrp.core.assets.common.Asset
import org.qbrp.core.assets.common.Key
import org.qbrp.core.resources.Savable
import org.qbrp.system.utils.log.Loggers
import java.io.File

class FilePathReference<T: Asset>(override val key: Key, private val factory: (File) -> T) : FileReference<T> {
    companion object {
        private val LOGGER = Loggers.get("resources", "debug")
    }

    val path = File(key.getId())

    override fun exists(): Boolean = path.exists()

    override fun read(): T = factory(path)

    override fun write(data: T) {
        (data as? Savable)?.save()
            ?: LOGGER.warn("Невозможно сохранить данные объекта $data в ${path.absolutePath}, т.к. нет имплементации Savable")
    }
}