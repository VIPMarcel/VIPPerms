package vip.marcel.vipperms.spigot.vipperms.plugin.players;

import vip.marcel.vipperms.spigot.vipperms.VIPPerms;
import vip.marcel.vipperms.spigot.vipperms.api.PermissionsPlayer;

import java.util.Map;
import java.util.UUID;

public class PermissionsPlayerService implements PermissionsPlayer {

    private final UUID uuid;
    private final String name;

    public PermissionsPlayerService(UUID uuid) {
        this.uuid = uuid;
        this.name = VIPPerms.getInstance().getMySQL().getDatabaseGroups().getName(uuid);
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getGroupId() {
        return VIPPerms.getInstance().getMySQL().getDatabasePlayers().getGroupUUID(uuid);
    }

    @Override
    public long getGroupExpires() {
        return VIPPerms.getInstance().getMySQL().getDatabasePlayers().getGroupExpires(uuid);
    }

    @Override
    public Map<String, Long> getPermissions() {
        return VIPPerms.getInstance().getMySQL().getDatabasePlayers().getPermissions(uuid);
    }

}
