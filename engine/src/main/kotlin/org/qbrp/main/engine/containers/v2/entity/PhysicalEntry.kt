package org.qbrp.main.engine.containers.v2.entity

import org.qbrp.main.engine.physics.AgentEntity
import org.qbrp.main.engine.physics.PhysicsTick

interface PhysicalEntry: ContainerEntry, AgentEntity, PhysicsTick