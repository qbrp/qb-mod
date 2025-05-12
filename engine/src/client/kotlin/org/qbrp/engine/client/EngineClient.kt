package org.qbrp.engine.client
import dev.felnull.specialmodelloader.api.SpecialModelLoaderAPI
import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents
import dev.felnull.specialmodelloader.impl.SpecialModelLoaderAPIImpl
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.SystemToast
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.util.Identifier
import org.koin.core.context.startKoin
import org.qbrp.core.Core
import org.qbrp.engine.client.core.ClientNotifications
import org.qbrp.engine.client.core.RegistrationManager
import org.qbrp.engine.client.core.ToastNotificationsManager
import org.qbrp.engine.client.core.events.ClientHandlers
import org.qbrp.engine.client.core.events.ClientReceivers
import org.qbrp.engine.client.render.Render
import org.qbrp.engine.client.core.keybinds.KeybindsManager
import org.qbrp.engine.client.system.ClientModuleManager
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.utils.format.Format.asMiniMessage
import java.util.function.BiPredicate

class EngineClient : ClientModInitializer {

    companion object {
        val moduleManager = ClientModuleManager()
        val keybindsManager = KeybindsManager()
        val notificationsManager: ClientNotifications = ToastNotificationsManager()
        val registrationManager = RegistrationManager()

        inline fun <reified T : ModuleAPI> getAPI(): T? {
            return moduleManager.getAPI<T>()
        }

        inline fun <reified T : ModuleAPI> isApiAvailable(): Boolean {
            return moduleManager.isApiAvailable<T>()
        }

        fun getServerIp(): String? {
            val client = MinecraftClient.getInstance()
            val networkHandler = client.networkHandler
            return networkHandler?.connection?.address?.toString()
                ?.replace("/", "")
                ?.split(":")?.first()
        }
    }

    override fun onInitializeClient() {
        //ClientResources.downloadPack()
        SpecialModelLoaderEvents.LOAD_SCOPE.register { location ->
            Core.MOD_ID == location.namespace
        }

        ClientHandlers.registerEvents()

        startKoin {
            moduleManager.initialize()
        }

        Render.initialize()

        ClientReceivers.register()
        keybindsManager.registerKeyBindings()
    }

}