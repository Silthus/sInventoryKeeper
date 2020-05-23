package net.silthus.inventorykeeper.api;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("InventoryFilter")
public class InventoryFilterTest {

    @Nested
    @DisplayName("default load()")
    public class Load {

        @Test
        @DisplayName("should do nothing")
        public void shouldDoNothing() {

            assertThatCode(() -> new TestFilter().load(null))
                    .doesNotThrowAnyException();
        }
    }

    public static class TestFilter implements InventoryFilter {
        @Override
        public List<ItemStack> filter(List<ItemStack> items) {
            return null;
        }
    }

}