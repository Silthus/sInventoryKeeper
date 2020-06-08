package net.silthus.inventorykeeper.api;

import lombok.Getter;
import net.silthus.inventorykeeper.filter.BlacklistInventoryFilter;
import net.silthus.inventorykeeper.filter.WhitelistInventoryFilter;

public enum FilterMode {

    WHITELIST(BlacklistInventoryFilter.class),
    BLACKLIST(WhitelistInventoryFilter.class);

    @Getter
    private final Class<? extends InventoryFilter> filterClass;

    FilterMode(Class<? extends InventoryFilter> filterClass) {
        this.filterClass = filterClass;
    }
}
