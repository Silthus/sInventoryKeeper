package net.silthus.inventorykeeper.filter;

import com.google.inject.Inject;
import net.silthus.inventorykeeper.InventoryManager;
import net.silthus.inventorykeeper.api.ConfiguredInventoryFilter;
import net.silthus.inventorykeeper.api.FilterResult;
import net.silthus.inventorykeeper.api.FilterType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FilterType("WHITELIST")
public class WhitelistInventoryFilter extends ConfiguredInventoryFilter {

    @Inject
    public WhitelistInventoryFilter(InventoryManager inventoryManager) {
        super(inventoryManager);
    }

    @Override
    public FilterResult filter(ItemStack... items) {

        ArrayList<ItemStack> drops = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item == null) continue;
            if (!getItemTypes().contains(item.getType())) {
                items[i] = null;
                drops.add(item);
            }
        }

        return new FilterResult(items, drops);
    }
}
