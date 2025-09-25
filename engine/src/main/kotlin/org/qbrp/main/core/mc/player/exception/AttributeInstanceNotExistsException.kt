package org.qbrp.main.core.mc.player.exception

import net.minecraft.entity.Entity
import java.lang.RuntimeException

class AttributeInstanceNotExistsException(val entity: Entity,
                                          val attribute: String
): RuntimeException("Entity ${entity.id} (${entity::class.simpleName}) doesnt have $attribute")