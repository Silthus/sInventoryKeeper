package net.silthus.inventorykeeper.api;

import net.silthus.inventorykeeper.config.InventoryConfig;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * An {@link InventoryFilter} is used to filter the items
 * that should be dropped or kept upon a {@link org.bukkit.event.entity.PlayerDeathEvent}.
 * <br>
 * All {@link InventoryFilter}s must be annotated with a {@link Filter}.
 */
@FunctionalInterface
public interface InventoryFilter {

    /**
     * This method is called after creating the filter.
     * Use it to load data from the configuration.
     *
     * @param config config that was loaded for this filter
     */
    default void load(InventoryConfig config) {}

    /**
     * Checks the given items against the config and filters out all items that should be dropped.
     * Only returns the items the player keeps.
     *
     * @param items items that are dropped on death
     * @return the filtered items the player keeps
     */
    List<ItemStack> filter(List<ItemStack> items);
}
