package org.qbrp.core.resources.structure
import net.minecraft.util.Identifier
import org.qbrp.core.resources.units.ContentUnit
import org.qbrp.core.resources.units.TextureUnit
import org.qbrp.core.resources.data.pack.MetaData
import org.qbrp.core.resources.data.pack.ModelData
import org.qbrp.core.resources.data.config.ServerConfigData.Resources.Pack
import org.qbrp.core.resources.data.pack.PredicatesData
import org.qbrp.core.resources.data.pack.TextureData
import org.qbrp.core.resources.structure.integrated.Branches
import org.qbrp.core.resources.structure.integrated.Parents
import java.io.File
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

class PackStructure(path: File) : Structure(path, clear = true) {

    init { handle() }

    val root = addBranch("assets")
    val mod = root.addBranch("qbrp")

    val modelsRegistry = mod.addBranch("models")
    val itemTypes = modelsRegistry.addBranch("item")
    val itemModels = modelsRegistry.addBranch("item")
    val texturesRegistry = mod.addBranch("textures")
    val itemTextures = texturesRegistry.addBranch("item")

    fun initResourcePack(pack: Pack, icon: File) {
        addUnit(MetaData(pack), name = "pack", extension = "mcmeta")
        addUnit(TextureData(icon.path), name = "pack", extension = "png")
    }

    fun addItemType(item: Identifier, parent: Parents) {
        itemTypes.addUnit(
            PredicatesData(parent = parent.value), item.path, "json")
    }

    fun addTexture(path: Path): TextureUnit {
        return itemTextures.addContainer()
            .addUnit(TextureData(path.toString()), path.nameWithoutExtension, "png") as TextureUnit
    }

    fun addModel(model: ModelData): ContentUnit {
        return itemModels.addContainer()
            .addUnit(model, path.nameWithoutExtension, "json")
    }

}