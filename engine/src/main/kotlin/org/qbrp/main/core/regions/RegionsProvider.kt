package org.qbrp.main.core.regions

import org.koin.core.context.GlobalContext
import org.qbrp.main.core.mc.commands.templates.ListProvider
import org.qbrp.main.core.regions.model.Region

class RegionsProvider(val regionsList: Collection<Region> = GlobalContext.get().get<RegionsAPI>().regions)
    : ListProvider<String>( { regionsList.map { it.name } } )