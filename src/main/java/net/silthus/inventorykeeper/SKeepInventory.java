package net.silthus.inventorykeeper;

import com.google.inject.Binder;
import com.google.inject.Inject;
import kr.entree.spigradle.Plugin;
import lombok.AccessLevel;
import lombok.Setter;
import net.silthus.inventorykeeper.api.FilterMode;
import net.silthus.inventorykeeper.config.InventoryConfig;
import net.silthus.inventorykeeper.listener.PlayerListener;
import net.silthus.slib.bukkit.BasePlugin;
import net.silthus.slib.configlib.configs.yaml.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

@Plugin
public class SKeepInventory extends BasePlugin {

    @Inject
    @Setter(AccessLevel.PACKAGE)
    private InventoryManager inventoryManager;
    @Inject
    private PlayerListener playerListener;

    public SKeepInventory() {
    }

    public SKeepInventory(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void enable() {
        copyExamples();
        createDefaultItemGroups();

        inventoryManager.load();

        registerEvents(playerListener);
    }

    @Override
    public void disable() {
        unregisterEvents(playerListener);

        inventoryManager.unload();
    }

    @Override
    public void configure(Binder binder) {

        binder.install(new InventoryKeeperModule(this));
    }

    private void copyExamples() {

        File configPath = new File(getDataFolder(), Constants.INVENTORY_CONFIG_PATH);

        InventoryConfig whitelistConfig = new InventoryConfig(new File(configPath, "whitelist-example.yaml").toPath());
        whitelistConfig.setMode(FilterMode.WHITELIST);
        whitelistConfig.setEnabled(true);
        whitelistConfig.setItemGroups(Collections.singletonList("armor"));
        whitelistConfig.setItems(Arrays.asList("stone", "dirt"));
        whitelistConfig.loadAndSave();

        InventoryConfig blacklistConfig = new InventoryConfig(new File(configPath, "blacklist-example.yaml").toPath());
        blacklistConfig.setMode(FilterMode.BLACKLIST);
        blacklistConfig.setEnabled(true);
        blacklistConfig.setItemGroups(Collections.singletonList("weapons"));
        blacklistConfig.loadAndSave();
    }

    private void createDefaultItemGroups() {
        DefaultItemCategories.getDefaultConfigs(new File(getDataFolder(), Constants.ITEM_GROUPS_CONFIG_PATH))
                .forEach(YamlConfiguration::loadAndSave);
    }

}
