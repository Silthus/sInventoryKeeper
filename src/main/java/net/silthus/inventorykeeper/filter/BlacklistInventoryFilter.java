package net.silthus.inventorykeeper.filter;

import net.silthus.inventorykeeper.InventoryManager;
import net.silthus.inventorykeeper.api.ConfiguredInventoryFilter;
import net.silthus.inventorykeeper.api.Filter;
import net.silthus.inventorykeeper.api.FilterMode;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

@Filter(FilterMode.BLACKLIST)
public class BlacklistInventoryFilter extends ConfiguredInventoryFilter {

    public BlacklistInventoryFilter(InventoryManager inventoryManager) {
        super(inventoryManager);
    }

    @Override
    public List<ItemStack> filter(List<ItemStack> items) {

        return items.stream()
                .filter(itemStack -> !getItemTypes().contains(itemStack.getType()))
                .collect(Collectors.toList());
    }
}
