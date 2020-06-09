package net.silthus.inventorykeeper.listener;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.silthus.inventorykeeper.InventoryManager;
import net.silthus.inventorykeeper.InventoryKeeper;
import net.silthus.inventorykeeper.mock.CustomServerMock;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("PlayerListener")
public class PlayerListenerTest {

    private static ServerMock server;
    private static InventoryKeeper plugin;
    private InventoryManager inventoryManager;
    private PlayerListener listener;

    @BeforeAll
    public static void beforeAll() {
        server = MockBukkit.mock(new CustomServerMock());
        plugin = MockBukkit.loadWith(InventoryKeeper.class, new File("src/test/resources/plugin.yml"));
    }

    @AfterAll
    public static void afterAll() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void beforeEach() {
        inventoryManager = mock(InventoryManager.class);
        listener = new PlayerListener(inventoryManager);
        server.getPluginManager().registerEvents(listener, plugin);
    }

    @AfterEach
    public void afterEach() {
        HandlerList.unregisterAll(listener);
    }

    @Nested
    @DisplayName("onPlayerDeath(PlayerDeathEvent)")
    public class OnDeath {

        private Player player;

        @BeforeEach
        public void beforeEach() {
            player = server.addPlayer();
        }

        @Test
        @DisplayName("should not keep inventory if no items are kept")
        public void shouldSetKeepInventoryToFalse() {

            when(inventoryManager.filterDroppedItems(any(), anyList())).thenReturn(new ArrayList<>());

            List<ItemStack> drops = new ArrayList<>(Arrays.asList(
                    new ItemStack(Material.DIRT, 20),
                    new ItemStack(Material.STONE, 50)
            ));

            PlayerDeathEvent event = new PlayerDeathEvent(player, drops, 0, "");
            listener.onDeath(event);

            assertThat(event.getKeepInventory()).isFalse();
            assertThat(event.getDrops()).containsExactly(drops.toArray(new ItemStack[0]));
        }

        @Test
        @DisplayName("should keep inventory if items are kept")
        public void shouldSetKeepInventoryToTrueIfItemsAreKept() {

            when(inventoryManager.filterDroppedItems(any(), anyList())).thenReturn(Arrays.asList(
                    new ItemStack(Material.COBBLESTONE, 20),
                    new ItemStack(Material.DIRT, 10)
            ));

            List<ItemStack> drops = new ArrayList<>(Arrays.asList(
                    new ItemStack(Material.DIRT, 20),
                    new ItemStack(Material.STONE, 50)
            ));

            PlayerDeathEvent event = new PlayerDeathEvent(player, drops, 0, "");
            listener.onDeath(event);

            assertThat(event.getKeepInventory()).isTrue();
        }

        @Test
        @DisplayName("should remove dropped items from inventory")
        public void shouldRemoveDroppedItemsFromInventory() {

            List<ItemStack> keptItems = Arrays.asList(
                    new ItemStack(Material.COBBLESTONE, 20),
                    new ItemStack(Material.DIRT, 10)
            );
            when(inventoryManager.filterDroppedItems(any(), anyList())).thenReturn(keptItems);

            List<ItemStack> drops = new ArrayList<>(Arrays.asList(
                    new ItemStack(Material.DIRT, 20),
                    new ItemStack(Material.STONE, 50)
            ));

            player.getInventory().addItem(keptItems.toArray(new ItemStack[0]));
            player.getInventory().addItem(drops.toArray(new ItemStack[0]));

            PlayerDeathEvent event = new PlayerDeathEvent(player, drops, 0, "");
            listener.onDeath(event);

            assertThat(event.getEntity().getInventory())
                    .containsOnly(keptItems.toArray(new ItemStack[0]));
        }
    }

}