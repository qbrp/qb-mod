package org.qbrp.main.mechanics.items.display.components

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.networking.synchronization.DtoSync

@Serializable
/**
 * Статическое описание, отображаемое для всех игроков одинаково
 * В будущем сделать РП-описанием
 *  **/
class Description(val text: String): DataComponent, DtoSync