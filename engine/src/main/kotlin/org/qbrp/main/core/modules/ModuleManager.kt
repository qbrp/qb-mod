package org.qbrp.main.core.modules

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.utils.log.LoggerUtil
import org.reflections.Reflections

open class ModuleManager(open val ignoreEnvironment: Boolean = false): KoinComponent {
    val modules: MutableList<QbModule> = ArrayList()
    protected open val logger = LoggerUtil.get("modules")

    protected open fun init(module: QbModule): QbModule {
        modules.add(module)
        return modules.last()
    }

    fun sendModuleStates(player: ServerPlayerEntity) {
        modules.forEach { it.sendStates(player) }
    }

    fun isModuleEnabled(name: String): Boolean {
        return modules.find { it.getName() == name }?.shouldLoad() == true
    }

    inline fun <reified T : QbModule> isModuleAvailable(): Boolean {
        return modules.filterIsInstance<T>().count() > 0
    }

    inline fun <reified T : QbModule> isModuleEnabled(): Boolean {
        return isModuleEnabled(modules.filterIsInstance<T>().first().getName())
    }

    inline fun <reified T : Any> isApiAvailable(): Boolean {
        return getAPI<T>() != null
    }

    inline fun <reified T : Any> getAPI(): T? {
        return try { get<T>() } catch (e: Exception) { null }
    }

    inline fun <reified T: QbModule> getModule(): T {
        return try { modules.filterIsInstance<T>().first() }
        catch (e: Exception) { if (e is NoSuchElementException) throw NoSuchElementException("Can't find module") else throw e }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : QbModule> getModule(name: String): T? {
        return modules.find { it.getName() == name } as? T
    }

    open val packet = "org.qbrp"
    fun findModules(): Collection<Class<*>> {
        val reflections = Reflections(packet)
        return reflections.getTypesAnnotatedWith(Autoload::class.java)
    }

    open fun initialize() {
        // Этап 1: Сборка и сортировка классов модулей
        val sortedModuleClasses = findModules()
            .sortedByDescending { it.getAnnotation(Autoload::class.java)?.priority ?: LoadPriority.ADDON }

        val env = FabricLoader.getInstance().environmentType

        // Этап 2: Инициализация модулей
        val availableModules = sortedModuleClasses
            .filter {  it.getAnnotation(Autoload::class.java).let {
                    if (it.both == false && !ignoreEnvironment) {
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
                    //Core.informationMessage.addErrorComponent("Класс ${clazz.simpleName} не реализует org.qbrp.main.core.modules.QbModule")
                    null
                }
            } catch (e: Exception) {
                //Core.informationMessage.addErrorComponent("Ошибка при проверке модуля ${clazz.simpleName}: ${e.message}")
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
                    //Core.informationMessage.addWarnComponent("Модуль ${instance.getName()} отключен.")
                    return@forEach
                }

                loadKoinModules(instance.getKoinModule())
                instance.load()
                instance.priority = priority
                init(instance)

            } catch (e: Exception) {
                //Core.informationMessage.addErrorComponent("Ошибка при создании экземпляра модуля ${clazz.simpleName}: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}