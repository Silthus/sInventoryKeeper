package net.silthus.inventorykeeper;

import com.google.common.base.Strings;
import lombok.Data;
import net.silthus.inventorykeeper.api.InventoryFilter;
import net.silthus.inventorykeeper.config.InventoryConfig;
import net.silthus.inventorykeeper.config.ItemGroupConfig;
import net.silthus.inventorykeeper.filter.BlacklistInventoryFilter;
import net.silthus.inventorykeeper.filter.WhitelistInventoryFilter;
import net.silthus.slib.config.ConfigUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class InventoryManager {

    private final SKeepInventory plugin;
    private final HashMap<String, ItemGroupConfig> itemGroupConfigs = new HashMap<>();
    private final HashMap<String, InventoryConfig> inventoryConfigs = new HashMap<>();
    private final Map<String, InventoryFilter> inventoryFilters = new HashMap<>();

    public void load() {

        ConfigUtil.loadRecursiveConfigs(plugin, Constants.ITEM_GROUPS_CONFIG_PATH, ItemGroupConfig.class, this::loadItemGroupConfig);
        ConfigUtil.loadRecursiveConfigs(plugin, Constants.INVENTORY_CONFIG_PATH, InventoryConfig.class, this::loadInventoryConfig);

        loadInventoryFilter();
    }

    public void unload() {
        itemGroupConfigs.clear();
        inventoryConfigs.clear();
        inventoryFilters.clear();
    }

    private void loadInventoryFilter() {

        getInventoryConfigs().forEach((key, value) -> {
            String permission = Constants.PERMISSION_PREFIX + key;
            InventoryFilter filter;
            switch (value.getMode()) {
                case BLACKLIST:
                    filter = new BlacklistInventoryFilter(this);
                    inventoryFilters.put(permission, filter);
                    break;
                case WHITELIST:
                default:
                    filter = new WhitelistInventoryFilter(this);
                    inventoryFilters.put(permission, filter);
                    break;
            }
            filter.load(value);
            getPlugin().getLogger().info("loaded " + key + value.getMode() + " filter with permission: " + permission);
        });
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
     *          Empty set if group does not exist.
     */
    public Set<Material> getItemGroupMaterials(String itemGroup) {

        if (Strings.isNullOrEmpty(itemGroup) || !getItemGroupConfigs().containsKey(itemGroup.toLowerCase())) {
            return new HashSet<>();
        }

        return new HashSet<>(getItemGroupConfigs().get(itemGroup.toLowerCase()).getItems());
    }

    /**
     * Filters the dopped items of the player.
     * Separating them into two stacks: the drops and the items that are kept.
     * <br>
     * The items that should be kept are returned and the initial drops list
     * is modified to contain the items that should be dropped.
     * <br>
     * Make sure to pass in the {@link PlayerDeathEvent#getDrops()} reference directly to modify the dropped items.
     *
     * @param player that should have its items filtered
     * @param drops reference from the {@link PlayerDeathEvent}.
     * @return list of items that should be kept and put into the inventory of the player on respawn
     */
    public List<ItemStack> filterDroppedItems(Player player, List<ItemStack> drops) {

        List<InventoryFilter> filters = getInventoryFilters().entrySet().stream()
                .filter(entry -> player.hasPermission(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (filters.isEmpty()) return new ArrayList<>();

        ArrayList<ItemStack> keptItems = new ArrayList<>();

        filters.forEach(filter -> {
            List<ItemStack> itemStacks = filter.filter(drops);
            keptItems.addAll(itemStacks);
            drops.removeAll(itemStacks);
        });

        return keptItems;
    }
}
