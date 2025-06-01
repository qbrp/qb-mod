//package org.qbrp.main.engine.items_old
//
//import org.koin.dsl.module
//import org.qbrp.main.engine.items_old.model.ItemLoader
//import org.qbrp.main.engine.items_old.model.ItemManager
//import org.qbrp.main.core.modules.QbModule
//
//class ItemsModuleOld: QbModule("itemRegistry") {
//    override fun load() {
//    }
//
//    override fun getKoinModule() = module {
//        single { ItemLoader() }
//        single { ItemManager() }
//    }
//}