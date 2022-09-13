package vip.marcel.vipperms.spigot.vipperms.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;

import java.util.concurrent.CompletableFuture;

public class PlayerLoginListener implements Listener {

    public PlayerLoginListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, VIPPerms.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        final Player player = event.getPlayer();

        player.setOp(false);
        VIPPerms.getInstance().resetPlayerPermissions(player);

        CompletableFuture.supplyAsync(() -> {

            if(!VIPPerms.getInstance().getMySQL().getDatabasePlayers().playerExists(player.getUniqueId())) {
                VIPPerms.getInstance().getMySQL().getDatabasePlayers().createPlayer(player.getUniqueId(), player.getName());
            } else {
                VIPPerms.getInstance().getMySQL().getDatabasePlayers().setName(player.getUniqueId(), player.getName());
            }

            VIPPerms.getInstance().setPlayerPermissions(player, true);
            return true;
        }).thenAccept(finished -> {
            if(finished) {
                player.recalculatePermissions();
            }
        });

    }

}
