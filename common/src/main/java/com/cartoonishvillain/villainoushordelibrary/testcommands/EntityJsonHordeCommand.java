package com.cartoonishvillain.villainoushordelibrary.testcommands;

import com.cartoonishvillain.villainoushordelibrary.VillainousHordeLibrary;
import com.cartoonishvillain.villainoushordelibrary.data.JsonHordeData;
import com.cartoonishvillain.villainoushordelibrary.data.JsonMobData;
import com.cartoonishvillain.villainoushordelibrary.hordedata.EntityTypeHordeData;
import com.cartoonishvillain.villainoushordelibrary.hordes.JsonHorde;
import com.cartoonishvillain.villainoushordelibrary.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static com.cartoonishvillain.villainoushordelibrary.VillainousHordeLibrary.loadHordes;

public class EntityJsonHordeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hordeLibrary").then(Commands.literal("reload").requires(cs -> cs.hasPermission(2)).executes(context -> reloadHorde(context.getSource()))));

        dispatcher.register(Commands.literal("hordeLibrary").then(Commands.literal("startJsonHorde")
                        .then(Commands.argument("hordeName", StringArgumentType.string()).then(Commands.argument("anchorplayer", EntityArgument.player())
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> startHorde(context.getSource(), StringArgumentType.getString(context, "hordeName"), EntityArgument.getPlayer(context, "anchorplayer")))
        ))));

        dispatcher.register(Commands.literal("hordeLibrary").then(Commands.literal("startJsonHorde")
                .then(Commands.argument("hordeName", StringArgumentType.string())
                        .requires(cs -> cs.hasPermission(2)).requires(CommandSourceStack::isPlayer)
                        .executes(context -> startHorde(context.getSource(), StringArgumentType.getString(context, "hordeName")))
                )));

        dispatcher.register(Commands.literal("hordeLibrary").then(Commands.literal("stopJsonHorde")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> stopHorde(context.getSource()))
        ));
    }

    private static int startHorde(CommandSourceStack sourceStack, String hordeName) {
        if (!sourceStack.getLevel().isClientSide && (VillainousHordeLibrary.jsonHorde == null || !VillainousHordeLibrary.jsonHorde.getHordeActive())) {
            if (VillainousHordeLibrary.gsonHordes.containsKey(hordeName)) {
                //we have the horde, load the horde.
                JsonHordeData jsonHordeData = VillainousHordeLibrary.gsonHordes.get(hordeName);
                ArrayList<EntityTypeHordeData<?>> hordeMobData = new ArrayList<>();
                for (JsonMobData data : jsonHordeData.getMobData()) {
                    Optional<EntityType<?>> type = EntityType.byString(data.getMobID());
                    if (type.isEmpty()) {
                        Services.PLATFORM.getLOGGER().warn("VillainousHordeLibrary - Failed to load gson mob of type: " + data.getMobID());
                        return 0;
                    }
                    hordeMobData.add(new EntityTypeHordeData(data.getGoalPriority(), data.getGoalMovementSpeed(), data.getSpawnWeight(), type.get()));
                }

                VillainousHordeLibrary.jsonHorde = new JsonHorde(sourceStack.getServer(), jsonHordeData.getKillsRequiredForEasy(),
                        jsonHordeData.getKillsRequiredForNormal(), jsonHordeData.getKillsRequiredForHard(), jsonHordeData.getMaximumActiveHordeMembers(), jsonHordeData.getFindSpawnAttempts(),
                        jsonHordeData.getBossInfoText(), jsonHordeData.getBossInfoColor(), jsonHordeData.getHordeName(), jsonHordeData.isDespawnLeftBehindMembers(), hordeMobData);

                VillainousHordeLibrary.jsonHorde.SetUpHorde(Objects.requireNonNull(sourceStack.getPlayer()));
            } else {
                //we do not have the horde, list the hordes we have.
                StringBuilder builder = new StringBuilder();
                for (String key : VillainousHordeLibrary.gsonHordes.keySet()) {
                    builder.append(key).append(" ");
                }

                sourceStack.sendFailure(Component.literal("Horde not found! Available hordes: " + builder));
            }
        }
        return 0;
    }

    private static int startHorde(CommandSourceStack sourceStack, String hordeName, ServerPlayer player) {
        if (!sourceStack.getLevel().isClientSide && (VillainousHordeLibrary.jsonHorde == null || !VillainousHordeLibrary.jsonHorde.getHordeActive())) {
            if (VillainousHordeLibrary.gsonHordes.containsKey(hordeName)) {
                //we have the horde, load the horde.
                JsonHordeData jsonHordeData = VillainousHordeLibrary.gsonHordes.get(hordeName);
                ArrayList<EntityTypeHordeData<?>> hordeMobData = new ArrayList<>();
                for (JsonMobData data : jsonHordeData.getMobData()) {
                    Optional<EntityType<?>> type = EntityType.byString(data.getMobID());
                    if (type.isEmpty()) {
                        Services.PLATFORM.getLOGGER().warn("VillainousHordeLibrary - Failed to load gson mob of type: " + data.getMobID());
                        return 0;
                    }
                    hordeMobData.add(new EntityTypeHordeData(data.getGoalPriority(), data.getGoalMovementSpeed(), data.getSpawnWeight(), type.get()));
                }

                VillainousHordeLibrary.jsonHorde = new JsonHorde(sourceStack.getServer(), jsonHordeData.getKillsRequiredForEasy(),
                        jsonHordeData.getKillsRequiredForNormal(), jsonHordeData.getKillsRequiredForHard(), jsonHordeData.getMaximumActiveHordeMembers(), jsonHordeData.getFindSpawnAttempts(),
                        jsonHordeData.getBossInfoText(), jsonHordeData.getBossInfoColor(), jsonHordeData.getHordeName(), jsonHordeData.isDespawnLeftBehindMembers(), hordeMobData);

                VillainousHordeLibrary.jsonHorde.SetUpHorde(Objects.requireNonNull(player));
            } else {
                //we do not have the horde, list the hordes we have.
                StringBuilder builder = new StringBuilder();
                for (String key : VillainousHordeLibrary.gsonHordes.keySet()) {
                    builder.append(key).append(" ");
                }

                sourceStack.sendFailure(Component.literal("Horde not found! Available hordes: " + builder));
            }
        }
        return 0;
    }

    private static int stopHorde(CommandSourceStack sourceStack) {
        if (!sourceStack.getLevel().isClientSide && VillainousHordeLibrary.jsonHorde != null && VillainousHordeLibrary.jsonHorde.getHordeActive())
            VillainousHordeLibrary.jsonHorde.Stop(JsonHorde.HordeStopReasons.DEFEAT);
        return 0;
    }

    private static int reloadHorde(CommandSourceStack sourceStack) {
        VillainousHordeLibrary.gsonHordes.clear();

        try {
            loadHordes();
        } catch (FileNotFoundException e) {
            Services.PLATFORM.getLOGGER().warn("VillainousHordeLibrary - hordeJsonData.json not found! No Json hordes are loaded!");
        }

        sourceStack.sendSuccess(() -> Component.literal("Reload attempted."), true);
        return 0;
    }
}
