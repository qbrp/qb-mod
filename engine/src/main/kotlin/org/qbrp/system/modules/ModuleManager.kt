package org.qbrp.system.modules

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.qbrp.core.ServerCore
import org.qbrp.core.assets.FileSystem
import org.qbrp.system.utils.log.Loggers
import org.reflections.Reflections

open class ModuleManager: KoinComponent {
    val modules: MutableList<QbModule> = ArrayList()
    private val logger = Loggers.get("modules")

    protected open fun init(module: QbModule): QbModule {
        modules.add(module)
        return modules.last()
    }

    fun sendModuleInformation(player: ServerPlayerEntity) {
        modules.forEach {
            it.sendStateInformation(player)
        }
    }

    fun isModuleEnabled(name: String): Boolean {
        return modules.find { it.getName() == name }?.shouldLoad() == true
    }

    inline fun <reified T : QbModule> isModuleAvailable(): Boolean {
        return modules.filterIsInstance<T>().count() > 0
    }

    inline fun <reified T : ModuleAPI> isApiAvailable(): Boolean {
        return getAPI<T>() != null
    }

    fun <T : QbModule> getModule(name: String): T? {
        return modules.find { it.getName() == name } as? T
    }

    inline fun <reified T : ModuleAPI> getAPI(): T? {
        return modules.find { it.getAPI() is T } as? T
    }

    fun initialize() {
        val reflections = Reflections("org.qbrp")
        val moduleClasses = reflections.getTypesAnnotatedWith(Autoload::class.java)

        // Этап 1: Сборка и сортировка классов модулей
        val sortedModuleClasses = moduleClasses
            .sortedByDescending { it.getAnnotation(Autoload::class.java)?.priority ?: LoadPriority.ADDON }

        val env = FabricLoader.getInstance().environmentType

        // Этап 2: Инициализация модулей
        val availableModules = sortedModuleClasses
            .filter {  it.getAnnotation(Autoload::class.java).let {
                    if (it.both == false) {
                        it.env == env
                    } else {
                        true
                    }
                }
            }
            .also { logger.log("Найдено ${it.count()} модулей") }
            .mapNotNull { clazz ->
            try {
                if (QbModule::class.java.isAssignableFrom(clazz)) {
                    clazz to (clazz.getAnnotation(Autoload::class.java)?.priority ?: LoadPriority.ADDON)
                } else {
                    ServerCore.informationMessage.addErrorComponent("Класс ${clazz.simpleName} не реализует org.qbrp.system.modules.QbModule")
                    null
                }
            } catch (e: Exception) {
                ServerCore.informationMessage.addErrorComponent("Ошибка при проверке модуля ${clazz.simpleName}: ${e.message}")
                e.printStackTrace()
                null
            }
        }

        // Добавляем модули в список через init(it)
        availableModules
            .sortedByDescending { it::class.java.getAnnotation(Autoload::class.java)?.priority }
            .forEach { (clazz, priority) ->
            try {
                // Создаем экземпляр модуля
                val instance = clazz.getDeclaredConstructor().newInstance() as QbModule

                if (instance.shouldLoad()) {
                    if(instance.createFile) FileSystem.createModuleFile(instance.getName())
                    logger.success("Загружен ${instance.getName()}")
                } else {
                    ServerCore.informationMessage.addWarnComponent("Модуль ${instance.getName()} отключен.")
                    return@forEach
                }

                instance.load()
                instance.priority = priority
                init(instance)

            } catch (e: Exception) {
                ServerCore.informationMessage.addErrorComponent("Ошибка при создании экземпляра модуля ${clazz.simpleName}: ${e.message}")
                e.printStackTrace()
            }
        }

        modules.sortByDescending { it.priority }
    }
}