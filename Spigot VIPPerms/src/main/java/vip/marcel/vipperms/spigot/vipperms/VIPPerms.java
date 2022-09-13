package vip.marcel.vipperms.spigot.vipperms;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import vip.marcel.vipperms.spigot.vipperms.api.PermissionsGroup;
import vip.marcel.vipperms.spigot.vipperms.api.PermissionsPlayer;
import vip.marcel.vipperms.spigot.vipperms.commands.VIPPermsCommand;
import vip.marcel.vipperms.spigot.vipperms.listener.AsyncPlayerChatListener;
import vip.marcel.vipperms.spigot.vipperms.listener.PlayerLoginListener;
import vip.marcel.vipperms.spigot.vipperms.plugin.groups.PermissionsGroupCache;
import vip.marcel.vipperms.spigot.vipperms.plugin.groups.PermissionsGroupService;
import vip.marcel.vipperms.spigot.vipperms.plugin.players.PermissionsPlayerCache;
import vip.marcel.vipperms.spigot.vipperms.plugin.players.PermissionsPlayerService;
import vip.marcel.vipperms.spigot.vipperms.utils.config.SettingsConfiguration;
import vip.marcel.vipperms.spigot.vipperms.utils.database.MySQL;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

public class VIPPerms extends JavaPlugin {

    private static VIPPerms instance;

    private String prefix, noPermissions, unknownCommand;

    private Map<UUID, PermissionsGroup> permissionsGroups;
    private Map<UUID, PermissionsPlayer> permissionsPlayers;

    private SettingsConfiguration settingsConfiguration;

    private MySQL mySQL;

    @Override
    public void onEnable() {
        instance = this;

        this.init();
        this.loadGroupsCache();
        this.registerListeners();
    }

    @Override
    public void onDisable() {
        this.mySQL.disconnect();
    }

    public static VIPPerms getInstance() {
        return instance;
    }

    private void init() {
        this.prefix = "§8§l┃ §bVPerms §8► §7";
        this.noPermissions = "Du hast keinen Zugriff auf diesen Befehl";
        this.unknownCommand = "Dieser Befehl existiert nicht.";

        this.permissionsGroups = Maps.newHashMap();
        this.permissionsPlayers = Maps.newHashMap();

        this.settingsConfiguration = new SettingsConfiguration();

        this.mySQL = new MySQL();
        this.mySQL.connect();

        Map<String, Long> testMap = Maps.newHashMap();
        testMap.put("vipperms.*", (long) -1);
        this.mySQL.getDatabaseGroups().setPermissions(UUID.fromString("00000183-31c6-bb2a-0000-000000000000"), testMap);
    }

    private void loadGroupsCache() {
        for(UUID uuid : this.mySQL.getAllPermissionsGroups()) {
            final PermissionsGroup permissionsGroup = new PermissionsGroupService(uuid);
            this.permissionsGroups.put(uuid, permissionsGroup);
            getLogger().log(Level.INFO, "Group '" + permissionsGroup.getName() + "' loaded into cache.");
        }
    }

    private void registerListeners() {
        new PlayerLoginListener();
        new AsyncPlayerChatListener();

        new VIPPermsCommand();
    }

    public PermissionsGroup getPermissionsGroup(UUID uuid) {
        return new PermissionsGroupCache(uuid, this.permissionsGroups);
    }

    public void getPermissionsGroup(UUID uuid, Consumer<PermissionsGroup> callback) {
        CompletableFuture.runAsync(() -> {
            callback.accept(new PermissionsGroupService(uuid));
        });
    }

    public PermissionsGroup getPermissionsGroup(String name) {
        return new PermissionsGroupCache(name, this.permissionsGroups);
    }

    public void getPermissionsGroup(String name, Consumer<PermissionsGroup> callback) {
        CompletableFuture.runAsync(() -> {
            callback.accept(new PermissionsGroupService(name));
        });
    }

    public PermissionsPlayer getPermissionsPlayer(UUID uuid) {
        return new PermissionsPlayerCache(uuid, this.permissionsPlayers);
    }

    public void getPermissionsPlayer(UUID uuid, Consumer<PermissionsPlayer> callback, boolean reloadCache) {
        CompletableFuture.runAsync(() -> {
            final PermissionsPlayerService permissionsPlayerService = new PermissionsPlayerService(uuid);

            if(!this.permissionsPlayers.containsKey(permissionsPlayerService.getUUID()) | reloadCache) {
                this.permissionsPlayers.put(permissionsPlayerService.getUUID(), permissionsPlayerService);
            }

            callback.accept(permissionsPlayerService);
        });
    }

    public PermissionsPlayer getPermissionsPlayer(String name) {
        return new PermissionsPlayerCache(name, this.permissionsPlayers);
    }

    public void resetPlayerPermissions(Player player) {

        for(Iterator<PermissionAttachmentInfo> iterator = player.getEffectivePermissions().iterator(); iterator.hasNext(); ) {
            PermissionAttachmentInfo info = iterator.next();

            if(info.getAttachment() != null) {
                PermissionAttachment permission = info.getAttachment();
                permission.unsetPermission(info.getPermission());
                permission.setPermission(info.getPermission(), false);
            }

        }

        player.recalculatePermissions();
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getNoPermissions() {
        return this.noPermissions;
    }

    public String getUnknownCommand() {
        return this.unknownCommand;
    }

    public SettingsConfiguration getSettingsConfiguration() {
        return this.settingsConfiguration;
    }

    public MySQL getMySQL() {
        return this.mySQL;
    }

}
