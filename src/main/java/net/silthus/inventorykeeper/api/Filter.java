package net.silthus.inventorykeeper.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tag your {@link InventoryFilter} with a mode.
 * When the mode is defined in the config your filter will be created.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Filter {

    FilterMode value();
}