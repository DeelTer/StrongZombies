package ru.deelter.strongzombies;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Plugin instance;

    public static Plugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("StrongZombies by DeelTer is ready");
        BreakBlocks.runTimer();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
