package net.silthus.inventorykeeper.injection;

import com.google.inject.Binder;
import com.google.inject.Module;
import net.silthus.inventorykeeper.InventoryKeeper;

public class InventoryKeeperModule implements Module {

    private final InventoryKeeper plugin;

    public InventoryKeeperModule(InventoryKeeper plugin) {
        this.plugin = plugin;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(InventoryKeeper.class).toInstance(plugin);
    }
}
