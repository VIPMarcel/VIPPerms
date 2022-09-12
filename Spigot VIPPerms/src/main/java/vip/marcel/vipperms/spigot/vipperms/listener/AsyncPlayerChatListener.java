package vip.marcel.vipperms.spigot.vipperms.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;
import vip.marcel.vipperms.spigot.vipperms.api.PermissionsGroup;
import vip.marcel.vipperms.spigot.vipperms.api.PermissionsPlayer;

public class AsyncPlayerChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();

        if(VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Chat.Enable")) {
            String format = ChatColor.translateAlternateColorCodes('&', VIPPerms.getInstance().getSettingsConfiguration().getString("Chat.Format"));

            final PermissionsPlayer permissionsPlayer = VIPPerms.getInstance().getPermissionsPlayer(player.getUniqueId());
            final PermissionsGroup permissionsGroup = VIPPerms.getInstance().getPermissionsGroup(permissionsPlayer.getGroupId());

            format.replace("{prefix}", permissionsGroup.getPrefix());
            format.replace("{color}", permissionsGroup.getColor());
            format.replace("{suffix}", permissionsGroup.getSuffix());
            format.replace("{player}", player.getName());
            format.replace("{message}", event.getMessage());

            event.setFormat(format);

        }

    }

}
