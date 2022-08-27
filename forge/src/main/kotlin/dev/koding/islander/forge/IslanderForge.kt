package dev.koding.islander.forge

import dev.architectury.platform.forge.EventBuses
import dev.koding.islander.Islander
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.KotlinModLoadingContext

@Mod(Islander.MOD_ID)
object IslanderForge {
    init {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Islander.MOD_ID, KotlinModLoadingContext.get().getKEventBus())
        Islander.init()
    }
}