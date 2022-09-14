package vip.marcel.vipperms.proxy.vipperms.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import vip.marcel.vipperms.proxy.vipperms.VIPPerms;
import vip.marcel.vipperms.proxy.vipperms.events.PlayerGroupChangeEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class PluginMessageListener implements Listener {

    public PluginMessageListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(VIPPerms.getInstance(), this);
    }

    @EventHandler
    public void onPluginMessageEvent(PluginMessageEvent event) {
        final ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());

        if(event.getTag().equalsIgnoreCase("vipperms:reloadplayer")) {
            final UUID uuid = UUID.fromString(input.readUTF());

            if(ProxyServer.getInstance().getPlayer(uuid) != null) {
                VIPPerms.getInstance().resetPlayerPermissions(ProxyServer.getInstance().getPlayer(uuid));
                VIPPerms.getInstance().setPlayerPermissions(ProxyServer.getInstance().getPlayer(uuid), true);

                ProxyServer.getInstance().getPluginManager().callEvent(new PlayerGroupChangeEvent(uuid, null, true));
            }

        }

        if(event.getTag().equalsIgnoreCase("vipperms:reloadgroups")) {

            try {
                Method method = Class.forName("vip.marcel.vipperms.proxy.vipperms.VIPPerms").getDeclaredMethod("loadGroupsCache");
                method.setAccessible(true);
                method.invoke(VIPPerms.getInstance());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            for(String server : ProxyServer.getInstance().getServers().keySet()) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                ProxyServer.getInstance().getServerInfo(server).sendData("vipperms:reloadgroups", out.toByteArray());
            }

        }

    }

}
