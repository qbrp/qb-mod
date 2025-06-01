package org.qbrp.main.engine.assets.resourcepack.models


interface Model {
    val parent: String
    val model: String
    fun getName(): String {
        return model.split("/").last().split(".").first()
    }
}