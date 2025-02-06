package org.qbrp.core.regions

import org.qbrp.core.resources.ServerResources
import org.qbrp.system.database.DatabaseService
import org.qbrp.system.utils.log.Loggers

object Regions {
    private val regions = mutableListOf<Region>()
    private val databaseService = RegionsDatabase(
        DatabaseService(ServerResources.getConfig().databases.nodeUri, ServerResources.getConfig().databases.regions).apply { connect() }
    )
    private val logger = Loggers.get("regions")

    fun load() {
        if (getRegion("world") == null) createRegion("world", Cuboid.createGlobalCuboid())
        openRegions()
    }

    fun saveAll() {
        regions.forEach { databaseService.saveRegion(it) }
    }

    fun openRegions() {
        regions.addAll(databaseService.openRegions())
        logger.log("Загружено <<${regions.size}>> регионов")
    }

    fun createRegion(name: String, cuboid: Cuboid) {
        regions.add(Region(name, listOf(cuboid)))
        saveAll()
    }

    fun createRegion(name: String) {
        regions.add(Region(name))
        saveAll()
    }

    fun getRegion(name: String): Region? {
        return regions.find { it.name == name }
    }
}