package org.qbrp.main.engine.assets.patches

import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import java.io.File
import java.security.MessageDigest

@Autoload(LoadPriority.HIGHEST)
class PatchesModule: QbModule("patches"), PatchesAPI {
    override fun getKoinModule() = inner<PatchesAPI>(this) {}

    override fun generateManifest(path: File, version: String, relative: String): Manifest {
        fun getRelativePath(path: String): String {
            return path.replace(relative, "").replace("\\", "/")
        }

        val files = mutableListOf<FileEntry>()
        path.walkTopDown().forEach {
            if (it.isFile) {
                val entry = FileEntry(getRelativePath(it.path), hashFile(it))
                files.add(entry)
            }
        }

        return Manifest(version, files)
    }

    fun hashFile(file: File, algorithm: String = "SHA-256"): String {
        val digest = MessageDigest.getInstance(algorithm)
        val fileBytes = file.readBytes()
        val hashBytes = digest.digest(fileBytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    override fun generateChangesPatch(copyPath: File, outputPath: File, diff: DiffResult) {
        val changes = diff.added + diff.changed
        changes.forEach {
            val newFile = FileSystem.getOrCreate(File(outputPath, it))
            val sourceFile = File(copyPath, it)
            if (sourceFile.exists()) {
                sourceFile.copyTo(newFile, true)
            } else {
                println("Warning: source file $sourceFile not found!")
            }
        }
    }

}