package dev.koding.islander.fabric

import dev.koding.islander.Islander
import net.fabricmc.api.ModInitializer

class IslanderFabric : ModInitializer {
    override fun onInitialize() {
        Islander.init()
    }
}