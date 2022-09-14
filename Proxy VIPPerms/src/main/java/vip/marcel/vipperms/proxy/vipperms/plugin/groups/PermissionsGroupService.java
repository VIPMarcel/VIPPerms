package vip.marcel.vipperms.proxy.vipperms.plugin.groups;

import vip.marcel.vipperms.proxy.vipperms.VIPPerms;
import vip.marcel.vipperms.proxy.vipperms.api.PermissionsGroup;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PermissionsGroupService implements PermissionsGroup {

    private final UUID uuid;
    private final String name;

    public PermissionsGroupService(UUID uuid) {
        this.uuid = uuid;
        this.name = null;
    }

    public PermissionsGroupService(String name) {
        this.name = name;
        this.uuid = VIPPerms.getInstance().getMySQL().getDatabaseGroups().getUUID(name);
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return (this.name != null ? this.name : VIPPerms.getInstance().getMySQL().getDatabaseGroups().getName(this.uuid));
    }

    @Override
    public String getPrefix() {
        return VIPPerms.getInstance().getMySQL().getDatabaseGroups().getPrefix(this.uuid);
    }

    @Override
    public String getSuffix() {
        return VIPPerms.getInstance().getMySQL().getDatabaseGroups().getSuffix(this.uuid);
    }

    @Override
    public String getColor() {
        return VIPPerms.getInstance().getMySQL().getDatabaseGroups().getColor(this.uuid);
    }

    @Override
    public List<UUID> getInterhances() {
        return VIPPerms.getInstance().getMySQL().getDatabaseGroups().getInterhances(this.uuid);
    }

    @Override
    public Map<String, Long> getPermissions() {
        return VIPPerms.getInstance().getMySQL().getDatabaseGroups().getPermissions(this.uuid);
    }

}
