package org.qbrp.core.regions

import org.qbrp.core.regions.model.Region
import org.qbrp.system.database.DatabaseService

class RegionsDatabase(val db: DatabaseService) {

    fun openRegions(): List<Region> {
        return db.fetchAll("regions", mapOf(), Region::class.java) as List<Region>
    }

    fun saveRegion(region: Region) {
        db.upsertObject<Region>("regions", mapOf("name" to region.name), region)
    }

    fun deleteRegion(region: Region) {
        db.delete("regions", mapOf("name" to region.name))
    }

}