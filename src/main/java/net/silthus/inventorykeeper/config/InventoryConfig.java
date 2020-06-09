package net.silthus.inventorykeeper.config;

import lombok.Getter;
import lombok.Setter;
import net.silthus.slib.configlib.annotation.Comment;
import net.silthus.slib.configlib.configs.yaml.BukkitYamlConfiguration;
import net.silthus.slib.configlib.format.FieldNameFormatters;
import org.bukkit.Material;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
public class InventoryConfig extends BukkitYamlConfiguration {

    @Comment({
            ">ou can define two different modes that will control how this config gets loaded.",
            "Allowed values (case sensitive): WHITELIST or BLACKLIST",
            "WHITELIST: only the defined items are kept",
            "BLACKLIST: all items excluding the defined items are kept"
    })
    private String mode = "WHITELIST";

    @Comment("Set this to false if you want to disable the config.")
    private boolean enabled = true;

    @Comment({
            "Define a list of item groups that should be kept or dropped (depending on the mode).",
            "You can create custom item groups inside the item-groups/ folder."
    })
    private List<String> itemGroups = new ArrayList<>();

    @Comment("Define a list of items that should be kept or dropped (depending on the mode).")
    private List<String> items = new ArrayList<>();

    public InventoryConfig(Path path) {
        super(path, BukkitYamlProperties.builder().setFormatter(FieldNameFormatters.LOWER_UNDERSCORE).build());
    }

    public List<Material> getItems() {
        return items.stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
