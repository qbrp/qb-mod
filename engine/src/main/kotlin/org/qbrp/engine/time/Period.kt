package org.qbrp.engine.time
data class Period(val name: String,
                  val duration: Int,
                  private val rpDuration: Int = 1,
                  val startTimeTicks: Int,
                  val endTimeTicks: Int) {

    fun getRpDuration() = rpDuration * 60

    @Transient var elapsedTimeMinutes: Int = 0
    fun incrementElapsedTime() = elapsedTimeMinutes++
    fun getElapsedTime() = elapsedTimeMinutes

    fun getRpTime(): Int {
        return (getRpDuration() / duration) * elapsedTimeMinutes
    }

    fun getTickTime(): Long {
        val tickCycleDuration = endTimeTicks - startTimeTicks
        return ((tickCycleDuration / 60) * elapsedTimeMinutes) + startTimeTicks.toLong()
    }
    fun isFinished(): Boolean { return elapsedTimeMinutes >= duration }
}