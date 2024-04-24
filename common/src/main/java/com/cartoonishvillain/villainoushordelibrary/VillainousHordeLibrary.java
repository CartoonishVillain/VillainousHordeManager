package com.cartoonishvillain.villainoushordelibrary;

import com.cartoonishvillain.villainoushordelibrary.data.JsonHordeData;
import com.cartoonishvillain.villainoushordelibrary.hordes.EntityEnumHorde;
import com.cartoonishvillain.villainoushordelibrary.hordes.EntityTypeHorde;
import com.cartoonishvillain.villainoushordelibrary.hordes.JsonHorde;
import com.cartoonishvillain.villainoushordelibrary.mixin.LivingGoalAccessor;
import com.cartoonishvillain.villainoushordelibrary.platform.Services;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class VillainousHordeLibrary {

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.

    public static EntityTypeHorde entityTypeHorde;
    public static EntityEnumHorde entityEnumHorde;
    public static HashMap<String, JsonHordeData> gsonHordes = new HashMap<>();
    public static JsonHorde jsonHorde;
    public static void init() {
    }

    public static void loadHordes() throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader("hordeJsonData.json"));
        JsonHordeData[] hordesArray = new Gson().fromJson(reader, JsonHordeData[].class);
        ArrayList<JsonHordeData> hordesArrayList = new ArrayList<>(Arrays.stream(hordesArray).toList());
        for (JsonHordeData hordes : hordesArrayList) {
            gsonHordes.put(hordes.getHordeName(), hordes);
        }
    }

    /**
     * @param entity The entity you'd like to check the horde member status of
     * @return if the entity in question has the horde movement goal or not.
     */
    public static boolean isHordeMember(PathfinderMob entity) {
        GoalSelector mobGoalSelector =((LivingGoalAccessor) entity).cartoonishHordeGetMobGoalSelector();
        Set<WrappedGoal> prioritizedGoals = mobGoalSelector.getAvailableGoals();
        Goal hordeGoal = null;
        for (WrappedGoal prioritizedGoal : prioritizedGoals) {
            if (prioritizedGoal.getGoal() instanceof TypeHordeMovementGoal || prioritizedGoal.getGoal() instanceof EnumHordeMovementGoal || prioritizedGoal.getGoal() instanceof JsonHordeMovementGoal<?>) {
                hordeGoal = prioritizedGoal.getGoal();
                break;
            }
        }
        return hordeGoal != null;
    }
}