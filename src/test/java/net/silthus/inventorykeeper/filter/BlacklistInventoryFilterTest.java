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

@DisplayName("Blacklist Inventory Filter")
public class BlacklistInventoryFilterTest {

    private static ServerMock server;
    private BlacklistInventoryFilter filter;
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
        filter = new BlacklistInventoryFilter(inventoryManager);
        filter.getItemTypes().addAll(Arrays.asList(
                Material.WOODEN_SWORD,
                Material.BOW
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
        @DisplayName("should return an empty list if all items match")
        public void shouldReturnEmptyListIfWhitelistAndNoMatches() {

            assertThat(filter.filter(items.toArray(new ItemStack[0])))
                    .extracting(FilterResult::isKeepingItems)
                    .isEqualTo(false);
        }

        @Test
        @DisplayName("should keep items that do not match")
        public void shouldMatchMaterialData() {

            items.add(new ItemStack(Material.ACACIA_WOOD, 1));

            FilterResult result = filter.filter(items.toArray(new ItemStack[0]));
            assertThat(result.getKeptItems())
                    .containsExactly(null, null, new ItemStack(Material.ACACIA_WOOD, 1));
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
                    .containsExactly(null, null, stone, stone2, gravel);
        }

        @Test
        @DisplayName("should keep all items if nothing matches")
        public void shouldKeepAllItems() {

            items = Arrays.asList(
                    new ItemStack(Material.STONE, 20),
                    new ItemStack(Material.GRAVEL, 55)
            );

            FilterResult result = filter.filter(items.toArray(new ItemStack[0]));
            assertThat(result.getKeptItems())
                    .hasSize(2)
                    .containsExactly(items.toArray(new ItemStack[0]));
        }
    }
}