package com.cartoonishvillain.villainoushordelibrary.testcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.cartoonishvillain.villainoushordelibrary.VillainousHordeLibrary;
import com.cartoonishvillain.villainoushordelibrary.hordes.EntityTypeHorde;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Objects;

public class EntityTypeHordeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("hordeManager").then(Commands.literal("startEntityTypeHorde")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> startHorde(context.getSource()))

        ));

        dispatcher.register(Commands.literal("hordeManager").then(Commands.literal("stopEntityTypeHorde")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> stopHorde(context.getSource()))

        ));
    }

    private static int startHorde(CommandSourceStack sourceStack) {
        if (!sourceStack.getLevel().isClientSide && VillainousHordeLibrary.entityTypeHorde != null && !VillainousHordeLibrary.entityTypeHorde.getHordeActive())
            VillainousHordeLibrary.entityTypeHorde.SetUpHorde(Objects.requireNonNull(sourceStack.getPlayer()));
        return 0;
    }

    private static int stopHorde(CommandSourceStack sourceStack) {
        if (!sourceStack.getLevel().isClientSide && VillainousHordeLibrary.entityTypeHorde != null && VillainousHordeLibrary.entityTypeHorde.getHordeActive())
            VillainousHordeLibrary.entityTypeHorde.Stop(EntityTypeHorde.HordeStopReasons.DEFEAT);
        return 0;
    }
}
