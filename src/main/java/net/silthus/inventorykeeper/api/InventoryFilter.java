package net.silthus.inventorykeeper.api;

import net.silthus.inventorykeeper.FilterException;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * An {@link InventoryFilter} is used to filter the items
 * that should be dropped or kept upon a {@link org.bukkit.event.entity.PlayerDeathEvent}.
 * <br>
 * All {@link InventoryFilter}s must be annotated with a {@link FilterType}.
 */
@FunctionalInterface
public interface InventoryFilter {

    /**
     * Checks the given items against the config and filters out all items that should be dropped.
     * Only returns the items the player keeps.
     *
     * @param items items that are dropped on death
     * @return the filtered items the player keeps
     * @throws FilterException if the filters fails to filter the result and all filtering should be aborted
     */
    FilterResult filter(ItemStack... items) throws FilterException;
}
