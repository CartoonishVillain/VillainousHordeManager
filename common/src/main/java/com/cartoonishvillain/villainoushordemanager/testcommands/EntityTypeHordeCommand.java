package com.cartoonishvillain.villainoushordemanager.testcommands;

import com.cartoonishvillain.villainoushordemanager.VillainousHordeManager;
import com.cartoonishvillain.villainoushordemanager.hordes.EntityTypeHorde;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Objects;

public class EntityTypeHordeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("hordeLibrary").then(Commands.literal("startEntityTypeHorde")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> startHorde(context.getSource()))
        ));

        dispatcher.register(Commands.literal("hordeLibrary").then(Commands.literal("stopEntityTypeHorde")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> stopHorde(context.getSource()))

        ));
    }

    private static int startHorde(CommandSourceStack sourceStack) {
        if (!sourceStack.getLevel().isClientSide && VillainousHordeManager.entityTypeHorde != null && !VillainousHordeManager.entityTypeHorde.getHordeActive())
            VillainousHordeManager.entityTypeHorde.SetUpHorde(Objects.requireNonNull(sourceStack.getPlayer()));
        return 0;
    }

    private static int stopHorde(CommandSourceStack sourceStack) {
        if (!sourceStack.getLevel().isClientSide && VillainousHordeManager.entityTypeHorde != null && VillainousHordeManager.entityTypeHorde.getHordeActive())
            VillainousHordeManager.entityTypeHorde.Stop(EntityTypeHorde.HordeStopReasons.DEFEAT);
        return 0;
    }
}
