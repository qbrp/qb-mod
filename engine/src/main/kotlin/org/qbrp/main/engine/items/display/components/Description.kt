package org.qbrp.main.engine.items.display.components

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.DataComponent

@Serializable
/**
 * Статическое описание, отображаемое для всех игроков одинаково
 * В будущем сделать РП-описанием
 *  **/
class Description(val text: String) : DataComponent