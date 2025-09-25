package org.qbrp.main.engine.physics.model.materials

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.DTO

@Serializable data class MaterialDTO(val name: String? = null,
                              val proportion: Double,
                              val density: Double? = null,
                              val molarMass: Double? = null): DTO