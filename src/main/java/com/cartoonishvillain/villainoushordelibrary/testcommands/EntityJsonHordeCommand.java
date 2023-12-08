package com.cartoonishvillain.villainoushordelibrary.testcommands;

import com.cartoonishvillain.villainoushordelibrary.VillainousHordeLibrary;
import com.cartoonishvillain.villainoushordelibrary.data.JsonHordes;
import com.cartoonishvillain.villainoushordelibrary.data.JsonMobData;
import com.cartoonishvillain.villainoushordelibrary.hordedata.EntityTypeHordeData;
import com.cartoonishvillain.villainoushordelibrary.hordes.EntityEnumHorde;
import com.cartoonishvillain.villainoushordelibrary.hordes.JsonHorde;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class EntityJsonHordeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hordeLibrary").then(Commands.literal("startJsonHorde")
                        .then(Commands.argument("hordeName", StringArgumentType.string())
                .requires(cs -> cs.hasPermission(2))
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
                JsonHordes jsonHordeData = VillainousHordeLibrary.gsonHordes.get(hordeName);
                ArrayList<EntityTypeHordeData> hordeMobData = new ArrayList<>();
                for (JsonMobData data : jsonHordeData.getMobData()) {
                    Optional<EntityType<?>> type = EntityType.byString(data.getMobID());
                    if (type.isEmpty()) {
                        VillainousHordeLibrary.LOGGER.warn("VillainousHordeLibrary - Failed to load gson mob of type: " + data.getMobID());
                        return 0;
                    }
                    hordeMobData.add(new EntityTypeHordeData<>(data.getGoalPriority(), data.getGoalMovementSpeed(), data.getSpawnWeight(), type.get()));
                }

                VillainousHordeLibrary.jsonHorde = new JsonHorde(sourceStack.getServer(), jsonHordeData.getKillsRequiredForEasy(),
                        jsonHordeData.getKillsRequiredForNormal(), jsonHordeData.getKillsRequiredForHard(), jsonHordeData.getMaximumActiveHordeMembers(),
                        jsonHordeData.getBossInfoText(), jsonHordeData.getBossInfoColor(), jsonHordeData.getHordeName(), hordeMobData);

                VillainousHordeLibrary.jsonHorde.SetUpHorde(Objects.requireNonNull(sourceStack.getPlayer()));
            } else {
                //we do not have the horde, list the hordes we have.
                StringBuilder builder = new StringBuilder();
                builder.append("Horde not found! Available hordes: ");
                for (String key : VillainousHordeLibrary.gsonHordes.keySet()) {
                    builder.append(key).append(" ");
                }

                sourceStack.sendFailure(Component.literal(builder.toString()));
            }
        }
        return 0;
    }

    private static int stopHorde(CommandSourceStack sourceStack) {
        if (!sourceStack.getLevel().isClientSide && VillainousHordeLibrary.forgeTestEnumHorde != null && VillainousHordeLibrary.forgeTestEnumHorde.getHordeActive())
            VillainousHordeLibrary.forgeTestEnumHorde.Stop(EntityEnumHorde.HordeStopReasons.DEFEAT);
        return 0;
    }
}
