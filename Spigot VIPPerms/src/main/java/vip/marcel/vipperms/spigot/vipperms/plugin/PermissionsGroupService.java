package vip.marcel.vipperms.spigot.vipperms.plugin;

import vip.marcel.vipperms.spigot.vipperms.api.PermissionsGroup;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PermissionsGroupService implements PermissionsGroup {

    private UUID uuid;
    private String name;

    public PermissionsGroupService(UUID uuid) {
        this.uuid = uuid;
        this.name = null;
    }

    public PermissionsGroupService(String name) {
        this.name = name;
        this.uuid = null;
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
        return null;
    }

    @Override
    public String getSuffix() {
        return null;
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public List<UUID> getInterhances() {
        return null;
    }

    @Override
    public HashMap<String, Long> getPermissions() {
        return null;
    }

}
