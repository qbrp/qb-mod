package org.qbrp.engine.time
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.resources.data.config.ConfigInitializationCallback
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.addons.BroadcasterAPI
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.networking.messages.types.StringContent

class TimeNotifications(): KoinComponent {
    private var config = get<ServerConfigData>().time
    init {
        ConfigInitializationCallback.EVENT.register { config ->
            this.config = config.time
        }
    }

    fun broadcastTimeDo(time: Int, name: String) {
        Engine.getAPI<BroadcasterAPI>()!!.broadcastGlobalDo(
            ChatMessage.text(config.formatDo)
                .apply {
                    getTagsBuilder()
                        .placeholder("name", "&bВремя")
                        .placeholder("time.roundRpTime", TimeUtils.minutesToTime(TimeUtils.roundMinutesToNearestHalfHour(time)))
                        .placeholder("time.rpTime", TimeUtils.minutesToTime(time))
                        .placeholder("time.period", name)
                }
        )
    }
}