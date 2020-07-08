package net.silthus.inventorykeeper;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.google.inject.Provider;
import lombok.SneakyThrows;
import net.silthus.inventorykeeper.api.FilterResult;
import net.silthus.inventorykeeper.api.InventoryFilter;
import net.silthus.inventorykeeper.config.InventoryConfig;
import net.silthus.inventorykeeper.config.ItemGroupConfig;
import net.silthus.inventorykeeper.filter.BlacklistInventoryFilter;
import net.silthus.inventorykeeper.filter.WhitelistInventoryFilter;
import net.silthus.inventorykeeper.mock.CustomServerMock;
import org.apache.commons.lang.RandomStringUtils;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.bukkit.Material;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@DisplayName("InventoryManager")
public class InventoryManagerTest {

    private static ServerMock server;
    private static InventoryKeeper plugin;

    private InventoryManager manager;

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
        Map<String, Provider<InventoryFilter>> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Provider<InventoryFilter> whitelistFilter = (Provider<InventoryFilter>) mock(Provider.class);
        Provider<InventoryFilter> blacklistFilter = (Provider<InventoryFilter>) mock(Provider.class);
        map.put("whitelist", whitelistFilter);
        map.put("blacklist", blacklistFilter);
        manager = new InventoryManager(plugin, map);
        FileConfiguration configuration = mock(FileConfiguration.class);
        manager.config = new PluginConfig(configuration);
        when(configuration.getString("combination_mode")).thenReturn(FilterResult.CleanupMode.KEEP_ITEMS.name());
        MemoryConfiguration messages = new MemoryConfiguration();
        messages.set("on_death_keep_items", "");
        messages.set("on_death_drop_all", "");
        when(configuration.getConfigurationSection("messages")).thenReturn(messages);
        when(configuration.isConfigurationSection("messages")).thenReturn(true);

