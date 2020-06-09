package net.silthus.inventorykeeper.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Inject;
import lombok.Getter;
import net.silthus.inventorykeeper.InventoryKeeper;
import org.bukkit.ChatColor;

@Getter
@CommandAlias("sinventorykeeper|inventorykeeper|sik|ik")
public class InventoryKeeperCommands extends BaseCommand {

    private final InventoryKeeper plugin;

    @Inject
    public InventoryKeeperCommands(InventoryKeeper plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @Description("Reloads the plugin and all configs from disk.")
    @CommandPermission("sinventorykeeper.admin.reload")
    public void reload() {

       getPlugin().reload();
        getCurrentCommandIssuer().sendMessage(ChatColor.YELLOW + "Reloaded the InventoryKeeper plugin and all configs.");
    }
}
