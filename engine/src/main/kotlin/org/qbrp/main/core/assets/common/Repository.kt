package org.qbrp.main.core.assets.common

import org.qbrp.main.core.assets.common.Asset

interface Repository {
    fun <T : Asset> getByKey(key: Key): T?
}