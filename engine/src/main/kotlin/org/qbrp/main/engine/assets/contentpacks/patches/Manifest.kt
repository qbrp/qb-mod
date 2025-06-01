package org.qbrp.main.engine.assets.contentpacks.patches

import kotlinx.serialization.Serializable

@Serializable
class Manifest(val version: String, val files: List<FileEntry>) {

    fun diff(other: Manifest): DiffResult {
        val oldMap = files.associateBy(FileEntry::path)
        val newMap = other.files.associateBy(FileEntry::path)

        val added = newMap.keys - oldMap.keys
        val removed = oldMap.keys - newMap.keys
        val changed = newMap.keys.intersect(oldMap.keys)
            .filter { oldMap[it]?.hash != newMap[it]?.hash }

        return DiffResult(
            added = added.toList(),
            changed = changed,
            deleted = removed.toList()
        )
    }
}