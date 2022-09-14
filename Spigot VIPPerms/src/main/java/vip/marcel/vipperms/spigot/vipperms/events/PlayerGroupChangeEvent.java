package vip.marcel.vipperms.spigot.vipperms.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerGroupChangeEvent extends Event {

    private static final HandlerList handlers;
    private final UUID playerUniqueId;
    private final UUID groupUniqueId;
    private final boolean smoothUpdate;

    public PlayerGroupChangeEvent(final UUID playerUniqueId, UUID groupUniqueId, boolean smoothUpdate) {
        this.playerUniqueId = playerUniqueId;
        this.groupUniqueId = groupUniqueId;
        this.smoothUpdate = smoothUpdate;
    }

    public HandlerList getHandlers() {
        return PlayerGroupChangeEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return PlayerGroupChangeEvent.handlers;
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

    static {
        handlers = new HandlerList();
    }

}
