package org.qbrp.main.core.assets.common.references

import org.qbrp.main.core.assets.common.Asset
import org.qbrp.main.core.assets.common.Key

interface FileReference<T : Asset> {
    val key: Key
    fun getKey(): String {
        return key.getId()
    }
    fun exists(): Boolean
    fun read(): T
    fun write(data: T)
}