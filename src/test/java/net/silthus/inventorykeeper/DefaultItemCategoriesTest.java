package net.silthus.inventorykeeper;

import net.silthus.inventorykeeper.config.ItemGroupConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DefaultItemCategories")
public class DefaultItemCategoriesTest {

    private File path;

    @BeforeEach
    public void beforeEach() {

        path = new File("defaults");
    }

    @Nested
    @DisplayName("getDefaultConfigs()")
    public class GetDefaultConfigs {

        @Test
        @DisplayName("should return a list of configs with the field name")
        public void shouldReturnAMapOfAllTaggedFields() {

            List<Field> fields = Arrays.stream(DefaultItemCategories.class.getFields())
                    .filter(field -> field.isAnnotationPresent(DefaultItemCategories.DefaultItemGroup.class))
                    .collect(Collectors.toList());

            List<ItemGroupConfig> defaultConfigs = DefaultItemCategories.getDefaultConfigs(path);

            assertThat(defaultConfigs)
                    .isNotNull()
                    .hasSize(fields.size())
                    .anyMatch(config -> config.getName().equals(fields.get(0).getName().toLowerCase()));
        }
    }
}