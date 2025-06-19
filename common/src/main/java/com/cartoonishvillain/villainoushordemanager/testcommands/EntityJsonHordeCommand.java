package com.cartoonishvillain.villainoushordemanager.testcommands;

import com.cartoonishvillain.villainoushordemanager.VillainousHordeManager;
import com.cartoonishvillain.villainoushordemanager.data.JsonHordeData;
import com.cartoonishvillain.villainoushordemanager.data.JsonMobData;
import com.cartoonishvillain.villainoushordemanager.data.JsonWaveData;
import com.cartoonishvillain.villainoushordemanager.hordedata.EntityTypeHordeData;
import com.cartoonishvillain.villainoushordemanager.hordes.JsonHorde;
import com.cartoonishvillain.villainoushordemanager.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;

import java.util.*;

import static com.cartoonishvillain.villainoushordemanager.VillainousHordeManager.loadHordes;

public class EntityJsonHordeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hordeManager").then(Commands.literal("reload").requires(cs -> cs.hasPermission(2)).executes(context -> reloadHorde(context.getSource()))));

        dispatcher.register(Commands.literal("hordeManager").then(Commands.literal("startJsonHorde")
                        .then(Commands.argument("hordeName", StringArgumentType.string()).then(Commands.argument("anchorplayer", EntityArgument.player())
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> startHorde(context.getSource(), StringArgumentType.getString(context, "hordeName"), EntityArgument.getPlayer(context, "anchorplayer")))
        ))));

        dispatcher.register(Commands.literal("hordeManager").then(Commands.literal("startJsonHorde")
                .then(Commands.argument("hordeName", StringArgumentType.string())
                        .requires(cs -> cs.hasPermission(2)).requires(CommandSourceStack::isPlayer)
                        .executes(context -> startHorde(context.getSource(), StringArgumentType.getString(context, "hordeName")))
                )));

        dispatcher.register(Commands.literal("hordeManager").then(Commands.literal("stopJsonHorde")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> stopHorde(context.getSource()))
        ));
    }

    private static int startHorde(CommandSourceStack sourceStack, String hordeName) {
        if (!sourceStack.getLevel().isClientSide && (VillainousHordeManager.jsonHorde == null || !VillainousHordeManager.jsonHorde.getHordeActive())) {
            if (VillainousHordeManager.gsonHordes.containsKey(hordeName)) {
                //we have the horde, load the horde.
                JsonHordeData jsonHordeData = VillainousHordeManager.gsonHordes.get(hordeName);
                Map<String, ArrayList<EntityTypeHordeData<?>>> waveHordeMobData = new HashMap<>();
                for (JsonWaveData wave : jsonHordeData.getWaves()) {
                    ArrayList<EntityTypeHordeData<?>> hordeMobData = new ArrayList<>();
                    for (JsonMobData data : wave.getMobData()) {
                        Optional<EntityType<?>> type = EntityType.byString(data.getMobID());
                        if (type.isEmpty()) {
                            Services.PLATFORM.getLOGGER().warn("VillainousHordeManager - Failed to load json mob of type: " + data.getMobID());
                            return 0;
                        }
                        hordeMobData.add(new EntityTypeHordeData(data.getGoalPriority(), data.getGoalMovementSpeed(), data.getSpawnWeight(), type.get(), data.getNbtData()));
                    }
                    waveHordeMobData.put(wave.getWaveName(), hordeMobData);
                }

                VillainousHordeManager.jsonHorde = new JsonHorde(sourceStack.getServer(), jsonHordeData.getWaves(), jsonHordeData.getHordeName());

                VillainousHordeManager.jsonHorde.SetUpHorde(Objects.requireNonNull(sourceStack.getPlayer()));
            } else {
                //we do not have the horde, list the hordes we have.
                StringBuilder builder = new StringBuilder();
                for (String key : VillainousHordeManager.gsonHordes.keySet()) {
                    builder.append(key).append(" ");
                }

                sourceStack.sendFailure(Component.literal("Horde not found! Available hordes: " + builder));
            }
        }
        return 0;
    }

    private static int startHorde(CommandSourceStack sourceStack, String hordeName, ServerPlayer player) {
        if (!sourceStack.getLevel().isClientSide && (VillainousHordeManager.jsonHorde == null || !VillainousHordeManager.jsonHorde.getHordeActive())) {
            if (VillainousHordeManager.gsonHordes.containsKey(hordeName)) {
                //we have the horde, load the horde.
                JsonHordeData jsonHordeData = VillainousHordeManager.gsonHordes.get(hordeName);
                Map<String, ArrayList<EntityTypeHordeData<?>>> waveHordeMobData = new HashMap<>();
                for (JsonWaveData wave : jsonHordeData.getWaves()) {
                    ArrayList<EntityTypeHordeData<?>> hordeMobData = new ArrayList<>();
                    for (JsonMobData data : wave.getMobData()) {
                        Optional<EntityType<?>> type = EntityType.byString(data.getMobID());
                        if (type.isEmpty()) {
                            Services.PLATFORM.getLOGGER().warn("VillainousHordeManager - Failed to load json mob of type: " + data.getMobID());
                            return 0;
                        }
                        hordeMobData.add(new EntityTypeHordeData(data.getGoalPriority(), data.getGoalMovementSpeed(), data.getSpawnWeight(), type.get(), data.getNbtData()));
                    }
                    waveHordeMobData.put(wave.getWaveName(), hordeMobData);
                }

                VillainousHordeManager.jsonHorde = new JsonHorde(sourceStack.getServer(), jsonHordeData.getWaves(), jsonHordeData.getHordeName());

                VillainousHordeManager.jsonHorde.SetUpHorde(Objects.requireNonNull(player));
            } else {
                //we do not have the horde, list the hordes we have.
                StringBuilder builder = new StringBuilder();
                for (String key : VillainousHordeManager.gsonHordes.keySet()) {
                    builder.append(key).append(" ");
                }

                sourceStack.sendFailure(Component.literal("Horde not found! Available hordes: " + builder));
            }
        }
        return 0;
    }

    private static int stopHorde(CommandSourceStack sourceStack) {
        if (!sourceStack.getLevel().isClientSide && VillainousHordeManager.jsonHorde != null && VillainousHordeManager.jsonHorde.getHordeActive())
            VillainousHordeManager.jsonHorde.Stop(JsonHorde.HordeStopReasons.DEFEAT);
        return 0;
    }

    private static int reloadHorde(CommandSourceStack sourceStack) {
        VillainousHordeManager.gsonHordes.clear();

        int failures = loadHordes();

        if (failures == 0) {
            sourceStack.sendSuccess(() -> Component.translatable("villainoushordemanager.reload.success"), true);
        } else if (failures == -1) {
            sourceStack.sendFailure(Component.translatable("villainoushordemanager.reload.failure"));
        } else {
            sourceStack.sendSuccess(() -> Component.translatable("villainoushordemanager.reload.success.partial", failures), true);
        }
        return 0;
    }
}
