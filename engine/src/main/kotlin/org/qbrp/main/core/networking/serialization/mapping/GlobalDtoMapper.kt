package org.qbrp.main.core.networking.serialization.mapping

import org.qbrp.main.core.components.serialization.DTO

interface GlobalDtoMapper: DtoMapper<DTO, Any>, DtoMapperRegistry<DTO, Any>