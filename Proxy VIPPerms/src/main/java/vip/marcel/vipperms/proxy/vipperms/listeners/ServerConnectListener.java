package vip.marcel.vipperms.proxy.vipperms.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import vip.marcel.vipperms.proxy.vipperms.VIPPerms;
import vip.marcel.vipperms.proxy.vipperms.api.values.PlayerValue;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ServerConnectListener implements Listener {

    public ServerConnectListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(VIPPerms.getInstance(), this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnectEvent(ServerConnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        if(player.getServer() == null) {

            VIPPerms.getInstance().resetPlayerPermissions(player);

            CompletableFuture.supplyAsync(() -> {

                if(!VIPPerms.getInstance().getMySQL().getDatabasePlayers().playerExists(player.getUniqueId())) {
                    VIPPerms.getInstance().getMySQL().getDatabasePlayers().createPlayer(player.getUniqueId(), player.getName());
                } else {
                    VIPPerms.getInstance().getMySQL().getDatabasePlayers().setName(player.getUniqueId(), player.getName());
                }

                final long groupExpiresMillis = VIPPerms.getInstance().getMySQL().getDatabasePlayers().getGroupExpires(player.getUniqueId());
                if(groupExpiresMillis <= System.currentTimeMillis() && groupExpiresMillis != -1) {
                    VIPPerms.getInstance().updatePermissionsPlayer(player.getUniqueId(), PlayerValue.GROUPID, UUID.fromString("00000183-31c6-bb2a-0000-000000000000"));
                    VIPPerms.getInstance().updatePermissionsPlayer(player.getUniqueId(), PlayerValue.GROUP_EXPIRES, (long) -1);
                }

                VIPPerms.getInstance().setPlayerPermissions(player, true);
                return true;
            });

        }

    }

}
