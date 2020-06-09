package net.silthus.inventorykeeper.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.silthus.inventorykeeper.InventoryManager;
import net.silthus.inventorykeeper.config.InventoryConfig;
import net.silthus.slib.config.Configured;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public abstract class ConfiguredInventoryFilter implements InventoryFilter, Configured<InventoryConfig> {

    private final InventoryManager inventoryManager;
    private final Set<Material> itemTypes = new HashSet<>();

    @Override
    public @NonNull Class<InventoryConfig> getConfigClass() {
        return InventoryConfig.class;
    }

    @Override
    public void load(InventoryConfig config) {

        itemTypes.addAll(config.getItems());
        config.getItemGroups().stream()
                .map(getInventoryManager()::getItemGroupMaterials)
                .forEach(itemTypes::addAll);
    }
}
