package net.silthus.inventorykeeper.filter;

import com.google.inject.Inject;
import net.silthus.inventorykeeper.InventoryManager;
import net.silthus.inventorykeeper.api.ConfiguredInventoryFilter;
import net.silthus.inventorykeeper.api.FilterType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

@FilterType("BLACKLIST")
public class BlacklistInventoryFilter extends ConfiguredInventoryFilter {

    @Inject
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
