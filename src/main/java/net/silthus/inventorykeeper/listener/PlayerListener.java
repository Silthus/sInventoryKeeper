package net.silthus.inventorykeeper.listener;

import lombok.Getter;
import net.silthus.inventorykeeper.InventoryManager;
import net.silthus.inventorykeeper.SKeepInventory;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerListener implements Listener {

    @Getter
    private final SKeepInventory plugin;

    public PlayerListener(SKeepInventory plugin) {
        this.plugin = plugin;
    }

    public InventoryManager getInventoryManager() {
        return getPlugin().getInventoryManager();
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
