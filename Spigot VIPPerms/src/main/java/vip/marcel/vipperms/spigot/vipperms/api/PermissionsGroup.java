package vip.marcel.vipperms.spigot.vipperms.api;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface PermissionsGroup {

    UUID getUUID();

    String getName();

    String getPrefix();

    String getSuffix();

    String getColor();

    List<UUID> getInterhances();

    HashMap<String, Long> getPermissions();

}
