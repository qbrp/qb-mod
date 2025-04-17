package org.qbrp.engine.time

import org.qbrp.system.modules.ModuleAPI

interface TimeAPI: ModuleAPI {
    fun getRpTime(): Int
    fun getGameTime(): Int
    fun getTickTime(): Long
    fun getCurrentPeriod(): Period?
    fun setRpTime(time: Int)
    fun getFormattedRpTime(): String
    fun setCycleEnabled(enabled: Boolean)
    fun broadcastTime(time: Int = getRpTime(), period: String = getCurrentPeriod()!!.name)
}