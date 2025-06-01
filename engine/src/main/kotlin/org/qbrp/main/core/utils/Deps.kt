package org.qbrp.main.core.utils

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.qbrp.main.core.game.prefabs.RuntimePrefab

object Deps: KoinComponent {
    val PLAYER_PREFAB
        get() = get<RuntimePrefab.Tag>(named("player-tag"))
}