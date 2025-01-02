package org.qbrp.core.resources.data.pack

import com.google.gson.annotations.SerializedName
import org.qbrp.core.resources.data.Data
import org.qbrp.core.resources.structure.integrated.Parents

data class PredicatesData(
    val parent: String = Parents.GENERATED.value,
    val textures: Textures = Textures("qbrp:item/placeholder"),
    val overrides: MutableList<Override> = mutableListOf<Override>() // По умолчанию пустой список
): Data() {
    override fun toFile(): String = gson.toJson(this)

    fun addPredicate(modelPath: String, modelData: Int): Override =
        Override(Predicate(modelData), "qbrp:$modelPath").also { overrides.add(it) }

    data class Textures(
        val layer0: String
    )

    data class Override(
        val predicate: Predicate,
        val model: String
    )

    data class Predicate(
        @SerializedName("custom_model_data")
        val customModelData: Int
    )

}
