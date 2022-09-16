package vip.marcel.vipperms.spigot.vipperms.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;
import vip.marcel.vipperms.spigot.vipperms.api.values.PlayerValue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerLoginListener implements Listener {

    public PlayerLoginListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, VIPPerms.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        final Player player = event.getPlayer();

        player.setOp(false);
        VIPPerms.getInstance().resetPlayerPermissions(player);

        if(!VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Settings.BungeeCord")) {
            if(!VIPPerms.getInstance().getMySQL().getDatabasePlayers().playerExists(player.getUniqueId())) {
                VIPPerms.getInstance().getMySQL().getDatabasePlayers().createPlayer(player.getUniqueId(), player.getName());
            } else {
                VIPPerms.getInstance().getMySQL().getDatabasePlayers().setName(player.getUniqueId(), player.getName());
            }
        }

        final long groupExpiresMillis = VIPPerms.getInstance().getMySQL().getDatabasePlayers().getGroupExpires(player.getUniqueId());
        if(groupExpiresMillis <= System.currentTimeMillis() && groupExpiresMillis != -1) {
            VIPPerms.getInstance().updatePermissionsPlayer(player.getUniqueId(), PlayerValue.GROUPID, UUID.fromString("00000183-31c6-bb2a-0000-000000000000"));
            VIPPerms.getInstance().updatePermissionsPlayer(player.getUniqueId(), PlayerValue.GROUP_EXPIRES, (long) -1);
            sendReloadPlayer(player);
        }

        VIPPerms.getInstance().setPlayerPermissions(player, true);

    }

    private void sendReloadPlayer(Player player) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(outStream);

        try {
            out.writeUTF(player.getUniqueId().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(VIPPerms.getInstance(), "vipperms:reloadplayer", outStream.toByteArray());
    }

}
