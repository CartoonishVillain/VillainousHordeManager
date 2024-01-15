package com.cartoonishvillain.villainoushordelibrary;

import com.cartoonishvillain.villainoushordelibrary.codebasedhordetest.TestEnumHorde;
import com.cartoonishvillain.villainoushordelibrary.codebasedhordetest.TestHordeDataClass;
import com.cartoonishvillain.villainoushordelibrary.hordedata.EntityTypeHordeData;
import com.cartoonishvillain.villainoushordelibrary.hordedata.EnumHordeData;
import com.cartoonishvillain.villainoushordelibrary.hordes.EntityTypeHorde;
import com.cartoonishvillain.villainoushordelibrary.platform.Services;
import com.cartoonishvillain.villainoushordelibrary.testcommands.EntityEnumHordeCommand;
import com.cartoonishvillain.villainoushordelibrary.testcommands.EntityJsonHordeCommand;
import com.cartoonishvillain.villainoushordelibrary.testcommands.EntityTypeHordeCommand;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import org.slf4j.Logger;

import java.io.FileNotFoundException;

import static com.cartoonishvillain.villainoushordelibrary.VillainousHordeLibrary.jsonHorde;
import static com.cartoonishvillain.villainoushordelibrary.VillainousHordeLibrary.loadHordes;
import static net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED;
import static net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK;

public class FabricVillainousHordeLibrary implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final ServerStartedListener serverStartedListener = new ServerStartedListener();
    private static final WorldTickListener worldTickListener = new WorldTickListener();

    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.
        VillainousHordeLibrary.init();

        SERVER_STARTED.register(serverStartedListener);
        END_WORLD_TICK.register(worldTickListener);

        //Register commands.
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
                    EntityTypeHordeCommand.register(dispatcher)
            ));

            CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
                    EntityEnumHordeCommand.register(dispatcher)
            ));
        }

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
                EntityJsonHordeCommand.register(dispatcher)
        ));
    }

    private static class ServerStartedListener implements ServerLifecycleEvents.ServerStarted {
        @Override
        public void onServerStarted(MinecraftServer server) {
            if (Services.PLATFORM.isDevelopmentEnvironment()) {
                //Step 1 - Instantiate
                VillainousHordeLibrary.entityTypeHorde = new EntityTypeHorde(server);
                VillainousHordeLibrary.entityEnumHorde = new TestEnumHorde(server);
                //This horde will consist of spiders, evokers, and creepers. Roughly equal quantities, but this is psuedo-randomized, so results may vary.
                VillainousHordeLibrary.entityTypeHorde.setHordeData(
                        new EntityTypeHordeData<>(2, 1, 1, EntityType.SPIDER),
                        new EntityTypeHordeData<>(2, 1, 1, EntityType.EVOKER),
                        new EntityTypeHordeData<>(2, 1, 1, EntityType.CREEPER)
                );
                //This enum horde will consist of creepers, spiders, and vindicators in the overworld, and zombies, evokers, and skeletons in the nether.
                VillainousHordeLibrary.entityEnumHorde.setHordeData(
                        new EnumHordeData(2, 1, 1, TestHordeDataClass.CREEPEROVERZOMBIENETHER),
                        new EnumHordeData(2, 1, 1, TestHordeDataClass.SPIDEROVEREVOKERNETHER),
                        new EnumHordeData(2, 1, 1, TestHordeDataClass.VINDICATOROVERSKELETONNETHER)
                );
            }

            try {
                loadHordes();
            } catch (FileNotFoundException e) {
                LOGGER.warn("VillainousHordeLibrary - hordeJsonData.json not found! No Json hordes are loaded!");
            }
        }
    }

    private static class WorldTickListener implements ServerTickEvents.EndWorldTick {

        @Override
        public void onEndTick(ServerLevel world) {
            if(Services.PLATFORM.isDevelopmentEnvironment()) {
                if (VillainousHordeLibrary.entityTypeHorde != null) {
                    //Step 2 - Connect
                    VillainousHordeLibrary.entityTypeHorde.tick();
                }

                if (VillainousHordeLibrary.entityEnumHorde != null) {
                    VillainousHordeLibrary.entityEnumHorde.tick();
                }
            }

            if (jsonHorde != null) {
                jsonHorde.tick();
            }
        }
    }
}
