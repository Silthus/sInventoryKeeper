package net.silthus.inventorykeeper.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

@Getter
@EqualsAndHashCode(of = {"keptItems", "drops"})
public class FilterResult {

    private final ItemStack[] keptItems;
    private final ItemStack[] drops;

    public FilterResult() {
        this.keptItems = new ItemStack[0];
        this.drops = new ItemStack[0];
    }

    public FilterResult(ItemStack[] keptItems, ItemStack[] drops) {
        this.keptItems = keptItems;
        this.drops = drops;
    }

    /**
     * Combines this {@link FilterResult} with the given items.
     *
     * @param keptItems items to keep
     * @param drops items to drop
     * @return a combined {@link FilterResult}
     */
    public FilterResult combine(ItemStack[] keptItems, ItemStack[] drops) {

        return new FilterResult(combineItems(this.keptItems, keptItems), combineItems(this.drops, drops));
    }

    public FilterResult combine(FilterResult result) {
        return combine(result.getKeptItems(), result.getDrops());
    }

    /**
     * Compares all kept items to the drops and removes
     * duplicate items from one or the other array depending on the {@link CleanupMode}.
     */
    public FilterResult cleanupDuplicates(CleanupMode mode) {

        ItemStack[] keptItems = Arrays.copyOf(getKeptItems(), Math.max(getKeptItems().length, getDrops().length));
        ItemStack[] drops = Arrays.copyOf(getDrops(), Math.max(getKeptItems().length, getDrops().length));

        for (int i = 0; i < keptItems.length; i++) {
            switch (mode) {
                case DROP_ITEMS:
                    if (drops[i] != null) keptItems[i] = null;
                    break;
                case KEEP_ITEMS:
                default:
                    if (keptItems[i] != null) drops[i] = null;
                    break;
            }
        }

        return new FilterResult(keptItems, drops);
    }

    public boolean isKeepingItems() {
        return Arrays.stream(getKeptItems())
                .filter(Objects::nonNull)
                .anyMatch(itemStack -> itemStack.getType() != Material.AIR);
    }

    private ItemStack[] combineItems(ItemStack[] items1, ItemStack[] items2) {

        ItemStack[] result = new ItemStack[Math.max(items1.length, items2.length)];

        for (int i = 0; i < result.length; i++) {
            if (items1.length > i && items1[i] != null) {
                result[i] = items1[i];
            }
            if (result[i] == null && items2.length > i && items2[i] != null) {
                result[i] = items2[i];
            }
        }

        return result;
    }

    public enum CleanupMode {

        /**
         * Keeping items has a higher priority.
         * This mode will remove any kept items from the drops.
         */
        KEEP_ITEMS,
        /**
         * Doping items has a higher priority.
         * This mode will remove any dropped items from the kept items.
         */
        DROP_ITEMS;
    }
}
