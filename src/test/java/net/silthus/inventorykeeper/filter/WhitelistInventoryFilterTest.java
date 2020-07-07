package net.silthus.inventorykeeper.filter;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.silthus.inventorykeeper.InventoryManager;
import net.silthus.inventorykeeper.api.FilterResult;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Whitelist Inventory Filter")
public class WhitelistInventoryFilterTest {

    private static ServerMock server;
    private WhitelistInventoryFilter filter;
    private InventoryManager inventoryManager;

    @BeforeAll
    public static void beforeAll() {
        server = MockBukkit.mock();
    }

    @AfterAll
    public static void afterAll() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void beforeEach() {
        inventoryManager = mock(InventoryManager.class);
        filter = new WhitelistInventoryFilter(inventoryManager);
        filter.getItemTypes().addAll(Arrays.asList(
                Material.ACACIA_WOOD,
                Material.STONE,
                Material.GRAVEL
        ));
    }

    @Nested
    @DisplayName("filter(List<ItemStack>)")
    public class FilterItems {

        private List<ItemStack> items;

        @BeforeEach
        public void beforeEach() {
            items = new ArrayList<>(Arrays.asList(
                    new ItemStack(Material.WOODEN_SWORD, 1),
                    new ItemStack(Material.BOW, 1)
            ));
        }

        @Test
        @DisplayName("should return an empty list if no items match")
        public void shouldReturnEmptyListIfWhitelistAndNoMatches() {

            FilterResult result = filter.filter(items.toArray(new ItemStack[0]));
            assertThat(result.getKeptItems())
                    .containsExactly(null, null);
        }

        @Test
        @DisplayName("should not filter same material but different data")
        public void shouldMatchMaterialData() {

            items.add(new ItemStack(Material.ACACIA_WOOD, 1));

            FilterResult result = filter.filter(items.toArray(new ItemStack[0]));
            assertThat(result.getKeptItems())
                    .containsExactly(null, null, new ItemStack(Material.ACACIA_WOOD, 1));
        }

        @Test
        @DisplayName("should not filter matching items")
        public void shouldNotFilterMatchingItems() {

            ItemStack stone = new ItemStack(Material.STONE, 1);
            ItemStack gravel = new ItemStack(Material.GRAVEL, 20);

            items.add(stone);
            items.add(gravel);

            FilterResult result = filter.filter(items.toArray(new ItemStack[0]));
            assertThat(result.getKeptItems())
                    .containsExactly(null, null, stone, gravel);
        }

        @Test
        @DisplayName("should keep items as separate stacks")
        public void shouldKeepItemsSeparated() {

            ItemStack stone = new ItemStack(Material.STONE, 20);
            ItemStack stone2 = new ItemStack(Material.STONE, 40);
            ItemStack gravel = new ItemStack(Material.GRAVEL, 20);

            items.add(stone);
            items.add(stone2);
            items.add(gravel);

            FilterResult result = filter.filter(items.toArray(new ItemStack[0]));
            assertThat(result.getKeptItems())
                    .hasSize(5)
                    .contains(null, null, stone, stone2, gravel);
        }
    }
}