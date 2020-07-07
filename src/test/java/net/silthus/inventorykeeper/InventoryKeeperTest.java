package net.silthus.inventorykeeper;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.silthus.inventorykeeper.api.FilterResult;
import net.silthus.inventorykeeper.api.FilterType;
import net.silthus.inventorykeeper.api.FilterRegistrationException;
import net.silthus.inventorykeeper.api.InventoryFilter;
import net.silthus.inventorykeeper.config.ItemGroupConfig;
import net.silthus.inventorykeeper.mock.CustomServerMock;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SKeepInventory Plugin")
public class InventoryKeeperTest {

    private static ServerMock server;
    private InventoryKeeper plugin;
    private InventoryManager inventoryManagerMock;

    @BeforeAll
    public static void beforeAll() {
        server = MockBukkit.mock(new CustomServerMock());
    }

    @BeforeEach
    public void beforeEach() {
        plugin = MockBukkit.loadWith(InventoryKeeper.class, new File("src/test/resources/plugin.yml"));
        inventoryManagerMock = mock(InventoryManager.class);
        plugin.setInventoryManager(inventoryManagerMock);
    }

    @AfterAll
    public static void afterAll() {
        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("registerInventoryFilter(Class<? extends InventoryFilter>)")
    public class registerInventoryFilter {

        @Test
        @DisplayName("should throw FilterRegistrationException if missing @FilterMode annotation")
        public void shouldThrowIfMissingAnnotation() {

            assertThatExceptionOfType(FilterRegistrationException.class)
                    .isThrownBy(() -> plugin.registerInventoryFilter(MissingAnnotationFilter.class));
        }

        @Test
        @DisplayName("should throw FilterRegistrationException if FilterMode already exists")
        public void shouldThrowIfDuplicateFiltersAreRegistered() {

            assertThatCode(() -> plugin.registerInventoryFilter(FilterOne.class))
                    .doesNotThrowAnyException();

            assertThatExceptionOfType(FilterRegistrationException.class)
                    .isThrownBy(() -> plugin.registerInventoryFilter(FilterOneDuplicate.class));

            assertThat(plugin.getFilterTypes())
                    .containsKey("one")
                    .extractingByKey("one")
                    .isEqualTo(FilterOne.class);
        }

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("should allow registration of multiple filters")
        public void shouldAllowRegistrationOfMultipleFilters() {

            assertThatCode(() -> plugin.registerInventoryFilter(FilterOne.class))
                    .doesNotThrowAnyException();
            assertThatCode(() -> plugin.registerInventoryFilter(FilterTwo.class))
                    .doesNotThrowAnyException();

            assertThat(plugin.getFilterTypes())
                    .containsKeys("one", "two")
                    .extractingByKeys("one", "two")
                    .contains(FilterOne.class, FilterTwo.class);
        }

        @Test
        @DisplayName("should be case insensitive")
        public void shouldBeCaseInsensitive() {

            assertThatCode(() -> plugin.registerInventoryFilter(FilterOne.class))
                    .doesNotThrowAnyException();

            assertThatExceptionOfType(FilterRegistrationException.class)
                    .isThrownBy(() -> plugin.registerInventoryFilter(FilterLowerCase.class));

            assertThat(plugin.getFilterTypes())
                    .containsKey("one")
                    .extractingByKey("one")
                    .isEqualTo(FilterOne.class);
        }

        class MissingAnnotationFilter implements InventoryFilter {

            @Override
            public FilterResult filter(ItemStack... items) {
                return null;
            }
        }

        @FilterType("One")
        class FilterOne implements InventoryFilter {

            @Override
            public FilterResult filter(ItemStack... items) {
                return null;
            }
        }

        @FilterType("One")
        class FilterOneDuplicate implements InventoryFilter {
            @Override
            public FilterResult filter(ItemStack... items) {
                return null;
            }
        }

        @FilterType("Two")
        class FilterTwo implements InventoryFilter {
            @Override
            public FilterResult filter(ItemStack... items) {
                return null;
            }
        }

        @FilterType("one")
        class FilterLowerCase implements InventoryFilter {
            @Override
            public FilterResult filter(ItemStack... items) {
                return null;
            }
        }
    }

    @Nested
    @DisplayName("enable()")
    public class Enable {

        File itemGroupsPath;
        File configsPath;

        @BeforeEach
        public void beforeEach() {
            itemGroupsPath = new File(plugin.getDataFolder(), Constants.ITEM_GROUPS_CONFIG_PATH);
            configsPath = new File(plugin.getDataFolder(), Constants.INVENTORY_CONFIG_PATH);
            if (itemGroupsPath.exists()) {
                itemGroupsPath.delete();
            }
            if (configsPath.exists()) {
                configsPath.delete();
            }
        }

        @Test
        @DisplayName("calls InventoryManager.load()")
        public void shouldCallLoadOnInventoryManager() {

            plugin.enable();
            verify(inventoryManagerMock, times(1)).load();
        }

        @Test
        @DisplayName("should copy examples from resources to plugin directory")
        public void shouldCopyExamples() {

            plugin.enable();

            assertThat(configsPath).exists();
            assertThat(configsPath.listFiles())
                    .contains(
                            new File(configsPath, "whitelist-example.yaml"),
                            new File(configsPath, "blacklist-example.yaml")
                    );
        }

        @Test
        @DisplayName("should create default item group configs")
        public void shouldCreateDefaultConfigs() {

            plugin.enable();
            assertThat(itemGroupsPath)
                    .exists()
                    .isDirectory();
            assertThat(itemGroupsPath.listFiles())
                    .contains(
                            new File(itemGroupsPath, "weapons.yaml"),
                            new File(itemGroupsPath, "armor.yaml")
                    );
        }

        @Test
        @DisplayName("should not override existing item group configs")
        public void shouldNotOverrideExistingItemGroupConfig() {

            File file = new File(itemGroupsPath, "weapons.yaml");
            ItemGroupConfig config = new ItemGroupConfig(file.toPath());
            config.setName("foobar");
            config.save();

            plugin.enable();

            ItemGroupConfig itemGroupConfig = new ItemGroupConfig(file.toPath());
            itemGroupConfig.load();

            assertThat(itemGroupConfig.getName()).isEqualTo("foobar");
            assertThat(itemGroupConfig.getItems()).isEmpty();
        }

        @Test
        @DisplayName("should load default filter types")
        public void shouldHaveLoadedTwoFilters() {

            assertThat(plugin.getFilterTypes())
                    .isNotEmpty()
                    .containsKeys("WHITELIST", "BLACKLIST");
        }
    }

}