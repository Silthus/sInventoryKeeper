package net.silthus.inventorykeeper.config;

import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ItemGroupConfig")
public class ItemGroupConfigTest {

    private ItemGroupConfig config;

    @BeforeEach
    public void beforeEach() {
        config = new ItemGroupConfig(new File("src/test/resources/item-groups", "test1.yaml").toPath());
        config.load();
    }

    @Nested
    @DisplayName("getName()")
    public class GetName {

        @Test
        @DisplayName("should be empty")
        public void shouldBeTheFileName() {

            assertThat(config.getName()).isEqualTo("");
        }

        @Test
        @DisplayName("should use custom name if set")
        public void shouldUseCustomNameIfSet() {

            config = new ItemGroupConfig(new File("src/test/resources/item-groups", "test2.yaml").toPath());
            config.load();

            assertThat(config.getName()).isEqualTo("foobar");
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
                            Material.STONE,
                            Material.DIRT,
                            Material.LAPIS_ORE,
                            Material.BIRCH_WOOD,
                            Material.GRAVEL
                    );
        }
    }
}