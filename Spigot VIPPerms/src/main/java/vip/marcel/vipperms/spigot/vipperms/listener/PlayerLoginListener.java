package vip.marcel.vipperms.spigot.vipperms.listener;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;
import vip.marcel.vipperms.spigot.vipperms.api.PermissionsGroup;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PlayerLoginListener implements Listener {

    public PlayerLoginListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, VIPPerms.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLoginEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PermissionAttachment permissionAttachment = player.addAttachment(VIPPerms.getInstance());

        player.setOp(false);
        VIPPerms.getInstance().resetPlayerPermissions(player);

        CompletableFuture.runAsync(() -> {

            if(!VIPPerms.getInstance().getMySQL().getDatabasePlayers().playerExists(player.getUniqueId())) {
                VIPPerms.getInstance().getMySQL().getDatabasePlayers().createPlayer(player.getUniqueId(), player.getName());
            } else {
                VIPPerms.getInstance().getMySQL().getDatabasePlayers().setName(player.getUniqueId(), player.getName());
            }

            VIPPerms.getInstance().getPermissionsPlayer(player.getUniqueId(), permissionsPlayer -> {
                final List<PermissionsGroup> permissionInterhances = Lists.newArrayList();

                final PermissionsGroup playerGroup = VIPPerms.getInstance().getPermissionsGroup(permissionsPlayer.getGroupId());

                permissionInterhances.add(playerGroup);

                playerGroup.getInterhances().forEach(groupIds -> {
                    permissionInterhances.add(VIPPerms.getInstance().getPermissionsGroup(groupIds));
                });

                permissionInterhances.forEach(interhances -> {
                    calculatePermissions(permissionAttachment, interhances.getPermissions());
                });

                calculatePermissions(permissionAttachment, permissionsPlayer.getPermissions());

                if(VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Player.Update-Displayname")) {
                    player.setDisplayName(playerGroup.getColor() + player.getName());
                }

            }, true);

        }).thenAccept(unused -> {
            player.recalculatePermissions();
        });

    }

    private void calculatePermissions(PermissionAttachment permissionAttachment, Map<String, Long> permissions) {
        Bukkit.broadcastMessage("heyyy");
        for(String permission : permissions.keySet()) {
            if(permissions.get(permission) >= System.currentTimeMillis() | permissions.get(permission) == -1) {
                Bukkit.getScheduler().runTask(VIPPerms.getInstance(), () -> {
                    permissionAttachment.setPermission(permission, true);
                    Bukkit.broadcastMessage("added: " + permission + " for " + permissions.get(permission));
                });
            }
        }
    }

}
