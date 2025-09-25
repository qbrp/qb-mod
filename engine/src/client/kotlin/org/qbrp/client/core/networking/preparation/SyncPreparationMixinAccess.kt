package org.qbrp.client.core.networking.preparation

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SyncPreparationMixinAccess: KoinComponent {
    private val preparation by inject<SyncPreparation>()
    val isReady get() = preparation.isReady

    fun onReady() = preparation
}