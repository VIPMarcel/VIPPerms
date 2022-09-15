package vip.marcel.vipperms.spigot.vipperms;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import vip.marcel.vipperms.spigot.vipperms.api.PermissionsGroup;
import vip.marcel.vipperms.spigot.vipperms.api.PermissionsPlayer;
import vip.marcel.vipperms.spigot.vipperms.api.values.GroupValue;
import vip.marcel.vipperms.spigot.vipperms.api.values.PlayerValue;
import vip.marcel.vipperms.spigot.vipperms.commands.VIPPermsCommand;
import vip.marcel.vipperms.spigot.vipperms.listener.AsyncPlayerChatListener;
import vip.marcel.vipperms.spigot.vipperms.listener.PlayerJoinListener;
import vip.marcel.vipperms.spigot.vipperms.listener.PlayerLoginListener;
import vip.marcel.vipperms.spigot.vipperms.listener.PluginMessagingListener;
import vip.marcel.vipperms.spigot.vipperms.plugin.groups.PermissionsGroupCache;
import vip.marcel.vipperms.spigot.vipperms.plugin.groups.PermissionsGroupService;
import vip.marcel.vipperms.spigot.vipperms.plugin.players.PermissionsPlayerCache;
import vip.marcel.vipperms.spigot.vipperms.plugin.players.PermissionsPlayerService;
import vip.marcel.vipperms.spigot.vipperms.utils.config.SettingsConfiguration;
import vip.marcel.vipperms.spigot.vipperms.utils.database.MySQL;
import vip.marcel.vipperms.spigot.vipperms.utils.entities.VIPScoreboardTeam;
import vip.marcel.vipperms.spigot.vipperms.utils.helper.GroupExpiresTimeHelper;
import vip.marcel.vipperms.spigot.vipperms.utils.helper.ScoreboardHelper;

