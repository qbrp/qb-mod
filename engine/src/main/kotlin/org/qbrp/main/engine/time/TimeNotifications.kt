package org.qbrp.main.engine.time
import org.koin.core.component.KoinComponent
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.addons.BroadcasterAPI
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.time.config.TimeConfig


class TimeNotifications(var config: TimeConfig): KoinComponent {
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