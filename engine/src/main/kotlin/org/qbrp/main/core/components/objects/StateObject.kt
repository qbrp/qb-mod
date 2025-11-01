package org.qbrp.main.core.components.objects

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.util.IDGenerator
import org.qbrp.main.core.components.Stateful
import org.qbrp.main.core.components.serialization.Identifiable

typealias ComponentObject = StateObject<*>

abstract class StateObject<T : ComponentManager>(override val state: T) : Stateful<T>, Identifiable,
    ComponentManager by state {
    override val id: String = IDGenerator.nextIdStr()
}