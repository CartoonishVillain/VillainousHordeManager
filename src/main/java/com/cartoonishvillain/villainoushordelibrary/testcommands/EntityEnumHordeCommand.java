package com.cartoonishvillain.villainoushordelibrary.testcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.cartoonishvillain.villainoushordelibrary.VillainousHordeLibrary;
import com.cartoonishvillain.villainoushordelibrary.hordes.EntityEnumHorde;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Objects;

public class EntityEnumHordeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hordeManager").then(Commands.literal("startEnumHorde")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> startHorde(context.getSource()))

        ));

        dispatcher.register(Commands.literal("hordeManager").then(Commands.literal("stopEnumHorde")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> stopHorde(context.getSource()))

        ));
    }

    private static int startHorde(CommandSourceStack sourceStack) {
        if (!sourceStack.getLevel().isClientSide && VillainousHordeLibrary.forgeTestEnumHorde != null && !VillainousHordeLibrary.forgeTestEnumHorde.getHordeActive())
            VillainousHordeLibrary.forgeTestEnumHorde.SetUpHorde(Objects.requireNonNull(sourceStack.getPlayer()));
        return 0;
    }

    private static int stopHorde(CommandSourceStack sourceStack) {
        if (!sourceStack.getLevel().isClientSide && VillainousHordeLibrary.forgeTestEnumHorde != null && VillainousHordeLibrary.forgeTestEnumHorde.getHordeActive())
            VillainousHordeLibrary.forgeTestEnumHorde.Stop(EntityEnumHorde.HordeStopReasons.DEFEAT);
        return 0;
    }
}
