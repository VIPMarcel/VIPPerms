package vip.marcel.vipperms.proxy.vipperms.api;

import java.util.Map;
import java.util.UUID;

public interface PermissionsPlayer {

    UUID getUUID();

    String getName();

    UUID getGroupId();

    long getGroupExpires();

    Map<String, Long> getPermissions();

}
