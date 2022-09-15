package vip.marcel.vipperms.spigot.vipperms.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;
import vip.marcel.vipperms.spigot.vipperms.api.values.PlayerValue;

import java.util.UUID;

public class PlayerLoginListener implements Listener {

    public PlayerLoginListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, VIPPerms.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLoginEvent(AsyncPlayerPreLoginEvent event) {
        final UUID uuid = event.getUniqueId();

        if(!VIPPerms.getInstance().getMySQL().getDatabasePlayers().playerExists(uuid)) {
            if(!VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Settings.BungeeCord")) {
                VIPPerms.getInstance().getMySQL().getDatabasePlayers().createPlayer(uuid, "-/-");
            }
        }

        final long groupExpiresMillis = VIPPerms.getInstance().getMySQL().getDatabasePlayers().getGroupExpires(uuid);
        if(groupExpiresMillis <= System.currentTimeMillis() && groupExpiresMillis != -1) {
            VIPPerms.getInstance().updatePermissionsPlayer(uuid, PlayerValue.GROUPID, UUID.fromString("00000183-31c6-bb2a-0000-000000000000"));
            VIPPerms.getInstance().updatePermissionsPlayer(uuid, PlayerValue.GROUP_EXPIRES, (long) -1);
        }

    }

}
