package org.qbrp.main.core.modules

import org.qbrp.main.core.assets.common.NamedAsset

abstract class MainConfigAsset: NamedAsset() {
    override val name: String = "config"
}