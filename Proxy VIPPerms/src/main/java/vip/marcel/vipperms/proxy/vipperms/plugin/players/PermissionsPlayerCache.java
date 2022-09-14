package vip.marcel.vipperms.proxy.vipperms.plugin.players;

import vip.marcel.vipperms.proxy.vipperms.api.PermissionsPlayer;

import java.util.Map;
import java.util.UUID;

public class PermissionsPlayerCache implements PermissionsPlayer {

    private UUID uuid;
    private final String name;

    private final Map<UUID, PermissionsPlayer> cache;

    public PermissionsPlayerCache(UUID uuid, Map<UUID, PermissionsPlayer> chache) {
        this.cache = chache;

        this.uuid = uuid;

        final PermissionsPlayer permissionsPlayer = this.cache.get(uuid);
        this.name = permissionsPlayer.getName();
    }

    public PermissionsPlayerCache(String name, Map<UUID, PermissionsPlayer> cache) {
        this.cache = cache;

        this.name = name;

        for(PermissionsPlayer permissionsPlayer : this.cache.values()) {
            if(permissionsPlayer.getName().equalsIgnoreCase(name)) {
                this.uuid = permissionsPlayer.getUUID();
            }
        }

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
        return this.cache.get(uuid).getGroupId();
    }

    @Override
    public long getGroupExpires() {
        return this.cache.get(uuid).getGroupExpires();
    }

    @Override
    public Map<String, Long> getPermissions() {
        return this.cache.get(uuid).getPermissions();
    }

}
