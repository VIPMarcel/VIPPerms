package vip.marcel.vipperms.spigot.vipperms.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;
import vip.marcel.vipperms.spigot.vipperms.events.PlayerGroupChangeEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class PluginMessagingListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

        if(!(channel.equals("vipperms:reloadgroups") | channel.equals("vipperms:reloadplayer"))) {
            return;
        }

        if(channel.equals("vipperms:reloadgroups")) {
            try {
                Method method = Class.forName("vip.marcel.vipperms.spigot.vipperms.VIPPerms").getDeclaredMethod("loadGroupsCache");
                method.setAccessible(true);
                method.invoke(VIPPerms.getInstance());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(channel.equals("vipperms:reloadplayer")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            UUID uuid = UUID.fromString(in.readUTF());

            final Player target = Bukkit.getPlayer(uuid);

            if(target != null) {
                VIPPerms.getInstance().resetPlayerPermissions(target);
                target.setOp(false);
                VIPPerms.getInstance().setPlayerPermissions(target, true);

                Bukkit.getScheduler().runTaskLater(VIPPerms.getInstance(), () -> {
                    Bukkit.getPlayer(uuid).recalculatePermissions();

                    if(player.hasPermission("vipperms.autoop")) {
                        player.setOp(true);
                    }
                }, 20);

                Bukkit.getPluginManager().callEvent(new PlayerGroupChangeEvent(uuid, null, true));
            }

        }

    }

}
