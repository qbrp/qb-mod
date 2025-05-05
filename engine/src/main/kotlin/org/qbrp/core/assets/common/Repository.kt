package org.qbrp.core.assets.common

interface Repository {
    fun <T : Asset> getByKey(key: Key): T?
}