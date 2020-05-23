package net.silthus.inventorykeeper;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.silthus.inventorykeeper.config.ItemGroupConfig;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("SKeepInventory Plugin")
public class SKeepInventoryTest {

    private static ServerMock server;
    private SKeepInventory plugin;
    private InventoryManager inventoryManagerMock;

    @BeforeAll
    public static void beforeAll() {
        server = MockBukkit.mock();
    }

    @BeforeEach
    public void beforeEach() {
        plugin = MockBukkit.loadWith(SKeepInventory.class, new File("src/test/resources/plugin.yml"));
        inventoryManagerMock = mock(InventoryManager.class);
        plugin.setInventoryManager(inventoryManagerMock);
    }

    @AfterAll
    public static void afterAll() {
        MockBukkit.unmock();
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
    }

}