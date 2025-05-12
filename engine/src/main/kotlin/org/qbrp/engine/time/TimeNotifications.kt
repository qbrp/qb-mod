package org.qbrp.engine.time
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.addons.BroadcasterAPI
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.time.config.TimeConfig


class TimeNotifications(): KoinComponent {
    var config = get<TimeConfig>()

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