package dev.koding.islander.fabric;

import dev.koding.islander.Islander;
import net.fabricmc.api.ModInitializer;

public class IslanderFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Islander.init();
    }
}