package org.qbrp.main.core.regions

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Vec3d
import org.koin.core.component.get
import org.qbrp.main.core.regions.model.Cuboid
import org.qbrp.main.core.regions.model.Region
import org.qbrp.main.core.Core
import org.qbrp.main.core.mc.commands.CommandsModule
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.regions.commands.RegionCommands
import org.qbrp.main.core.storage.StorageAPI
import org.qbrp.main.core.storage.Table
import org.qbrp.main.core.utils.log.LoggerUtil
import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload(env = EnvType.SERVER)
class RegionsModule(): QbModule("regions"), RegionsAPI {
    override val regions = mutableListOf<Region>()
    companion object {
        const val TABLE_NAME = "regions"
        private val JSON = Json { ignoreUnknownKeys = true }
    }

    private val logger = LoggerUtil.get("regions")
    private lateinit var storage: Table

    init {
        dependsOn { Core.isApiAvailable<StorageAPI>() }
    }

    override fun getKoinModule() = inner<RegionsAPI>(this) {
        scoped { RegionSelection() }
    }

    override fun onEnable() {
        storage = get<StorageAPI>().getTable(TABLE_NAME)
        loadRegions()
        get<CommandsAPI>().add(RegionCommands(this, getLocal()))
        UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
            get<RegionSelection>().handleInteraction(player as ServerPlayerEntity, hand, hitResult)
            ActionResult.PASS
        })
    }

    fun loadRegions() = runBlocking {
        if (getRegion("world") == null) createRegion("world", Cuboid.createGlobalCuboid())
        regions.addAll(storage.getAll().await().map { JSON.decodeFromString<Region>(it.toJson()) })
        logger.log("Загружено <<${regions.size}>> регионов")
    }

    fun saveAll() {
        regions.forEach { storage.saveObject(it, JSON.encodeToString(it)) }
    }

    override fun nearest(position: Vec3d, count: Int): List<Region> {
        return regions.sortedBy { it.distanceTo(position.x, position.y, position.z) }.take(count)
    }

    override fun createRegion(name: String, cuboid: Cuboid) {
        regions.add(Region(name, listOf(cuboid)))
        saveAll()
    }

    override fun removeRegion(name: String): Boolean {
        val region = regions.find { it.name == name } ?: throw NoSuchElementException("Region $name not found.")
        storage.archive(region)
        return regions.remove(region)
    }

    override fun createRegion(name: String) {
        regions.add(Region(name))
        saveAll()
    }

    override fun getRegion(name: String): Region? {
        return regions.find { it.name == name }
    }
}