package org.qbrp.core.resources.units
import org.qbrp.core.resources.ISavable
import org.qbrp.core.resources.data.Data
import java.nio.file.Path

open class ContentUnit(
    path: Path,
    name: String,
    extension: String,
    open val data: Data
) : Unit(path.resolve("$name.$extension") as Path), ISavable {

    override fun handle(): Unit {
        val filePath = this.path.toFile()
        if (!filePath.exists()) {
            try { filePath.createNewFile()
            } catch (e: Exception) {
                throw IllegalStateException("Не удалось создать файл: ${filePath.path}", e)
            }
        }
        save()
        return this
    }

    override fun save() {
        path.toFile().writeText(data.toFile())
    }

}
