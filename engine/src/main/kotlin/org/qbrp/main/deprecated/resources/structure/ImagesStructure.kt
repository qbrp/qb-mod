package org.qbrp.deprecated.resources.structure

class ImagesStructure(branchName: String, branch: Branch): Structure(branch.path.toFile()) {
    val imagesBranch = addBranch(branchName)

    fun openImages(extensions: Set<String> = setOf("png")) {}
}