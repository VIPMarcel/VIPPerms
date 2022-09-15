package vip.marcel.vipperms.spigot.vipperms.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;

import java.util.concurrent.CompletableFuture;

public class PlayerJoinListener implements Listener {

    public PlayerJoinListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, VIPPerms.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        player.setOp(false);
        VIPPerms.getInstance().resetPlayerPermissions(player);

        CompletableFuture.runAsync(() -> {
            if(!VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Settings.BungeeCord")) {
                VIPPerms.getInstance().getMySQL().getDatabasePlayers().setName(player.getUniqueId(), player.getName());
            }
        });

        VIPPerms.getInstance().setPlayerPermissions(player, true);
        player.recalculatePermissions();

        if(player.hasPermission("vipperms.autoop")) {
            player.setOp(true);
        }

    }

}
