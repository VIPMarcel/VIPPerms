package vip.marcel.vipperms.proxy.vipperms.events;

import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class PlayerGroupChangeEvent extends Event {

    private final UUID playerUniqueId;
    private final UUID groupUniqueId;
    private final boolean smoothUpdate;

    public PlayerGroupChangeEvent(final UUID playerUniqueId, UUID groupUniqueId, boolean smoothUpdate) {
        this.playerUniqueId = playerUniqueId;
        this.groupUniqueId = groupUniqueId;
        this.smoothUpdate = smoothUpdate;
    }

    public UUID getPlayerUniqueId() {
        return this.playerUniqueId;
    }

    public UUID getGroupUniqueId() {
        return this.groupUniqueId;
    }

    public boolean isSmoothUpdate() {
        return this.smoothUpdate;
    }

}
