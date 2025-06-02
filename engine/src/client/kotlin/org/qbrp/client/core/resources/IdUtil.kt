package org.qbrp.client.core.resources

import net.minecraft.client.util.ModelIdentifier
import net.minecraft.util.Identifier

object IdUtil {
    fun clean(id: Identifier): ModelIdentifier {
        val cleanedPath = id.path.replace(".json", "").replace("models/item/", "")
        val fixedId = Identifier("qbrp", cleanedPath);
        return ModelIdentifier(fixedId, "inventory");
    }
}