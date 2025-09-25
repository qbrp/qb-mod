package org.qbrp.main.core.networking.serialization

import kotlin.reflect.KClass

fun String.serializerId(): SerializerRegistryId = SerializerRegistryId(this)
fun KClass<*>.serializerId(): SerializerRegistryId = SerializerRegistryId(this)