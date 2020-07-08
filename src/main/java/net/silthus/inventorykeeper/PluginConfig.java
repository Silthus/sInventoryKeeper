package net.silthus.inventorykeeper;

import lombok.AccessLevel;
import lombok.Getter;
import net.silthus.inventorykeeper.api.FilterResult;
import net.silthus.slib.util.EnumUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig {

    @Getter(AccessLevel.PRIVATE)
    private final FileConfiguration configuration;
    @Getter
    private final Messages messages = new Messages();

    public PluginConfig(FileConfiguration configuration) {
        this.configuration = configuration;
    }

    public FilterResult.CleanupMode getFilterMode() {
        return EnumUtils.getEnumFromString(FilterResult.CleanupMode.class, getConfiguration().getString("combination_mode", FilterResult.CleanupMode.KEEP_ITEMS.name()));
    }

    public boolean isIgnoringOp() {
        return getConfiguration().getBoolean("ignore_op", false);
    }

    public boolean isOpKeepingAll() {
        return getConfiguration().getBoolean("op_keep_all", true);
    }

    public class Messages {

        private ConfigurationSection getConfiguration() {
            if (PluginConfig.this.configuration.isConfigurationSection("messages")) {
                return PluginConfig.this.getConfiguration().getConfigurationSection("messages");
            } else {
                return PluginConfig.this.getConfiguration().createSection("messages");
            }
        }

        public String getKeepItemsMessage() {
            return getConfiguration().getString("on_death_keep_items", "&eThe force is with you and you got to keep some of your items.");
        }

        public String getDropAllMessage() {
            return getConfiguration().getString("on_death_drop_all", "");
        }

        public String getKeepAllmessage() {
            return getConfiguration().getString("on_death_keep_all", "&aYou got to keep all of your items.");
        }
    }
}
