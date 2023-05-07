package com.villain.villainoushordemanager.testcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.villain.villainoushordemanager.hordes.EntityTypeHorde;
import com.villain.villainoushordemanager.ForgeVillainousHordeManager;
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
        if (!sourceStack.getLevel().isClientSide && ForgeVillainousHordeManager.entityTypeHorde != null && !ForgeVillainousHordeManager.entityTypeHorde.getHordeActive())
            ForgeVillainousHordeManager.entityTypeHorde.SetUpHorde(Objects.requireNonNull(sourceStack.getPlayer()));
        return 0;
    }

    private static int stopHorde(CommandSourceStack sourceStack) {
        if (!sourceStack.getLevel().isClientSide && ForgeVillainousHordeManager.entityTypeHorde != null && ForgeVillainousHordeManager.entityTypeHorde.getHordeActive())
            ForgeVillainousHordeManager.entityTypeHorde.Stop(EntityTypeHorde.HordeStopReasons.DEFEAT);
        return 0;
    }
}
