package vip.marcel.vipperms.spigot.vipperms.listener;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
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
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final PermissionAttachment permissionAttachment = player.addAttachment(VIPPerms.getInstance());

        player.setOp(false);

        CompletableFuture.runAsync(() -> {
            if(!VIPPerms.getInstance().getMySQL().getDatabasePlayers().playerExists(player.getUniqueId())) {
                VIPPerms.getInstance().getMySQL().getDatabasePlayers().createPlayer(player.getUniqueId(), player.getName());
            } else {
                VIPPerms.getInstance().getMySQL().getDatabasePlayers().setName(player.getUniqueId(), player.getName());
            }
        });

        VIPPerms.getInstance().getPermissionsPlayer(player.getUniqueId(), permissionsPlayer -> {
            final List<PermissionsGroup> permissionInterhances = Lists.newArrayList();

            final PermissionsGroup playerGroup = VIPPerms.getInstance().getPermissionsGroup(permissionsPlayer.getGroupId());

            permissionInterhances.add(playerGroup);

            playerGroup.getInterhances().forEach(groupIds -> {
                permissionInterhances.add(VIPPerms.getInstance().getPermissionsGroup(groupIds));
            });

            permissionInterhances.forEach(interhances -> {
                calcuatePermissions(permissionAttachment, interhances.getPermissions());

            });

            calcuatePermissions(permissionAttachment, permissionsPlayer.getPermissions());

            if(VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Player.Update-Displayname")) {
                player.setDisplayName(playerGroup.getColor() + player.getName());
            }

        }, true);

    }

    private void calcuatePermissions(PermissionAttachment permissionAttachment, Map<String, Long> permissions) {
        for(String permission : permissions.keySet()) {
            if(permissions.get(permission) >= System.currentTimeMillis()) {
                Bukkit.getScheduler().runTask(VIPPerms.getInstance(), () -> {
                    permissionAttachment.setPermission(permission, true);
                });
            }
        }
    }

}
