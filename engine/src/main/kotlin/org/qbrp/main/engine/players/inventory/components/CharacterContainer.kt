package org.qbrp.main.engine.players.inventory.components

import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.util.containerComponentName
import org.qbrp.main.engine.containers.v2.components.ContainerAnchor
import org.qbrp.main.engine.players.characters.components.CharacterComponent
import org.qbrp.main.engine.players.characters.components.CharacterComponentInstantiateContext

class CharacterContainer(ctx: CharacterComponentInstantiateContext, override val id: ContainerId) : CharacterComponent(ctx), ContainerAnchor

fun characterContainerComponentName(id: ContainerId) = containerComponentName(id, "CharacterContainer")