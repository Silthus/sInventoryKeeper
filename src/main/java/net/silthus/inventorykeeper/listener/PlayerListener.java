package net.silthus.inventorykeeper.listener;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import lombok.Getter;
import net.silthus.inventorykeeper.Constants;
import net.silthus.inventorykeeper.FilterException;
import net.silthus.inventorykeeper.InventoryManager;
import net.silthus.inventorykeeper.PluginConfig;
import net.silthus.inventorykeeper.api.FilterResult;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import javax.inject.Singleton;
import java.util.Arrays;

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
        PluginConfig config = getInventoryManager().getConfig();
        PluginConfig.Messages messages = config.getMessages();

        if (player.isOp() && config.isIgnoringOp()) return;
        if (player.hasPermission(Constants.BYPASS_ON_DEATH)) return;

        if (player.hasPermission(Constants.KEEP_ALL_ITEMS) ||(player.isOp() && config.isOpKeepingAll())) {
            event.setKeepInventory(true);
            event.getDrops().clear();
            if (!Strings.isNullOrEmpty(messages.getKeepAllmessage())) {
                player.sendMessage(messages.getKeepAllmessage());
            }
            return;
        }

        try {
            ItemStack[] inventoryItems = player.getInventory().getStorageContents();
            ItemStack[] extraContents = player.getInventory().getExtraContents();
            ItemStack[] armorContents = player.getInventory().getArmorContents();

            FilterResult inventoryResult = getInventoryManager().filterItems(player, inventoryItems);
            FilterResult extraResult = getInventoryManager().filterItems(player, extraContents);
            FilterResult armorResult = getInventoryManager().filterItems(player, armorContents);

            if (!inventoryResult.isKeepingItems() && !extraResult.isKeepingItems() && !armorResult.isKeepingItems()) {
                event.setKeepInventory(false);
                String dropAllMessage = messages.getDropAllMessage();
                if (!Strings.isNullOrEmpty(dropAllMessage)) {
                    player.sendMessage(dropAllMessage);
                }
            } else {
                event.setKeepInventory(true);

                event.getDrops().clear();
                event.getDrops().addAll(Arrays.asList(inventoryResult.getDrops()));
                event.getDrops().addAll(Arrays.asList(extraResult.getDrops()));
                event.getDrops().addAll(Arrays.asList(armorResult.getDrops()));

                player.getInventory().setContents(inventoryResult.getKeptItems());
                player.getInventory().setExtraContents(extraResult.getKeptItems());
                player.getInventory().setArmorContents(armorResult.getKeptItems());

                String keepItemsMessage = messages.getKeepItemsMessage();
                if (!Strings.isNullOrEmpty(keepItemsMessage)) {
                    player.sendMessage(keepItemsMessage);
                }
            }
        } catch (FilterException e) {
            event.getEntity().sendMessage(ChatColor.RED + "Dropping all items and not applying filters: " + e.getMessage());
        }
    }
}
