package net.silthus.inventorykeeper.config;

import lombok.Getter;
import lombok.Setter;
import net.silthus.slib.configlib.configs.yaml.BukkitYamlConfiguration;
import net.silthus.slib.configlib.format.FieldNameFormatters;
import org.bukkit.Material;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class ItemGroupConfig extends BukkitYamlConfiguration {

    private String name = "";
    private List<String> items = new ArrayList<>();

    public ItemGroupConfig(Path path) {
        super(path, BukkitYamlProperties.builder().setFormatter(FieldNameFormatters.LOWER_UNDERSCORE).build());
    }

    public List<Material> getItems() {
        return items.stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
