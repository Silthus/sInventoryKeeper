package net.silthus.inventorykeeper.injection;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import net.silthus.inventorykeeper.api.InventoryFilter;

import java.util.Map;

public class FilterDiscoveryModule extends AbstractModule {

    private final Map<String, Class<? extends InventoryFilter>> filters;

    public FilterDiscoveryModule(Map<String, Class<? extends InventoryFilter>> filters) {
        this.filters = filters;
    }

    @Override
    public void configure() {

        MapBinder<String, InventoryFilter> mapBinder = MapBinder.newMapBinder(binder(), String.class, InventoryFilter.class);

        for (Map.Entry<String, Class<? extends InventoryFilter>> entry : filters.entrySet()) {
            mapBinder.addBinding(entry.getKey()).to(entry.getValue());
        }
    }
}
