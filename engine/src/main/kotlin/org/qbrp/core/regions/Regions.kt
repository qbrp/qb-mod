package org.qbrp.core.regions

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.qbrp.core.game.commands.templates.ListProvider
import org.qbrp.core.regions.model.Cuboid
import org.qbrp.core.regions.model.Region
import org.qbrp.core.resources.ServerResources
import org.qbrp.system.database.DatabaseService
import org.qbrp.system.utils.log.Loggers

object Regions {
    private val regions = mutableListOf<Region>()
    private val databaseService = RegionsDatabase(
        DatabaseService(ServerResources.getConfig().databases.nodeUri, ServerResources.getConfig().databases.regions)
            //.apply { connect() }
    )
    private val logger = Loggers.get("regions")

    fun getRegions() = regions
    fun load() {
        if (getRegion("world") == null) createRegion("world", Cuboid.Companion.createGlobalCuboid())
        openRegions()
    }

    fun nearest(position: Vec3d, count: Int = 1): List<Region> {
        return regions.sortedBy { it.distanceTo(position.x, position.y, position.z) }.take(count)
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

    fun removeRegion(name: String) {
        val region = regions.find { it.name == name } ?: throw NoSuchElementException("Region $name not found.")
        databaseService.deleteRegion(region)
        regions.remove(region)
    }

    fun createRegion(name: String) {
        regions.add(Region(name))
        saveAll()
    }

    fun getRegion(name: String): Region? {
        return regions.find { it.name == name }
    }

    class RegionsProvider() : ListProvider<String>( { getRegions().map { it.name } } )
}