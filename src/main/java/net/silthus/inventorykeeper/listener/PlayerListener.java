package net.silthus.inventorykeeper.listener;

import com.google.inject.Inject;
import lombok.Getter;
import net.silthus.inventorykeeper.InventoryManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class PlayerListener implements Listener {

    @Getter
    private final InventoryManager inventoryManager;

    @Inject
    PlayerListener(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {

        List<ItemStack> keptItems = getInventoryManager().filterDroppedItems(event.getEntity(), event.getDrops());

        if (keptItems.isEmpty()) {
            event.setKeepInventory(false);
        } else {
            event.setKeepInventory(true);
            event.getEntity().getInventory().removeItem(event.getDrops().toArray(new ItemStack[0]));
            event.getEntity().sendMessage(ChatColor.YELLOW + "The force is with you and made you keep some items.");
        }
    }
}