        when(whitelistFilter.get()).thenReturn(new WhitelistInventoryFilter(manager));
        when(blacklistFilter.get()).thenReturn(new BlacklistInventoryFilter(manager));
    }

    @Nested
    @DisplayName("load()")
    public class Load {

        private File configPath;

        @BeforeEach
        public void beforeEach() {
            configPath = new File(plugin.getDataFolder(), Constants.INVENTORY_CONFIG_PATH);
            configPath.mkdirs();
        }

        @AfterEach
        public void afterEach() {
            configPath.delete();
        }

        private File getRandomFile(File path) {
            return new File(path, RandomStringUtils.random(10, "abcdefghijklmnopqrstuvwxyz1234567890") + ".yaml");
        }

        @Test
        @DisplayName("should not load disabled configs")
        public void shouldNotLoadDisabledConfigs() {

            File file = getRandomFile(configPath);
            InventoryConfig config = new InventoryConfig(file.toPath());
            config.setEnabled(false);
            config.loadAndSave();

            manager.load();

            assertThat(manager.getInventoryConfigs())
                    .doesNotContainKey(file.getName().replace(".yaml", ""));
        }

        @Test
        @DisplayName("should load item group configs")
        public void shouldLoadItemGroups() {

            manager.load();

            // at least two default configs exist
            assertThat(manager.getItemGroupConfigs())
                    .hasSizeGreaterThan(1);
        }

        @Test
        @DisplayName("should load inventory configs")
        public void shouldLoadInventoryConfigs() {

            for (int i = 0; i < 10; i++) {
                new InventoryConfig(getRandomFile(configPath).toPath()).loadAndSave();
            }

            manager.load();

            assertThat(manager.getInventoryConfigs())
                    .hasSizeGreaterThan(9);
        }

        @Test
        @DisplayName("should create whitelist mode filters")
        public void shouldCreateWhitelistFilters() {

            manager.getInventoryConfigs().put("test-foobar", new InventoryConfig(getRandomFile(configPath).toPath()));

            manager.load();

            assertThat(manager.getInventoryFilters())
                    .containsKey(Constants.PERMISSION_PREFIX + "test-foobar");

            assertThat(manager.getInventoryFilters().values())
                    .extracting("class")
                    .contains(WhitelistInventoryFilter.class);
        }

        @Test
        @DisplayName("should create blacklist mode filters")
        public void shouldCreateBlacklistFilters() {

            InventoryConfig config = new InventoryConfig(getRandomFile(configPath).toPath());
            config.setMode("BLACKLIST");
            manager.getInventoryConfigs().put("test-foobar-black", config);

            manager.load();

            assertThat(manager.getInventoryFilters())
                    .containsKey(Constants.PERMISSION_PREFIX + "test-foobar-black");

            assertThat(manager.getInventoryFilters().values())
                    .extracting("class")
                    .contains(BlacklistInventoryFilter.class);
        }

        @Test
        @DisplayName("should call load() on filter if configurable")
        public void shouldCallLoadIfFilterIsConfigurable() {

            manager.getInventoryConfigs().put("test-foobar", new InventoryConfig(getRandomFile(configPath).toPath()));

            manager.load();

            InventoryFilter filter = manager.getInventoryFilters().get(Constants.PERMISSION_PREFIX + "test-foobar");

            assertThat(filter).isNotNull();
            assertThat(filter)
                    .extracting("itemTypes")
                    .asInstanceOf(InstanceOfAssertFactories.ITERABLE)
                    .isNotEmpty();
        }
    }

    @Nested
    @DisplayName("getItemGroupMaterials(String)")
    public class GetItemGroup {

        @Test
        @DisplayName("should return empty set if group name is null")
        public void shouldReturnEmptySetIfNull() {

            assertThat(manager.getItemGroupMaterials(null))
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("should return empty set if group name is empty")
        public void shouldReturnEmptySetIfEmpty() {

            assertThat(manager.getItemGroupMaterials(""))
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("should return empty set if item group is not found")
        public void shouldReturnEmptySetIfGroupNotFound() {

            assertThat(manager.getItemGroupMaterials("asdasbfgafg"))
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("should return set of materials if group is found")
        public void shouldReturnASetOfMaterialsIfGroupIsFound() {

            ItemGroupConfig itemGroupConfig = new ItemGroupConfig(new File("foobar.yaml").toPath());
            itemGroupConfig.setItems(Arrays.asList(
                    "stone",
                    "dirt",
                    "gravel"
            ));
            manager.getItemGroupConfigs().put("foobar", itemGroupConfig);

            assertThat(manager.getItemGroupMaterials("foobar"))
                    .hasSize(3)
                    .contains(
                            Material.STONE,
                            Material.DIRT,
                            Material.GRAVEL
                    );
        }
    }


    @Nested
    @DisplayName("filterDropedItems(Player, List<ItemStack>)")
    public class FilterItems {

        private Player player;

        @BeforeEach
        public void beforeEach() {

            WhitelistInventoryFilter whitelistFilter = new WhitelistInventoryFilter(manager);
            whitelistFilter.getItemTypes().addAll(Arrays.asList(
                    Material.DIRT,
                    Material.STONE,
                    Material.GRAVEL
            ));
            manager.getInventoryFilters().put("test", whitelistFilter);
            WhitelistInventoryFilter secondFilter = new WhitelistInventoryFilter(manager);
            secondFilter.getItemTypes().add(Material.BEDROCK);
            manager.getInventoryFilters().put("test2", secondFilter);

            player = server.addPlayer();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return empty list if player has no permissions")
        public void shouldReturnEmptyList() {

            FilterResult result = manager.filterItems(player, new ItemStack(Material.WOODEN_AXE), new ItemStack(Material.STONE));
            assertThat(result.getKeptItems())
                    .hasSize(2)
                    .containsOnlyNulls();
            assertThat(result.getDrops())
                    .containsExactly(new ItemStack(Material.WOODEN_AXE), new ItemStack(Material.STONE));
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all kept items")
        public void shouldReturnAllItems() {

            ArrayList<ItemStack> items = new ArrayList<>();
            items.add(new ItemStack(Material.STONE, 20));
            items.add(new ItemStack(Material.DIRT, 64));
            items.add(new ItemStack(Material.GRAVEL, 12));

            player.addAttachment(plugin, "test", true);

            FilterResult result = manager.filterItems(player, items.toArray(new ItemStack[0]));

            assertThat(result.getDrops()).containsOnlyNulls();
            assertThat(result.getKeptItems())
                    .containsExactly(items.toArray(new ItemStack[0]));
        }

        @Test
        @SneakyThrows
        @DisplayName("should only apply configs with matching permissions")
        public void shouldOnlyApplyConfigsWithMatchingPermission() {

            ArrayList<ItemStack> items = new ArrayList<>();
            items.add(new ItemStack(Material.STONE, 20));
            items.add(new ItemStack(Material.DIRT, 64));
            items.add(new ItemStack(Material.GRAVEL, 12));
            items.add(new ItemStack(Material.BEDROCK, 5));

            player.addAttachment(plugin, "test", true);

            FilterResult result = manager.filterItems(player, items.toArray(new ItemStack[0]));
            assertThat(result.getKeptItems())
                    .hasSize(4)
                    .containsNull()
                    .doesNotContain(new ItemStack(Material.BEDROCK, 5));
        }

        @Test
        @SneakyThrows
        @DisplayName("should combine multiple matching configs")
        public void shouldCombineMultipleMatchingConfigs() {

            ArrayList<ItemStack> items = new ArrayList<>();
            items.add(new ItemStack(Material.STONE, 20));
            items.add(new ItemStack(Material.DIRT, 64));
            items.add(new ItemStack(Material.GRAVEL, 12));
            items.add(new ItemStack(Material.BEDROCK, 5));

            player.addAttachment(plugin, "test", true);
            player.addAttachment(plugin, "test2", true);

            FilterResult result = manager.filterItems(player, items.toArray(new ItemStack[0]));

            assertThat(result.getDrops()).containsOnlyNulls();
            assertThat(result.getKeptItems())
                    .containsExactly(items.toArray(new ItemStack[0]));
        }

        @Test
        @SneakyThrows
        @DisplayName("should combine multiple matching configs with drops and kept items")
        public void shouldCombineMultipleMatchingConfigsMix() {

            ArrayList<ItemStack> items = new ArrayList<>();
            items.add(new ItemStack(Material.STONE, 20));
            items.add(new ItemStack(Material.DIRT, 64));
            items.add(new ItemStack(Material.GRAVEL, 12));
            items.add(new ItemStack(Material.BEDROCK, 5));
            items.add(new ItemStack(Material.IRON_CHESTPLATE));

            player.addAttachment(plugin, "test", true);
            player.addAttachment(plugin, "test2", true);

            FilterResult result = manager.filterItems(player, items.toArray(new ItemStack[0]));

            assertThat(result.getDrops()).containsExactly(null, null, null, null, new ItemStack(Material.IRON_CHESTPLATE));
            assertThat(result.getKeptItems())
                    .containsExactly(
                            new ItemStack(Material.STONE, 20),
                            new ItemStack(Material.DIRT, 64),
                            new ItemStack(Material.GRAVEL, 12),
                            new ItemStack(Material.BEDROCK, 5),
                            null
                    );
        }

        @Test
        @SneakyThrows
        @DisplayName("should remove kept items from drops reference")
        public void shouldRemoveItemsFromInputReference() {

            List<ItemStack> items = new ArrayList<>();
            items.add(new ItemStack(Material.STONE, 20));
            items.add(new ItemStack(Material.DIRT, 64));
            items.add(new ItemStack(Material.GRAVEL, 12));
            items.add(new ItemStack(Material.BEDROCK, 5));

            player.addAttachment(plugin, "test", true);

            FilterResult result = manager.filterItems(player, items.toArray(new ItemStack[0]));
            assertThat(result.getKeptItems())
                    .containsExactly(
                            new ItemStack(Material.STONE, 20),
                            new ItemStack(Material.DIRT, 64),
                            new ItemStack(Material.GRAVEL, 12),
                            null
                    );
            assertThat(result.getDrops())
                    .containsExactly(null, null, null, new ItemStack(Material.BEDROCK, 5));
        }

        @Test
        @SneakyThrows
        @DisplayName("should remove armor items on death")
        void shouldRemoveDamagedItems() {

            ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
            ItemMeta itemMeta = chestplate.getItemMeta();
            if (itemMeta instanceof Damageable) {
                ((Damageable) itemMeta).setDamage(20);
            }
            chestplate.setItemMeta(itemMeta);

            player.addAttachment(plugin, "test", true);

            FilterResult result = manager.filterItems(player, chestplate);
            assertThat(result.getDrops()).containsExactly(chestplate);
            assertThat(result.getKeptItems()).containsOnlyNulls();
        }

        @Test
        @DisplayName("should not allow combining filters of different types")
        void shouldNotCombineBlacklistAndWhitelistFilter() {

            BlacklistInventoryFilter blacklistFilter = new BlacklistInventoryFilter(manager);
            blacklistFilter.getItemTypes().add(Material.BEDROCK);
            manager.getInventoryFilters().put("test3", blacklistFilter);

            player.addAttachment(plugin, "test", true);
            player.addAttachment(plugin, "test3", true);

            assertThatExceptionOfType(FilterException.class)
                    .isThrownBy(() -> manager.filterItems(player, new ItemStack(Material.PAPER)));
        }
    }

}