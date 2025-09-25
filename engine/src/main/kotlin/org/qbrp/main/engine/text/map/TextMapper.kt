package org.qbrp.main.engine.text.map

import org.qbrp.main.core.networking.serialization.mapping.DtoMapper
import org.qbrp.main.core.networking.serialization.mapping.DtoSerializable
import org.qbrp.main.engine.text.components.TextComponent

interface TextMapper<C: TextComponent, D: TextDto>: DtoMapper<D, C>, DtoSerializable