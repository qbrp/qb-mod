package org.qbrp.main.engine.containers.entity

import org.qbrp.main.engine.chemistry.AgentEntity
import org.qbrp.main.engine.chemistry.PhysicsTick

interface PhysicalEntry: ContainerEntry, AgentEntity, PhysicsTick