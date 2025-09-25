package org.qbrp.main.engine.containers.v2.entity

sealed class OccupancyView {
    data class Limited(val fullness: Double): OccupancyView()
    object Unlimited: OccupancyView()
}