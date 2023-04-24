package com.villain.cartoonishhorde.testcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.villain.cartoonishhorde.hordes.EntityEnumHorde;
import com.villain.cartoonishhorde.ForgeCartoonishHorde;
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
        if (!sourceStack.getLevel().isClientSide && ForgeCartoonishHorde.forgeTestEnumHorde != null && !ForgeCartoonishHorde.forgeTestEnumHorde.getHordeActive())
            ForgeCartoonishHorde.forgeTestEnumHorde.SetUpHorde(Objects.requireNonNull(sourceStack.getPlayer()));
        return 0;
    }

    private static int stopHorde(CommandSourceStack sourceStack) {
        if (!sourceStack.getLevel().isClientSide && ForgeCartoonishHorde.forgeTestEnumHorde != null && ForgeCartoonishHorde.forgeTestEnumHorde.getHordeActive())
            ForgeCartoonishHorde.forgeTestEnumHorde.Stop(EntityEnumHorde.HordeStopReasons.DEFEAT);
        return 0;
    }
}
