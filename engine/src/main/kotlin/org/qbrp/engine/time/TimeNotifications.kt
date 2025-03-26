package org.qbrp.engine.time
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.addons.BroadcasterAPI
import org.qbrp.engine.chat.core.messages.ChatMessage

class TimeNotifications(val config: ServerConfigData.Time): KoinComponent {
    fun broadcastTimeDo(time: Int, name: String) {
        Engine.getAPI<BroadcasterAPI>()!!.broadcastGlobalDo(
            ChatMessage.text("${TimeUtils.minutesToTime(TimeUtils.roundMinutesToNearestHalfHour(time))} - $name")
                .apply {
                    getTagsBuilder()
                        .placeholder("name", "&bВремя")
                }
        )
    }
}