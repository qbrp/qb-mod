package org.qbrp.main.engine.containers.v2.entity

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.serialization.Identifiable

interface ContainerEntity: Identifiable, ComponentManager, EntryReference