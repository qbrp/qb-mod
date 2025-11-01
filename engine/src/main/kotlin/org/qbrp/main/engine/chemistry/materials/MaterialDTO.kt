package org.qbrp.main.engine.chemistry.materials

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.DTO

@Serializable data class MaterialDTO(val name: String? = null,
                              val proportion: Double,
                              val density: Double? = null,
                              val molarMass: Double? = null): DTO