package org.qbrp.main.engine.containers.entity

sealed class OccupancyView {
    data class Limited(val fullness: Double): OccupancyView()
    object Unlimited: OccupancyView()
}