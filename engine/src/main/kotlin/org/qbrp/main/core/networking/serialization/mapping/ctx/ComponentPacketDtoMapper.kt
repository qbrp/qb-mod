package org.qbrp.main.core.networking.serialization.mapping.ctx

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.networking.serialization.impl.ComponentDtoMappingContext

interface ComponentPacketDtoMapper<Dto: DTO, Obj: Any, State: ComponentManager>: ContextualDtoMapper<Dto, Obj, ComponentDtoMappingContext<State>> {}