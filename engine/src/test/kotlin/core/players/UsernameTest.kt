package core.players

import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test
import org.qbrp.main.core.mc.player.dataformats.Username

class UsernameTest {
    @Test
    fun `username can't contains spaces`() {
        shouldThrow<Exception> { Username("space letters") }
    }
}