package net.silthus.inventorykeeper.listener;

import com.google.inject.Inject;
import lombok.Getter;
import net.silthus.inventorykeeper.InventoryManager;
import net.silthus.inventorykeeper.api.FilterResult;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import javax.inject.Singleton;

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

        Player player = event.getEntity();
        ItemStack[] inventoryItems = player.getInventory().getStorageContents();
        ItemStack[] extraContents = player.getInventory().getExtraContents();
        ItemStack[] armorContents = player.getInventory().getArmorContents();

        FilterResult inventoryResult = getInventoryManager().filterItems(player, inventoryItems);
        FilterResult extraResult = getInventoryManager().filterItems(player, extraContents);
        FilterResult armorResult = getInventoryManager().filterItems(player, armorContents);


        if (!inventoryResult.isKeepingItems() && !extraResult.isKeepingItems() && !armorResult.isKeepingItems()) {
            event.setKeepInventory(false);
        } else {
            event.setKeepInventory(true);

            event.getDrops().clear();
            event.getDrops().addAll(inventoryResult.getDrops());
            event.getDrops().addAll(extraResult.getDrops());
            event.getDrops().addAll(armorResult.getDrops());

            player.getInventory().setContents(inventoryResult.getKeptItems());
            player.getInventory().setExtraContents(extraResult.getKeptItems());
            player.getInventory().setArmorContents(armorResult.getKeptItems());
            
            player.sendMessage(ChatColor.YELLOW + "The force is with you and made you keep some items.");
        }
    }
}
