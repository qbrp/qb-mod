package org.qbrp.engine.client.core.resources

import org.qbrp.core.resources.data.ZipData
import org.qbrp.core.resources.structure.Branch
import org.qbrp.core.resources.units.DownloadedUnit
import org.qbrp.system.utils.log.Loggers
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.pathString

object ClientResources {
    val logger = Loggers.get("resources")
    val root = ClientStructure(Branch(Paths.get("qbrpClient")).handle().path.toFile())
    val resourcepacks = Branch(File("resourcepacks").toPath()).handle()
    val assets = Branch(File("assets").toPath()).handle()

    fun downloadPack() {
        val pack = resourcepacks.addUnit(ZipData(assets.path.resolve("pack.zip").pathString), name = "pack", extension = "zip") as DownloadedUnit
        pack.download(root.config.resources.downloadUrl)
        pack.extract(resourcepacks.path)
    }
}