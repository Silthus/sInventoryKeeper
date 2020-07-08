package net.silthus.inventorykeeper;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.Binder;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.silthus.inventorykeeper.api.FilterType;
import net.silthus.inventorykeeper.api.FilterRegistrationException;
import net.silthus.inventorykeeper.api.InventoryFilter;
import net.silthus.inventorykeeper.commands.InventoryKeeperCommands;
import net.silthus.inventorykeeper.config.InventoryConfig;
import net.silthus.inventorykeeper.filter.BlacklistInventoryFilter;
import net.silthus.inventorykeeper.filter.WhitelistInventoryFilter;
import net.silthus.inventorykeeper.injection.FilterDiscoveryModule;
import net.silthus.inventorykeeper.injection.InventoryKeeperModule;
import net.silthus.inventorykeeper.listener.PlayerListener;
import net.silthus.slib.bukkit.BasePlugin;
import net.silthus.slib.configlib.configs.yaml.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@PluginMain
public class InventoryKeeper extends BasePlugin {

    @Getter(AccessLevel.PACKAGE)
    private final Map<String, Class<? extends InventoryFilter>> filterTypes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    @Inject
    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private InventoryManager inventoryManager;
    @Inject
    private PlayerListener playerListener;
    private BukkitCommandManager commandManager;
    @Inject
    private InventoryKeeperCommands commands;

    public InventoryKeeper() {
        registerInventoryFilters();
    }

    public InventoryKeeper(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);

        registerInventoryFilters();
    }

    public void reload() {
        getInventoryManager().reload();
    }

    private void registerInventoryFilters() {
        try {
            registerInventoryFilter(WhitelistInventoryFilter.class);
            registerInventoryFilter(BlacklistInventoryFilter.class);
        } catch (FilterRegistrationException e) {
            getLogger().warning(e.getMessage());
        }
    }

    @Override
    public void enable() {

        commandManager = new BukkitCommandManager(this);
        commandManager.registerCommand(commands);

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
        binder.install(new FilterDiscoveryModule(filterTypes));
    }

    /**
     * Registers the given class as an {@link InventoryFilter} which will be called to filter items when a player dies.
     * The filter must be annotated with @{@link FilterType}.
     * <br>
     * Important: register your filters in your {@link JavaPlugin#onLoad()} method before your plugin is enabled!
     *
     * @param filterType class of the filter to register
     * @throws FilterRegistrationException is thrown if the filter could not be registered. Possible reasons are:
     *                                     <ul>
     *                                         <li>Missing @{@link FilterType}</li>
     *                                         <li>Duplicate filter with the same {@link FilterType}</li>
     *                                     </ul>
     */
    public void registerInventoryFilter(Class<? extends InventoryFilter> filterType) throws FilterRegistrationException {

        if (!filterType.isAnnotationPresent(FilterType.class)) {
            throw new FilterRegistrationException(filterType.getCanonicalName() + " is missing the @FilterMode annotation and cannot be registered.");
        }

        String filterMode = filterType.getAnnotation(FilterType.class).value();

        if (filterTypes.containsKey(filterMode)) {
            throw new FilterRegistrationException("Failed to register " + filterType.getCanonicalName() + ". " +
                    "A duplicate filter " + filterTypes.get(filterMode).getCanonicalName() + " with the filter mode " + filterMode + " already exists.");
        }

        filterTypes.put(filterMode, filterType);
        getLogger().info("registered InventoryFilter " + filterType.getCanonicalName() + " as type: " + filterMode);
    }

    private void copyExamples() {

        File configPath = new File(getDataFolder(), Constants.INVENTORY_CONFIG_PATH);

        InventoryConfig whitelistConfig = new InventoryConfig(new File(configPath, "whitelist-example.yaml").toPath());
        whitelistConfig.setMode("WHITELIST");
        whitelistConfig.setEnabled(true);
        whitelistConfig.setItemGroups(Collections.singletonList("armor"));
        whitelistConfig.setItems(Arrays.asList("stone", "dirt"));
        whitelistConfig.loadAndSave();

        InventoryConfig blacklistConfig = new InventoryConfig(new File(configPath, "blacklist-example.yaml").toPath());
        blacklistConfig.setMode("BLACKLIST");
        blacklistConfig.setEnabled(true);
        blacklistConfig.setItemGroups(Collections.singletonList("weapons"));
        blacklistConfig.loadAndSave();
    }

    private void createDefaultItemGroups() {
        DefaultItemCategories.getDefaultConfigs(new File(getDataFolder(), Constants.ITEM_GROUPS_CONFIG_PATH))
                .forEach(YamlConfiguration::loadAndSave);
    }
}
