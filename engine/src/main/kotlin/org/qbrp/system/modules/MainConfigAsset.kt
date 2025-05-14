package org.qbrp.system.modules

import org.qbrp.core.assets.common.NamedAsset

abstract class MainConfigAsset: NamedAsset() {
    override val name: String = "config"
}