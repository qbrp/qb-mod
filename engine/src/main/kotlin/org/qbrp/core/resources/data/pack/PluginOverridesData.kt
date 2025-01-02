package org.qbrp.core.resources.data.pack
import com.google.gson.annotations.SerializedName
import org.qbrp.core.resources.data.Data

class PluginOverridesData(val predicates: MutableList<Predicate> = mutableListOf<Predicate>()): Data() {
    override fun toFile(): String = gson.toJson(predicates)
    fun addPredicate(path: String, customModelData: Int) {
        predicates.add(Predicate(path, customModelData))
    }

    data class Predicate(
        val path: String,
        @SerializedName("custom_model_data")
        val customModelData: Int
    )
}