import java.util.Iterator;
import java.util.List;
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

    private GroupExpiresTimeHelper groupExpiresTimeHelper;

    private MySQL mySQL;

    @Override
    public void onEnable() {
        instance = this;

        this.init();
        this.loadGroupsCache();
        this.registerListeners();

        final PluginMessagingListener pluginMessagingListener = new PluginMessagingListener();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "vipperms:reloadgroups");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "vipperms:reloadplayer");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "vipperms:reloadgroups", pluginMessagingListener);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "vipperms:reloadplayer", pluginMessagingListener);
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

    private void loadGroupsCache() {
        this.permissionsGroups.clear();

        for(UUID uuid : this.mySQL.getAllPermissionsGroups()) {
            final PermissionsGroup permissionsGroup = new PermissionsGroupService(uuid);
            this.permissionsGroups.put(uuid, permissionsGroup);
            getLogger().log(Level.INFO, "Group '" + permissionsGroup.getName() + "' loaded into cache.");
        }
    }

    private void registerListeners() {
        new PlayerLoginListener();
        new PlayerJoinListener();
        new AsyncPlayerChatListener();

        new VIPPermsCommand();
    }

    public void setScoreboard(Player player) {
        final List<VIPScoreboardTeam> scoreboardTeams = Lists.newArrayList();

        if(!this.settingsConfiguration.getBoolean("Tab.Enable")) {
            return;
        }

        getPermissionsGroups().forEach(group -> {
            VIPScoreboardTeam team = new VIPScoreboardTeam();
            team.setSortId(group.getTabSortId());
            team.setName(group.getName());
            team.setPrefix(group.getPrefix());
            team.setSuffix(group.getSuffix());
            team.setColor(group.getColor().length() > 1 ? group.getColor().substring(0, 2) : group.getColor());

            scoreboardTeams.add(team);
        });

        new ScoreboardHelper(player)
                .setDisplaySlot(DisplaySlot.SIDEBAR)
                .addTeams(scoreboardTeams)
                .build();

        final ScoreboardHelper scoreboardHelper = new ScoreboardHelper();

        final int playerTeamIndex = getPermissionsGroup(getPermissionsPlayer(player.getUniqueId()).getGroupId()).getTabSortId();
        final String playerTeamName = getPermissionsGroup(getPermissionsPlayer(player.getUniqueId()).getGroupId()).getName();

        scoreboardHelper.updatePlayerTeam(player, playerTeamIndex, playerTeamName, false);

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

    /* Synchronized method!!! (no delay at login-event)  */
    public void getPermissionsPlayer(UUID uuid, Consumer<PermissionsPlayer> callback, boolean reloadCache) {
        //CompletableFuture.runAsync(() -> {
            final PermissionsPlayerService permissionsPlayerService = new PermissionsPlayerService(uuid);

            if(!this.permissionsPlayers.containsKey(permissionsPlayerService.getUUID()) | reloadCache) {
                this.permissionsPlayers.put(permissionsPlayerService.getUUID(), permissionsPlayerService);
            }

            callback.accept(permissionsPlayerService);
        //});
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

    public boolean setPlayerPermissions(Player player, boolean database) {
        final PermissionAttachment permissionAttachment = player.addAttachment(this);

        if(database) {
            getPermissionsPlayer(player.getUniqueId(), permissionsPlayer -> {
                final Map<String, Long> playerPermissions = permissionsPlayer.getPermissions();

                for(String permission : playerPermissions.keySet()) {
                    if(playerPermissions.get(permission) >= System.currentTimeMillis() | playerPermissions.get(permission) == -1) {
                        permissionAttachment.setPermission(permission, true);
                    } else {
                        playerPermissions.remove(permission);
                    }
                }

                final PermissionsGroup playerGroup = getPermissionsGroup(permissionsPlayer.getGroupId());

                for(String permission : playerGroup.getPermissions().keySet()) {
                    if(playerGroup.getPermissions().get(permission) >= System.currentTimeMillis() | playerGroup.getPermissions().get(permission) == -1) {
                        permissionAttachment.setPermission(permission, true);
                    } else {
                        playerGroup.getPermissions().remove(permission);
                    }
                }

                for(UUID interhance : playerGroup.getInterhances()) {
                    final PermissionsGroup interhaceGroup = getPermissionsGroup(interhance);

                    for(String permission : interhaceGroup.getPermissions().keySet()) {
                        if(interhaceGroup.getPermissions().get(permission) >= System.currentTimeMillis() | interhaceGroup.getPermissions().get(permission) == -1) {
                            permissionAttachment.setPermission(permission, true);
                        } else {
                            interhaceGroup.getPermissions().remove(permission);
                        }
                    }

                }

                if(VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Player.Update-Displayname")) {
                    player.setDisplayName(playerGroup.getColor() + player.getName());
                }

                player.recalculatePermissions();

                if(player.hasPermission("vipperms.autoop")) {
                    player.setOp(true);
                }

            }, true);
        } else {
            final PermissionsPlayer permissionsPlayer = getPermissionsPlayer(player.getUniqueId());

            final Map<String, Long> playerPermissions = permissionsPlayer.getPermissions();

            for(String permission : playerPermissions.keySet()) {
                if(playerPermissions.get(permission) >= System.currentTimeMillis() | playerPermissions.get(permission) == -1) {
                    permissionAttachment.setPermission(permission, true);
                }
            }

            final PermissionsGroup playerGroup = getPermissionsGroup(permissionsPlayer.getGroupId());

            for(String permission : playerGroup.getPermissions().keySet()) {
                if(playerGroup.getPermissions().get(permission) >= System.currentTimeMillis() | playerGroup.getPermissions().get(permission) == -1) {
                    permissionAttachment.setPermission(permission, true);
                }
            }

            for(UUID interhance : playerGroup.getInterhances()) {
                final PermissionsGroup interhaceGroup = getPermissionsGroup(interhance);

                for(String permission : interhaceGroup.getPermissions().keySet()) {
                    if(interhaceGroup.getPermissions().get(permission) >= System.currentTimeMillis() | interhaceGroup.getPermissions().get(permission) == -1) {
                        permissionAttachment.setPermission(permission, true);
                    }
                }

            }

            if(VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Player.Update-Displayname")) {
                player.setDisplayName(playerGroup.getColor() + player.getName());
            }

            player.recalculatePermissions();

            if(player.hasPermission("vipperms.autoop")) {
                player.setOp(true);
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
