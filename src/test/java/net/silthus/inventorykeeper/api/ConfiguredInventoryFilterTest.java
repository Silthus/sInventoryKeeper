package net.silthus.inventorykeeper.api;

import net.silthus.inventorykeeper.InventoryManager;
import net.silthus.inventorykeeper.config.InventoryConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("ConfiguredInventoryFilter")
public class ConfiguredInventoryFilterTest {

    private InventoryManager inventoryManager;
    private Filter filter;

    @BeforeEach
    public void beforeEach() {
        inventoryManager = mock(InventoryManager.class);
        filter = new Filter(inventoryManager);
    }

    @Nested
    @DisplayName("load(InventoryConfig)")
    public class Load {

        private InventoryConfig config;

        @BeforeEach
        public void beforeEach() {
            config = mock(InventoryConfig.class);
        }

        @Test
        @DisplayName("should load empty list if config is empty")
        public void shouldLoadNothingIfConfigIsEmpty() {

            filter.load(config);

            assertThat(filter.getItemTypes())
                    .isEmpty();
        }

        @Test
        @DisplayName("should resolve item groups from the InventoryManager")
        public void shouldLoadItemGroupsFromInventoryManager() {

            when(config.getItemGroups()).thenReturn(Collections.singletonList("foobar"));

            when(inventoryManager.getItemGroupMaterials("foobar"))
                    .thenReturn(new HashSet<>(Arrays.asList(
                        Material.DIRT,
                        Material.STONE
                    )));

            filter.load(config);

            assertThat(filter.getItemTypes())
                    .hasSize(2)
                    .contains(Material.DIRT, Material.STONE);

            verify(inventoryManager, times(1)).getItemGroupMaterials("foobar");
        }

        @Test
        @DisplayName("should resolve multiple item groups from the InventoryManager")
        public void shouldLoadMultipleItemGroupsFromInventoryManager() {

            when(config.getItemGroups()).thenReturn(Arrays.asList("foo", "bar"));

            when(inventoryManager.getItemGroupMaterials("foo"))
                    .thenReturn(new HashSet<>(Arrays.asList(
                            Material.DIRT,
                            Material.STONE
                    )));
            when(inventoryManager.getItemGroupMaterials("bar"))
                    .thenReturn(new HashSet<>(Arrays.asList(
                            Material.GRAVEL,
                            Material.LAPIS_BLOCK
                    )));

            filter.load(config);

            assertThat(filter.getItemTypes())
                    .hasSize(4)
                    .contains(
                            Material.DIRT,
                            Material.STONE,
                            Material.GRAVEL,
                            Material.LAPIS_BLOCK
                    );

            verify(inventoryManager, times(2)).getItemGroupMaterials(anyString());
        }

        @Test
        @DisplayName("should load direct items from the config")
        public void shouldUseItemsFromConfig() {

            when(config.getItems()).thenReturn(Collections.singletonList(Material.STONE));

            filter.load(config);

            assertThat(filter.getItemTypes())
                    .containsExactly(Material.STONE);
        }

        @Test
        @DisplayName("should combine item groups and direct items")
        public void shouldCombineItemGroupsAndDirectItems() {

            when(config.getItemGroups()).thenReturn(Collections.singletonList("foo"));
            when(config.getItems()).thenReturn(Arrays.asList(Material.STONE, Material.LAPIS_ORE));

            when(inventoryManager.getItemGroupMaterials("foo"))
                    .thenReturn(new HashSet<>(Arrays.asList(
                            Material.DIRT,
                            Material.STONE,
                            Material.GRAVEL
                    )));

            filter.load(config);

            assertThat(filter.getItemTypes())
                    .hasSize(4)
                    .contains(
                            Material.DIRT,
                            Material.STONE,
                            Material.GRAVEL,
                            Material.LAPIS_ORE
                    );
        }
    }


    public static class Filter extends ConfiguredInventoryFilter {

        public Filter(InventoryManager inventoryManager) {
            super(inventoryManager);
        }

        @Override
        public List<ItemStack> filter(List<ItemStack> items) {
            return items;
        }
    }
}