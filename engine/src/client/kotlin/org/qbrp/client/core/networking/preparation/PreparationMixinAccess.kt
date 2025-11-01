package org.qbrp.client.core.networking.preparation

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PreparationMixinAccess : KoinComponent {
    val isReady get() = inEngineLoaded(getKoin())
}