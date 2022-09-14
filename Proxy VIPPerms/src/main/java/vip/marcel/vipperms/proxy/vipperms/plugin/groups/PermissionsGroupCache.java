package vip.marcel.vipperms.proxy.vipperms.plugin.groups;

import vip.marcel.vipperms.proxy.vipperms.api.PermissionsGroup;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PermissionsGroupCache implements PermissionsGroup {

    private UUID uuid;
    private final String name;

    private final Map<UUID, PermissionsGroup> cache;

    public PermissionsGroupCache(UUID uuid, Map<UUID, PermissionsGroup> chache) {
        this.cache = chache;

        this.uuid = uuid;

        final PermissionsGroup permissionsGroup = this.cache.get(uuid);
        this.name = permissionsGroup.getName();
    }

    public PermissionsGroupCache(String name, Map<UUID, PermissionsGroup> cache) {
        this.cache = cache;

        this.name = name;

        for(PermissionsGroup permissionsGroup : this.cache.values()) {
            if(permissionsGroup.getName().equalsIgnoreCase(name)) {
                this.uuid = permissionsGroup.getUUID();
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
    public String getPrefix() {
        return this.cache.get(uuid).getPrefix();
    }

    @Override
    public String getSuffix() {
        return this.cache.get(uuid).getSuffix();
    }

    @Override
    public String getColor() {
        return this.cache.get(uuid).getColor();
    }

    @Override
    public List<UUID> getInterhances() {
        return this.cache.get(uuid).getInterhances();
    }

    @Override
    public Map<String, Long> getPermissions() {
        return this.cache.get(uuid).getPermissions();
    }

}
