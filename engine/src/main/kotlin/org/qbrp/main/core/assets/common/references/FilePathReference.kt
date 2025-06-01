package org.qbrp.main.core.assets.common.references

import org.qbrp.main.core.assets.common.Asset
import org.qbrp.main.core.assets.common.Key
import org.qbrp.deprecated.resources.Savable
import org.qbrp.main.core.utils.log.LoggerUtil
import java.io.File

class FilePathReference<T: Asset>(override val key: Key, private val factory: (File) -> T) : FileReference<T> {
    companion object {
        private val LOGGER = LoggerUtil.get("resources", "debug")
    }

    val path = File(key.getId())

    override fun exists(): Boolean = path.exists()

    override fun read(): T = factory(path)

    override fun write(data: T) {
        (data as? Savable)?.save()
            ?: LOGGER.warn("Невозможно сохранить данные объекта $data в ${path.absolutePath}, т.к. нет имплементации Savable")
    }
}