package dev.koding.islander.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.koding.islander.Islander;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Islander.MOD_ID)
public class IslanderForge {
    public IslanderForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Islander.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
            Islander.init();
    }
}