package org.qbrp.engine.time

import net.minecraft.server.MinecraftServer

class WorldTimeManager(
    private val server: MinecraftServer,
) {
//    private val timeMarkers: MutableList<TimeMarker> = mutableListOf()
//
//    fun checkTimeMarkers() {
//        for (i in 0 until timeMarkers.size) {
//            val marker = timeMarkers[i]
//            if (convertRealMinutesToTicks(marker.time) >= server.overworld.time) {
//                timeMarkers.removeAt(i)
//            }
//        }
//    }


    fun setTickTime(ticks: Long) {
        server.worlds.forEach { it.timeOfDay = ticks }
        //checkTimeMarkers()
    }

    fun getTickTime(): Long = server.overworld.time
}