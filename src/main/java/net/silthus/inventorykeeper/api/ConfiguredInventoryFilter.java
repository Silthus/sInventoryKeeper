package net.silthus.inventorykeeper.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.silthus.inventorykeeper.InventoryManager;
import net.silthus.inventorykeeper.config.InventoryConfig;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public abstract class ConfiguredInventoryFilter implements InventoryFilter {

    private final InventoryManager inventoryManager;
    private final Set<Material> itemTypes = new HashSet<>();

    @Override
    public void load(InventoryConfig config) {

        itemTypes.addAll(config.getItems());
        config.getItemGroups().stream()
                .map(getInventoryManager()::getItemGroupMaterials)
                .forEach(itemTypes::addAll);
    }
}
