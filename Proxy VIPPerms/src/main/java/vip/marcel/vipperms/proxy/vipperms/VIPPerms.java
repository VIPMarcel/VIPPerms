package vip.marcel.vipperms.proxy.vipperms;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import vip.marcel.vipperms.proxy.vipperms.api.PermissionsGroup;
import vip.marcel.vipperms.proxy.vipperms.api.PermissionsPlayer;
import vip.marcel.vipperms.proxy.vipperms.api.values.GroupValue;
import vip.marcel.vipperms.proxy.vipperms.api.values.PlayerValue;
import vip.marcel.vipperms.proxy.vipperms.commands.VIPPermsCommand;
import vip.marcel.vipperms.proxy.vipperms.listeners.PluginMessageListener;
import vip.marcel.vipperms.proxy.vipperms.listeners.ServerConnectListener;
import vip.marcel.vipperms.proxy.vipperms.plugin.groups.PermissionsGroupCache;
import vip.marcel.vipperms.proxy.vipperms.plugin.groups.PermissionsGroupService;
import vip.marcel.vipperms.proxy.vipperms.plugin.players.PermissionsPlayerCache;
import vip.marcel.vipperms.proxy.vipperms.plugin.players.PermissionsPlayerService;
import vip.marcel.vipperms.proxy.vipperms.utils.config.SettingsConfiguration;
import vip.marcel.vipperms.proxy.vipperms.utils.database.MySQL;
import vip.marcel.vipperms.proxy.vipperms.utils.helper.GroupExpiresTimeHelper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class VIPPerms extends Plugin {

    private static VIPPerms instance;

    private String prefix, noPermissions, unknownCommand;

    private Map<UUID, PermissionsGroup> permissionsGroups;
    private Map<UUID, PermissionsPlayer> permissionsPlayers;

    private SettingsConfiguration settingsConfiguration;

    private GroupExpiresTimeHelper groupExpiresTimeHelper;

    private MySQL mySQL;

    @Override
    public void onEnable() {
        instance = this;

        this.init();
        this.registerListeners();
        this.loadGroupsCache();

        ProxyServer.getInstance().registerChannel("vipperms:reloadgroups");
        ProxyServer.getInstance().registerChannel("vipperms:reloadplayer");
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

        this.groupExpiresTimeHelper = new GroupExpiresTimeHelper();

        this.mySQL = new MySQL();
        this.mySQL.connect();
    }

    private void registerListeners() {
        new ServerConnectListener();
        new PluginMessageListener();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new VIPPermsCommand("vipperms", "vipperms.*", "vp"));
    }

    private void loadGroupsCache() {
        this.permissionsGroups.clear();

        for(UUID uuid : this.mySQL.getAllPermissionsGroups()) {
            final PermissionsGroup permissionsGroup = new PermissionsGroupService(uuid);
            this.permissionsGroups.put(uuid, permissionsGroup);
            getLogger().log(Level.INFO, "Group '" + permissionsGroup.getName() + "' loaded into cache.");
        }
    }

    public List<PermissionsGroup> getPermissionsGroups() {
        final List<PermissionsGroup> output = Lists.newArrayList();

        output.addAll(this.permissionsGroups.values());

        return output;
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

    public void updatePermissionsGroup(UUID uuid, GroupValue values, Object value) {
        final PermissionsGroup permissionsGroup = getPermissionsGroup(uuid);

        if(permissionsGroup == null) {
            getLogger().log(Level.WARNING, "Group with uniqueId '" + uuid.toString() + "' not found and updated.");
            return;
        }

        switch(values) {
            case NAME -> this.mySQL.getDatabaseGroups().setName(uuid, value.toString());
            case PREFIX -> this.mySQL.getDatabaseGroups().setPrefix(uuid, value.toString());
            case SUFFIX -> this.mySQL.getDatabaseGroups().setSuffix(uuid, value.toString());
            case COLOR -> this.mySQL.getDatabaseGroups().setColor(uuid, value.toString());
            case INTERHANCES -> this.mySQL.getDatabaseGroups().setInterhances(uuid, (List<UUID>) value);
            case PERMISSIONS -> this.mySQL.getDatabaseGroups().setPermissions(uuid, (Map<String, Long>) value);
            default -> getLogger().log(Level.WARNING, "Group with uniqueId '" + uuid.toString() + "' tried to update unknown GroupValue.");
        }

        loadGroupsCache();
    }

    public void updatePermissionsPlayer(UUID uuid, PlayerValue values, Object value) {
        final PermissionsPlayer permissionsPlayer = getPermissionsPlayer(uuid);

        if(permissionsPlayer == null) {
            getLogger().log(Level.WARNING, "Player with uniqueId '" + uuid.toString() + "' not found and updated.");
            return;
        }

        switch(values) {
            case NAME -> this.mySQL.getDatabasePlayers().setName(uuid, value.toString());
            case GROUPID -> this.mySQL.getDatabasePlayers().setGroup(uuid, (UUID) value);
            case GROUP_EXPIRES -> this.mySQL.getDatabasePlayers().setGroupExpires(uuid, (long) value);
            case PERMISSIONS -> this.mySQL.getDatabasePlayers().setPermissions(uuid, (Map<String, Long>) value);
            default -> getLogger().log(Level.WARNING, "Player with uniqueId '" + uuid.toString() + "' tried to update unknown PlayerValue.");
        }

        getPermissionsPlayer(uuid, unused -> {}, true);
    }

    public void resetPlayerPermissions(ProxiedPlayer player) {

        for(Iterator<String> iterator = new ArrayList<>(player.getPermissions()).iterator(); iterator.hasNext(); ) {
            player.setPermission(iterator.next(), false);
        }

    }

    public boolean setPlayerPermissions(ProxiedPlayer player, boolean database) {

        if(database) {
            getPermissionsPlayer(player.getUniqueId(), permissionsPlayer -> {
                final Map<String, Long> playerPermissions = permissionsPlayer.getPermissions();

                for(String permission : playerPermissions.keySet()) {
                    if(playerPermissions.get(permission) >= System.currentTimeMillis() | playerPermissions.get(permission) == -1) {
                        player.setPermission(permission, true);
                    } else {
                        playerPermissions.remove(permission);
                    }
                }

                final PermissionsGroup playerGroup = getPermissionsGroup(permissionsPlayer.getGroupId());

                for(String permission : playerGroup.getPermissions().keySet()) {
                    if(playerGroup.getPermissions().get(permission) >= System.currentTimeMillis() | playerGroup.getPermissions().get(permission) == -1) {
                        player.setPermission(permission, true);
                    } else {
                        playerGroup.getPermissions().remove(permission);
                    }
                }

                for(UUID interhance : playerGroup.getInterhances()) {
                    final PermissionsGroup interhaceGroup = getPermissionsGroup(interhance);

                    for(String permission : interhaceGroup.getPermissions().keySet()) {
                        if(interhaceGroup.getPermissions().get(permission) >= System.currentTimeMillis() | interhaceGroup.getPermissions().get(permission) == -1) {
                            player.setPermission(permission, true);
                        } else {
                            interhaceGroup.getPermissions().remove(permission);
                        }
                    }

                }
            }, true);
        } else {
            final PermissionsPlayer permissionsPlayer = getPermissionsPlayer(player.getUniqueId());

            final Map<String, Long> playerPermissions = permissionsPlayer.getPermissions();

            for(String permission : playerPermissions.keySet()) {
                if(playerPermissions.get(permission) >= System.currentTimeMillis() | playerPermissions.get(permission) == -1) {
                    player.setPermission(permission, true);
                }
            }

            final PermissionsGroup playerGroup = getPermissionsGroup(permissionsPlayer.getGroupId());

            for(String permission : playerGroup.getPermissions().keySet()) {
                if(playerGroup.getPermissions().get(permission) >= System.currentTimeMillis() | playerGroup.getPermissions().get(permission) == -1) {
                    player.setPermission(permission, true);
                }
            }

            for(UUID interhance : playerGroup.getInterhances()) {
                final PermissionsGroup interhaceGroup = getPermissionsGroup(interhance);

                for(String permission : interhaceGroup.getPermissions().keySet()) {
                    if(interhaceGroup.getPermissions().get(permission) >= System.currentTimeMillis() | interhaceGroup.getPermissions().get(permission) == -1) {
                        player.setPermission(permission, true);
                    }
                }

            }

        }

        return true;
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

    public GroupExpiresTimeHelper getGroupExpiresTimeHelper() {
        return this.groupExpiresTimeHelper;
    }

    public MySQL getMySQL() {
        return this.mySQL;
    }

}
