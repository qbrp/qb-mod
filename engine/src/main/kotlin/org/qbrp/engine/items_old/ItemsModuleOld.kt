//package org.qbrp.engine.items_old
//
//import org.koin.dsl.module
//import org.qbrp.engine.items_old.model.ItemLoader
//import org.qbrp.engine.items_old.model.ItemManager
//import org.qbrp.system.modules.QbModule
//
//class ItemsModuleOld: QbModule("items") {
//    override fun load() {
//    }
//
//    override fun getKoinModule() = module {
//        single { ItemLoader() }
//        single { ItemManager() }
//    }
//}