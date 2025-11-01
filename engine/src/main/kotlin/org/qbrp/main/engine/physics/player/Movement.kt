package org.qbrp.main.engine.physics.player

import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.fabricmc.player.Permission
import org.qbrp.main.core.fabricmc.player.hasPermission
import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.core.util.math.lerp
import org.qbrp.main.engine.physics.system.BUILTIN_GRAVITY
import org.qbrp.main.engine.physics.system.entity.vec3FromAngle2
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.world.location.WorldVec3
import org.qbrp.main.engine.world.location.asWorldVec3
import kotlin.math.max

// Input

interface MovementInput {
    val pressingForward: Boolean
    val pressingBack: Boolean
    val pressingLeft: Boolean
    val pressingRight: Boolean
    val isSneaking: Boolean
    val isJumping: Boolean
}

data class MovementInputComponent(
    @Volatile override var pressingForward: Boolean,
    @Volatile override var pressingBack: Boolean,
    @Volatile override var pressingLeft: Boolean,
    @Volatile override var pressingRight: Boolean,
    @Volatile override var isSneaking: Boolean,
    @Volatile override var isJumping: Boolean
) : DataComponent, MovementInput

val ServerPlayerObject.input: MovementInputComponent
    get() = getOrAdd {
        MovementInputComponent(
            pressingForward = false,
            pressingBack = false,
            pressingLeft = false,
            pressingRight = false,
            isSneaking = false,
            isJumping = false
        )
    }

// Emulation

data class MovementEmulation(
    var forward: Double = 0.0,
    var back: Double = 0.0,
    var left: Double = 0.0,
    var right: Double = 0.0,
    var up: Double = 0.0,
    var bottom: Double = 0.0,
) : DataComponent {
    val x get() = forward - back
    val z get() = right - left
    val y get() = up - bottom
    val isMoving get() = right != 0.0 || back != 0.0 || left != 0.0 || forward != 0.0

    fun emulateJump(jumpStrength: Double) {
        up = lerp(up, jumpStrength, 0.3)
    }

    fun emulateBottom(jumpStrength: Double) {
        bottom = lerp(bottom, jumpStrength, 0.3)
    }

    fun emulateForward(speed: Double) {
        forward = lerp(forward, speed, 0.3)
    }

    fun emulateBack(speed: Double) {
        back = lerp(back, speed, 0.3)
    }

    fun emulateRight(speed: Double) {
        right = lerp(right, speed, 0.3)
    }

    fun emulateLeft(speed: Double) {
        left = lerp(left, speed, 0.3)
    }
}

fun MovementEmulation.tickInput(
    input: MovementInput,
    isOnGround: Boolean,
    fly: Boolean,
    maxHorizontalSpeed: Double,
    maxVerticalSpeed: Double = maxHorizontalSpeed,
) {
    emulateForward(if (input.pressingForward) maxHorizontalSpeed else 0.0)
    emulateBack(if (input.pressingBack) maxHorizontalSpeed else 0.0)
    emulateLeft(if (input.pressingLeft) maxHorizontalSpeed else 0.0)
    emulateRight(if (input.pressingRight) maxHorizontalSpeed else 0.0)
    val isJumping = input.isJumping
    val canMoveUp = fly || isOnGround
    emulateJump(if (isJumping && canMoveUp) maxVerticalSpeed else 0.0)

    val isSneaking = input.isSneaking
    val canMoveDown = fly
    emulateBottom(if (isSneaking && canMoveDown) maxVerticalSpeed else 0.0)
}

fun MovementEmulation.getHorizontalMovementVector(yaw: Double): Vec3 {
    val lookVector = vec3FromAngle2(yaw, 0.0)
    val forward = lookVector.normalize().negate()

    val forwardMove = forward
        .mul(x)
    val rightMove = Vec3(forward.z, 0.0, -forward.x)
        .mul(z)

    return forwardMove.add(rightMove)
}

// Movement

fun getMovementVelocity(
    linearVelocity: WorldVec3,
    emulation: MovementEmulation,
    yaw: Double,
    fly: Boolean,
    isOnGround: Boolean,
    deltaTime: Double = 0.05,
    maxFallSpeed: Double = -30.0,
    maxJumpSpeed: Double = 30.0
): WorldVec3 {
    val moveVector = emulation.getHorizontalMovementVector(yaw)
    val verticalMove = emulation.y
    var y = linearVelocity.y

    if (!fly) {
        if (isOnGround) {
            y = 0.0
        } else {
            y += BUILTIN_GRAVITY.y * deltaTime
        }
    } else {
        y = max(y, -BUILTIN_GRAVITY.y)
    }

    y += verticalMove
    y = y.coerceIn(maxFallSpeed, maxJumpSpeed)

    return Vec3(moveVector.x, y, moveVector.z).asWorldVec3()
}

fun PlayerObject.tickMovement(
    yaw: Double,
    emulation: MovementEmulation = require(),
    fly: Boolean = require<PlayerMovementAbilities>().fly,
    linearVelocity: WorldVec3 = this.velocity,
    isOnGround: Boolean = this.isSupported
) {
    val velocity = getMovementVelocity(linearVelocity, emulation, yaw, fly, isOnGround)
    println("На земле: $isOnGround, движение: $linearVelocity")
    setVelocity(velocity)
}

// Movement data

val FLY_PERMISSION = Permission("fly")

data class PlayerMovementAbilities(
    var fly: Boolean,
) : DataComponent {
    companion object {
        val DEFAULT = PlayerMovementAbilities(false)
    }
}

fun ServerPlayerObject.hasFlyPermission() = hasPermission(FLY_PERMISSION)

var PlayerObject.fly: Boolean
    get() = this.getOrAdd { PlayerMovementAbilities.DEFAULT }.fly
    set(value) {
        this.getOrAdd<PlayerMovementAbilities> { PlayerMovementAbilities.DEFAULT.copy(value) }.fly
    }

fun PlayerObject.toggleFly() {
    this.fly = !fly
}

// Teleportation

data class TeleportationComponent(val x: Double, val y: Double, val z: Double, val notify: Boolean) : DataComponent