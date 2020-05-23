package net.silthus.inventorykeeper.config;

import net.silthus.inventorykeeper.api.FilterMode;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InventoryConfig")
public class InventoryConfigTest {

    private InventoryConfig config;

    @BeforeEach
    public void beforeEach() {
        config = new InventoryConfig(new File("src/test/resources/configs", "test-config1.yaml").toPath());
        config.load();
    }

    @Nested
    @DisplayName("getMode()")
    public class GetMode {

        @Test
        @DisplayName("should default to WHITELIST")
        public void shouldDefaultToWhitelist() {

            assertThat(config.getMode()).isEqualTo(FilterMode.WHITELIST);
        }

        @Test
        @DisplayName("should use custom defined mode")
        public void shouldUseCustomMode() {

            config = new InventoryConfig(new File("src/test/resources/configs", "test-config2.yaml").toPath());
            config.load();

            assertThat(config.getMode()).isEqualTo(FilterMode.BLACKLIST);
        }
    }

    @Nested
    @DisplayName("getItemGroups()")
    public class GetItemGroups {

        @Test
        @DisplayName("should return empty list if not configured")
        public void shouldReturnEmptyList() {

            assertThat(config.getItemGroups())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("should return configured item groups")
        public void shouldReturnConfiguredItemGroups() {

            config = new InventoryConfig(new File("src/test/resources/configs", "test-config2.yaml").toPath());
            config.load();

            assertThat(config.getItemGroups())
                    .containsExactly("foo", "bar");
        }
    }


    @Nested
    @DisplayName("getItems()")
    public class GetItems {

        @Test
        @DisplayName("returns all items as materials")
        public void loadsItemsFromConfig() {

            assertThat(config.getItems())
                    .containsExactly(
                            Material.DIRT,
                            Material.STONE,
                            Material.GRAVEL
                    );
        }

        @Test
        @DisplayName("should return empty item list")
        public void shouldReturnEmptyList() {

            config = new InventoryConfig(new File("src/test/resources/configs", "test-config3.yaml").toPath());
            config.loadAndSave();

            assertThat(config.getItems())
                    .isNotNull()
                    .isEmpty();
        }
    }
}