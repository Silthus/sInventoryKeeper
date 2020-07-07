package net.silthus.inventorykeeper;

import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig {

    private final FileConfiguration configuration;

    public PluginConfig(FileConfiguration configuration) {
        this.configuration = configuration;
    }
}
