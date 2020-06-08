package net.silthus.inventorykeeper;

import com.google.inject.Binder;
import com.google.inject.Module;

public class InventoryKeeperModule implements Module {

    private final SKeepInventory plugin;

    InventoryKeeperModule(SKeepInventory plugin) {
        this.plugin = plugin;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(SKeepInventory.class).toInstance(plugin);
    }
}
