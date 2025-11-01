package org.qbrp.main.core.fabricmc.chunks

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos

object MainWorldBlockEvents {
   fun interface Break {
       fun onBreak(pos: BlockPos)
   }
    fun interface Add {
        fun onAdded(pos: BlockPos)
    }

    val ADDED: Event<Add> = EventFactory.createArrayBacked(
        Add::class.java
    ) { listeners: Array<out Add> ->
        Add { pos ->
            for (listener in listeners) {
                listener.onAdded(pos)
            }
            ActionResult.PASS
        }
    }
    val BREAK: Event<Break> = EventFactory.createArrayBacked(
        Break::class.java
    ) { listeners: Array<out Break> ->
        Break { pos ->
            for (listener in listeners) {
                listener.onBreak(pos)
            }
            ActionResult.PASS
        }
    }
}