package us.talabrek.ultimateskyblock.command.island;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.talabrek.ultimateskyblock.Settings;
import us.talabrek.ultimateskyblock.SkyBlockMenu;
import us.talabrek.ultimateskyblock.island.IslandInfo;
import us.talabrek.ultimateskyblock.player.PlayerInfo;
import us.talabrek.ultimateskyblock.uSkyBlock;

import java.util.Map;

import static us.talabrek.ultimateskyblock.util.I18nUtil.tr;

public class BiomeCommand extends RequireIslandCommand {
    private final SkyBlockMenu menu;

    public BiomeCommand(uSkyBlock plugin, SkyBlockMenu menu) {
        super(plugin, "biome|b", null, "biome", "change the biome of the island");
        this.menu = menu;
    }

    @Override
    protected boolean doExecute(String alias, Player player, PlayerInfo pi, IslandInfo island, Map<String, Object> data, String... args) {
        if (args.length == 0) {
            if (!island.hasPerm(player, "canChangeBiome")) {
                player.sendMessage(tr("\u00a7cYou do not have permission to change the biome of your current island."));
            } else {
                player.openInventory(menu.displayBiomeGUI(player)); // Weird, that we show the UI
            }
        }
        if (args.length == 1) {
            String biome = args[0];
            if (island.hasPerm(player, "canChangeBiome")) {
                if (plugin.onBiomeCooldown(player) && Settings.general_biomeChange != 0) {
                    player.sendMessage(tr("\u00a7eYou can change your biome again in {0,number,#} minutes.", plugin.getBiomeCooldownTime(player) / 1000L / 60L));
                    return true;
                }
                if (plugin.playerIsOnIsland(player)) {
                    if (plugin.changePlayerBiome(player, biome)) {
                        player.sendMessage(tr("\u00a7aYou have changed your island's biome to {0}", biome.toUpperCase()));
                        player.sendMessage(tr("\u00a7aYou may need to go to spawn, or relog, to see the changes."));
                        island.sendMessageToIslandGroup(tr("{0} changed the island biome to {1}", player.getName(), biome.toUpperCase()));
                        plugin.setBiomeCooldown(player);
                    } else {
                        player.sendMessage(tr("\u00a7aUnknown biome name, changing your biome to OCEAN"));
                        player.sendMessage(tr("\u00a7aYou may need to go to spawn, or relog, to see the changes."));
                        island.sendMessageToIslandGroup(tr("{0} changed the island biome to OCEAN", player.getName()));
                    }
                } else {
                    player.sendMessage(tr("\u00a7eYou must be on your island to change the biome!"));
                }
            } else {
                player.sendMessage(tr("\u00a74You do not have permission to change the biome of this island!"));
            }
        }
        return true;
    }
}
