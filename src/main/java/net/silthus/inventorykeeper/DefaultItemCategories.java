package net.silthus.inventorykeeper;

import net.silthus.inventorykeeper.config.ItemGroupConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class DefaultItemCategories {

    @DefaultItemGroup
    public static List<String> WEAPONS = asNamespacedKeys(
            // swords
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.DIAMOND_SWORD,
            Material.GOLDEN_SWORD,
            // axes
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.DIAMOND_AXE,
            Material.GOLDEN_AXE,
            // trident
            Material.TRIDENT,
            // bow
            Material.BOW,
            // crossbow
            Material.CROSSBOW,
            // arrows
            Material.ARROW,
            Material.SPECTRAL_ARROW,
            Material.TIPPED_ARROW,
            // shield
            Material.SHIELD
    );

    @DefaultItemGroup
    public static List<String> ARMOR = asNamespacedKeys(
            // leather
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
            // chainmail
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,
            // iron
            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,
            // diamond
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            // gold
            Material.GOLDEN_HELMET,
            Material.GOLDEN_CHESTPLATE,
            Material.GOLDEN_LEGGINGS,
            Material.GOLDEN_BOOTS
    );

    @SuppressWarnings("unchecked")
    public static List<ItemGroupConfig> getDefaultConfigs(File basePath) {

        return Arrays.stream(DefaultItemCategories.class.getFields())
                .filter(field -> field.isAnnotationPresent(DefaultItemGroup.class))
                .collect(toMap(field -> field.getName().toLowerCase(), field -> {
                    try {
                        return (List<String>) field.get(null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                }))
                .entrySet().stream()
                .map(entry -> {
                    String id = entry.getKey().toLowerCase();
                    ItemGroupConfig itemGroupConfig = new ItemGroupConfig(new File(basePath, id + ".yaml").toPath());
                    itemGroupConfig.setName(id);
                    itemGroupConfig.setItems(entry.getValue());
                    return itemGroupConfig;
                })
                .collect(Collectors.toList());
    }

    private static List<String> asNamespacedKeys(Material... items) {
        return Arrays.stream(items)
                .map(Material::getKey)
                .map(NamespacedKey::toString)
                .collect(Collectors.toList());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DefaultItemGroup {

    }
}
