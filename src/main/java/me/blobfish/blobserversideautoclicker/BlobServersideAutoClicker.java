package me.blobfish.blobserversideautoclicker;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlobServersideAutoClicker extends JavaPlugin {

    static BlobServersideAutoClicker plugin;

    static long maxinterval;
    static long mininterval;
    @Override
    public void onEnable() {
        plugin = this;

        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        maxinterval = config.getLong("max-interval");
        mininterval = config.getLong("min-interval");

        getCommand("autoclicker").setExecutor(new CommandAutoclicker());
        getServer().getPluginManager().registerEvents(new EventStuff(), this);
    }

}

