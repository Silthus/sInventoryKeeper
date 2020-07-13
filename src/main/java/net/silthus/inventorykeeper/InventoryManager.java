package net.silthus.inventorykeeper;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.silthus.inventorykeeper.api.FilterResult;
import net.silthus.inventorykeeper.api.InventoryFilter;
import net.silthus.inventorykeeper.config.InventoryConfig;
import net.silthus.inventorykeeper.config.ItemGroupConfig;
import net.silthus.slib.config.ConfigUtil;
import net.silthus.slib.config.Configured;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import javax.inject.Singleton;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;

@Getter
@EqualsAndHashCode(callSuper = false)
@Singleton
public class InventoryManager {

    private final InventoryKeeper plugin;
    private final Map<String, Provider<InventoryFilter>> filterTypes;
    private final Map<String, ItemGroupConfig> itemGroupConfigs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, InventoryConfig> inventoryConfigs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, InventoryFilter> inventoryFilters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    @Getter
    PluginConfig config;

    @Inject
    InventoryManager(InventoryKeeper plugin, Map<String, Provider<InventoryFilter>> filterTypes) {
        this.plugin = plugin;
        this.filterTypes = filterTypes;
    }

    public void reload() {
        unload();
        load();
    }

    public void load() {

        getPlugin().saveDefaultConfig();
        this.config = new PluginConfig(getPlugin().getConfig());

        ConfigUtil.loadRecursiveConfigs(plugin, Constants.ITEM_GROUPS_CONFIG_PATH, ItemGroupConfig.class, this::loadItemGroupConfig);
        ConfigUtil.loadRecursiveConfigs(plugin, Constants.INVENTORY_CONFIG_PATH, InventoryConfig.class, this::loadInventoryConfig);

        loadInventoryFilter();

        registerPermissions();
        registerBstats();
    }

    public void unload() {
        itemGroupConfigs.clear();
        inventoryConfigs.clear();
        inventoryFilters.clear();
    }

    private void registerPermissions() {

        registerDefaultPermission(Constants.KEEP_ALL_ITEMS);
        registerDefaultPermission(Constants.BYPASS_ON_DEATH);

        for (String key : inventoryFilters.keySet()) {
            registerDefaultPermission(key);
        }
    }

    private void registerBstats() {

        // Optional: Add custom charts
        getPlugin().getMetrics().addCustomChart(new Metrics.SimplePie(Constants.Charts.COMBINATION_MODE, () -> getConfig().getFilterMode().name()));
        getPlugin().getMetrics().addCustomChart(new Metrics.AdvancedPie(Constants.Charts.CONFIG_MODE, () -> {
            HashMap<String, Integer> configModes = new HashMap<>();
            for (InventoryConfig config : getInventoryConfigs().values()) {
                configModes.put(config.getMode(), configModes.getOrDefault(config.getMode(), 0) + 1);
            }
            return configModes;
        }));
        getPlugin().getMetrics().addCustomChart(new Metrics.SingleLineChart(Constants.Charts.LOADED_CONFIG_AMOUNT, () -> getInventoryConfigs().size()));
        getPlugin().getMetrics().addCustomChart(new Metrics.SingleLineChart(Constants.Charts.LOADED_ITEM_GROUP_AMOUNT, () -> getItemGroupConfigs().size()));
    }

    private void registerDefaultPermission(String key) {
        try {
            // Create and register permission
            Permission permission = new Permission(key, PermissionDefault.FALSE);
            getServer().getPluginManager().addPermission(permission);
        } catch (IllegalArgumentException e) {
            // Permission already exists, ensure it has the correct PermissionDefault instead
            Permission permission = getServer().getPluginManager().getPermission(key);
            if (permission != null) {
                permission.setDefault(PermissionDefault.FALSE);
            }
        }
    }

    private void loadInventoryFilter() {

        for (Map.Entry<String, InventoryConfig> entry : getInventoryConfigs().entrySet()) {
            String permission = Constants.PERMISSION_PREFIX + entry.getKey();

            String filterMode = entry.getValue().getMode();

            if (!filterTypes.containsKey(filterMode)) {
                getPlugin().getLogger().warning("No filter for filter mode " + filterMode + " registered!");
                continue;
            }

            InventoryFilter filter = filterTypes.get(filterMode).get();
            inventoryFilters.put(permission, filter);

            if (filter instanceof Configured) {
                ((Configured<?>) filter).tryLoad(entry.getValue());
            }

            getPlugin().getLogger().info("loaded " + entry.getKey() + " " + filterMode + " filter with permission: " + permission);
        }
    }

    public void loadItemGroupConfig(String id, File file, ItemGroupConfig config) {
        id = (Strings.isNullOrEmpty(config.getName()) ? id : config.getName()).toLowerCase();

        if (itemGroupConfigs.containsKey(id)) {
            getPlugin().getLogger().warning("duplicate item group detected: " + id);
            return;
        }

        itemGroupConfigs.put(id, config);
        getPlugin().getLogger().info("loaded item group: " + id);
    }

    public void loadInventoryConfig(String id, File file, InventoryConfig config) {

        String relativeConfigPath = getPlugin().getDataFolder().toURI().relativize(file.toURI()).getPath();

        if (inventoryConfigs.containsKey(id)) {
            getPlugin().getLogger().warning("duplicate inventory config detected: " + relativeConfigPath);
            return;
        }

        if (!config.isEnabled()) {
            getPlugin().getLogger().info("not loading disabled config: " + relativeConfigPath);
            return;
        }

        inventoryConfigs.put(id, config);

        getPlugin().getLogger().info("loaded inventory config  \"" + relativeConfigPath + "\" with id: " + id);
    }

    /**
     * Resolves the given name as an {@link ItemGroupConfig} and returns
     * all materials that are configured in that group.
     * <br>
     * Returns an empty set if no item group with that name was found.
     *
     * @param itemGroup name of the item group
     * @return set of configured materials in the item group.
     * Empty set if group does not exist.
     */
    public Set<Material> getItemGroupMaterials(String itemGroup) {

        if (Strings.isNullOrEmpty(itemGroup) || !getItemGroupConfigs().containsKey(itemGroup.toLowerCase())) {
            return new HashSet<>();
        }

        return new HashSet<>(getItemGroupConfigs().get(itemGroup.toLowerCase()).getItems());
    }

    /**
     * Filters given items.
     * Separating them into two stacks: the drops and the items that are kept.
     * The kept items take precedence if multiple {@link InventoryFilter} apply to the same {@link Player}.
     *
     * @param player that should have its items filtered
     * @param items  that you want to split into drops and kept items
     * @return a {@link FilterResult} with the items that should be kept and the ones that should be dropped
     * @throws FilterException when filters fail to match or cannot be combined
     */
    public FilterResult filterItems(Player player, ItemStack... items) throws FilterException {

        List<InventoryFilter> filters = getInventoryFilters().entrySet().stream()
                .filter(entry -> player.hasPermission(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (filters.isEmpty()) return new FilterResult(new ItemStack[items.length], items);

        InventoryFilter inventoryFilter = filters.get(0);
        if (!filters.stream().allMatch(filter -> filter.getClass().equals(inventoryFilter.getClass()))) {
            throw new FilterException("Cannot combine filters of different types.");
        }

        FilterResult result = new FilterResult();

        for (InventoryFilter filter : filters) {
            result = result.combine(filter.filter(items.clone())).cleanupDuplicates(getConfig().getFilterMode());
        }

        return result;
    }
}
