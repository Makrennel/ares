package org.aresclient.ares.impl.global

import dev.tigr.simpleevents.listener.EventHandler
import dev.tigr.simpleevents.listener.EventListener
import org.aresclient.ares.api.Ares
import org.aresclient.ares.api.event.AresEvent
import org.aresclient.ares.api.event.client.SendMovementPacketsEvent
import org.aresclient.ares.api.global.Global
import org.aresclient.ares.api.minecraft.math.Vec2f
import org.aresclient.ares.api.minecraft.math.Vec3d

object Movement: Global("Movement", "Handles movement related tasks and packets") {
    class MovePacketEvent(era: Era): AresEvent("MovePacketEvent", era) {
        var position: Vec3d = TODO()
        var rotation: Vec2f = TODO()
        var onGround: Boolean = TODO()
    }
    
    @field:EventHandler
    private val onSendMovementPacketsEvent = EventListener<SendMovementPacketsEvent> {
        val event = Ares.getEventManager().post(MovePacketEvent(AresEvent.Era.BEFORE))
        if(!event.isCancelled) return@EventListener
        it.isCancelled = true

        sendSprintPacket()
        sendSneakPacket()

        /*if(TODO("MC.client.cameraEntity == MC.player")) {
            val diffX = event.position.x - TODO("MC.player.lastX")
            val diffY = event.position.y - TODO("MC.player.lastY")
            val diffZ = event.position.z - TODO("MC.player.lastZ")
            val diffYaw = event.rotation.x - TODO("MC.player.lastYaw")
            val diffPitch = event.rotation.y - TODO("MC.player.lastPitch")

            // TODO: Increment ticksSinceLastPositionPacketSent by 1
            val move = (diffX * diffX + diffY * diffY + diffZ * diffZ) > 2.0E-4 * 2.0E-4 || TODO("MC.player.ticksSinceLastPositionPacketSent >= 20")
            val rotate = diffYaw != 0.0 || diffPitch != 0.0

            sendMovementPacket(event, move, rotate)
            setLastStates(event, move, rotate)
        }*/

        Ares.getEventManager().post(MovePacketEvent(AresEvent.Era.AFTER))
    }

    private fun sendSprintPacket() {
    }

    private fun sendSneakPacket() {
    }

    private fun sendMovementPacket(event: MovePacketEvent, move: Boolean, rotate: Boolean) {
    }

    private fun setLastStates(event: MovePacketEvent, move: Boolean, rotate: Boolean) {
    }
}