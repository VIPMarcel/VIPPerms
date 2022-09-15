package vip.marcel.vipperms.spigot.vipperms.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PermissionsGroup {

    UUID getUUID();

    Integer getTabSortId();

    String getName();

    String getPrefix();

    String getSuffix();

    String getColor();

    List<UUID> getInterhances();

    Map<String, Long> getPermissions();

}
