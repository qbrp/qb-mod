package org.qbrp.core.resources.content

import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import org.qbrp.core.resources.structure.Branch
import org.qbrp.core.resources.structure.Structure
import org.qbrp.system.utils.keys.Key
import java.util.concurrent.CompletableFuture

class DirectoryStorage(branch: Branch): Structure(branch.resolve("items")) {

    fun openDirectories() {
        path.toFile().listFiles()?.forEach { directory ->
            registerBranch(ItemStorage(directory.toPath()), Key(directory.nameWithoutExtension))
        }
    }

    fun getDirectories() = branchesRegistry.keys.map { it.name }

    fun suggestDirectories(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        getDirectories().forEach { builder.suggest(it) }
        return builder.buildFuture()
    }
}