package net.silthus.inventorykeeper.api;

import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
public class FilterResult {

    private final ItemStack[] keptItems;
    private final List<ItemStack> drops;

    public FilterResult() {
        this.keptItems = new ItemStack[0];
        this.drops = new ArrayList<>();
    }

    public FilterResult(ItemStack[] keptItems, List<ItemStack> drops) {
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
    public FilterResult combine(ItemStack[] keptItems, List<ItemStack> drops) {

        ArrayList<ItemStack> keepers = new ArrayList<>();

        for (int i = 0; i < this.keptItems.length; i++) {
            keepers.add(i, this.keptItems[i]);
        }

        for (int i = 0; i < keptItems.length; i++) {
            keepers.add(i, keptItems[i]);
        }

        ArrayList<ItemStack> newDrops = new ArrayList<>();
        newDrops.addAll(this.drops);
        newDrops.addAll(drops);

        return new FilterResult(keepers.toArray(new ItemStack[0]), newDrops);
    }

    public FilterResult combine(FilterResult result) {
        return combine(result.getKeptItems(), result.getDrops());
    }

    public boolean isKeepingItems() {
        return Arrays.stream(getKeptItems())
                .filter(Objects::nonNull)
                .anyMatch(itemStack -> itemStack.getType() != Material.AIR);
    }
}
