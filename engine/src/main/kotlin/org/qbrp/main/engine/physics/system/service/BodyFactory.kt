package org.qbrp.main.engine.physics.system.service

import org.qbrp.main.core.util.math.Pos
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.entity.BodyDescriptor
import org.qbrp.main.engine.physics.system.entity.MutableCompoundBody
import org.qbrp.main.engine.physics.system.entity.PhysicalCharacter
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.entity.Transform

interface BodyFactory {
    fun build(descriptor: BodyDescriptor): Body
    fun plane(y: Int): Body
    fun character(shape: Shape, mass: Double, location: Pos) : PhysicalCharacter
    fun mutableCompoundBody(shapes: Shape.Multi, transform: Transform): MutableCompoundBody
}