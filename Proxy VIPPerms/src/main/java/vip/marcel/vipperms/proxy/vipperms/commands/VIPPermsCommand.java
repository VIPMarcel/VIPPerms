package vip.marcel.vipperms.proxy.vipperms.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.vipperms.proxy.vipperms.VIPPerms;
import vip.marcel.vipperms.proxy.vipperms.api.values.PlayerValue;
import vip.marcel.vipperms.proxy.vipperms.events.PlayerGroupChangeEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class VIPPermsCommand extends Command {

    public VIPPermsCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.setPermissionMessage(VIPPerms.getInstance().getPrefix() + VIPPerms.getInstance().getNoPermissions());
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(!sender.hasPermission("vipperms.*") && sender instanceof ProxiedPlayer) {
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + VIPPerms.getInstance().getNoPermissions());
            return;
        }

        if(arguments.length == 0) {
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Benutze §e/vp help §7für Hilfe.");
            return;
        }

        /*
        /vp group[0] <group>[1] create[2]
        /vp group[0] <group>[1] delete[2]
        /vp group[0] <group>[1] info[2]
        /vp group[0] <group>[1] prefix/suffix/color[2] <arguments>[3++]
        /vp group[0] <group>[1] interhace[2] add/remove[3] <Group-Name>[4]
        /vp group[0] <group>[1] permission[2] add/remove[3] <Permission>[4]

        /vp user[0] <user>[1] delete[2]
        /vp user[0] <user>[1] info[2]
        /vp user[0] <user>[1] setgroup[2] <Group>[3] <Time-String>[4]
        /vp user[0] <user>[1] permission[2] add/remove[3] <Permission>[4]

        /vp reload?
         */

        if(arguments[0].equalsIgnoreCase("help")) {
            sendHelpTopic(sender);
        } else if(arguments[0].equalsIgnoreCase("createGroup")) {

            if(arguments.length == 1) {
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Gebe einen §eGruppennamen §7an, um sie zu erstellen.");
                return;
            }
            final String groupName = arguments[1].toLowerCase();

            if(VIPPerms.getInstance().getMySQL().getDatabaseGroups().groupExists(groupName)) {
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Die Gruppe §e" + groupName + " §7ist bereits registriert.");
                return;
            }

            VIPPerms.getInstance().getMySQL().getDatabaseGroups().createGroup(groupName);
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Gruppe §e" + groupName + " §7erstellt.");

        } else if(arguments[0].equalsIgnoreCase("setGroup")) {

            if(arguments.length == 1) {
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Gebe einen §eSpielernamen §7an, um eine Gruppe zu setzen.");
                return;
            }
            final String playerName = arguments[1];
            final UUID uuid = VIPPerms.getInstance().getMySQL().getDatabasePlayers().getUUID(playerName);

            if(uuid == null) {
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Der Spieler §e" + playerName + " §7ist nicht registriert.");
                return;
            }

            if(arguments.length == 2) {
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Du musst einen §eGruppennamen §7angeben und ggf. eine §eZeit§7.");
                return;
            }
            final String groupName = arguments[2];

            if(!VIPPerms.getInstance().getMySQL().getDatabaseGroups().groupExists(groupName)) {
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Die Gruppe §e" + groupName + " §7ist nicht registriert.");
                return;
            }

            if(arguments.length == 3) {
                VIPPerms.getInstance().updatePermissionsPlayer(uuid, PlayerValue.GROUPID, VIPPerms.getInstance().getPermissionsGroup(groupName.toLowerCase()).getUUID());
                VIPPerms.getInstance().updatePermissionsPlayer(uuid, PlayerValue.GROUP_EXPIRES, (long) -1);
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§e" + playerName + " §7hat die Gruppe §e" + groupName + " §8» §aLifetime §7erhalten.");

                if(VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Kick.Enable")) {
                    if(ProxyServer.getInstance().getPlayer(uuid) != null) {
                        String kickMessage = ChatColor.translateAlternateColorCodes('&', VIPPerms.getInstance().getSettingsConfiguration().getString("Kick.Message"));
                        String name = VIPPerms.getInstance().getPermissionsGroup(groupName).getName();
                        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();

                        kickMessage = kickMessage.replace("{groupcolor}", VIPPerms.getInstance().getPermissionsGroup(groupName).getColor());
                        kickMessage = kickMessage.replace("{groupname}", name);
                        kickMessage = kickMessage.replace("{time}", "Lifetime");

                        ProxyServer.getInstance().getPlayer(uuid).disconnect(kickMessage);
                    }
                    ProxyServer.getInstance().getPluginManager().callEvent(new PlayerGroupChangeEvent(uuid, VIPPerms.getInstance().getPermissionsGroup(groupName).getUUID(), false));
                } else {
                    if(ProxyServer.getInstance().getPlayer(uuid) != null) {
                        VIPPerms.getInstance().resetPlayerPermissions(ProxyServer.getInstance().getPlayer(uuid));
                        VIPPerms.getInstance().setPlayerPermissions(ProxyServer.getInstance().getPlayer(uuid), true);

                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeUTF(uuid.toString());
                        ProxyServer.getInstance().getPlayer(uuid).getServer().getInfo().sendData("vipperms:reloadplayer", out.toByteArray());
                    }
                    ProxyServer.getInstance().getPluginManager().callEvent(new PlayerGroupChangeEvent(uuid, VIPPerms.getInstance().getPermissionsGroup(groupName).getUUID(), true));
                }

            }

            if(arguments.length == 4) {
                VIPPerms.getInstance().updatePermissionsPlayer(uuid, PlayerValue.GROUPID, VIPPerms.getInstance().getPermissionsGroup(groupName.toLowerCase()).getUUID());
                VIPPerms.getInstance().updatePermissionsPlayer(uuid, PlayerValue.GROUP_EXPIRES, VIPPerms.getInstance().getGroupExpiresTimeHelper().getExpiresTimeMillis(arguments[3]));
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§e" + playerName + " §7hat die Gruppe §e" + groupName + " §8» §c" + arguments[3] + " §7erhalten.");

                if(VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Kick.Enable")) {
                    if(ProxyServer.getInstance().getPlayer(uuid) != null) {
                        String kickMessage = ChatColor.translateAlternateColorCodes('&', VIPPerms.getInstance().getSettingsConfiguration().getString("Kick.Message"));
                        String name = VIPPerms.getInstance().getPermissionsGroup(groupName).getName();
                        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();

                        kickMessage = kickMessage.replace("{groupcolor}", VIPPerms.getInstance().getPermissionsGroup(groupName).getColor());
                        kickMessage = kickMessage.replace("{groupname}", name);
                        kickMessage = kickMessage.replace("{time}", arguments[3]);

                        ProxyServer.getInstance().getPlayer(uuid).disconnect(kickMessage);
                    }
                    ProxyServer.getInstance().getPluginManager().callEvent(new PlayerGroupChangeEvent(uuid, VIPPerms.getInstance().getPermissionsGroup(groupName).getUUID(), false));
                } else {
                    if(ProxyServer.getInstance().getPlayer(uuid) != null) {
                        VIPPerms.getInstance().resetPlayerPermissions(ProxyServer.getInstance().getPlayer(uuid));
                        VIPPerms.getInstance().setPlayerPermissions(ProxyServer.getInstance().getPlayer(uuid), true);

                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeUTF(uuid.toString());
                        ProxyServer.getInstance().getPlayer(uuid).getServer().getInfo().sendData("vipperms:reloadplayer", out.toByteArray());
                    }
                    ProxyServer.getInstance().getPluginManager().callEvent(new PlayerGroupChangeEvent(uuid, VIPPerms.getInstance().getPermissionsGroup(groupName).getUUID(), true));
                }

            }

        } else if(arguments[0].equalsIgnoreCase("reload")) {

            try {
                Method method = Class.forName("vip.marcel.vipperms.proxy.vipperms.VIPPerms").getDeclaredMethod("loadGroupsCache");
                method.setAccessible(true);
                method.invoke(VIPPerms.getInstance());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            for(String server : ProxyServer.getInstance().getServers().keySet()) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                ProxyServer.getInstance().getServerInfo(server).sendData("vipperms:reloadgroups", out.toByteArray());
            }


            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Du hast den §eGruppen- Cache §7neu initialisiert.");

        } else {
            sendHelpTopic(sender);
        }

        return;
    }

    private void sendHelpTopic(CommandSender sender) {
        /*sender.sendMessage(VIPPerms.getInstance().getPrefix());
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "   §7§oGruppen- Befehle §8»");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §egroup §7<Name> create");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §egroup §7<Name> delete");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §egroup §7<Name> info");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §egroup §7<Name> prefix <Prefix>");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §egroup §7<Name> suffix <Suffix>");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §egroup §7<Name> color <Color>");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §egroup §7<Name> interhance add/remove <Gruppe>");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §egroup §7<Name> permission add/remove <Permission>");
            sender.sendMessage(VIPPerms.getInstance().getPrefix());
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "   §7§oSpieler- Befehle §8»");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §euser §7<Name> delete");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §euser §7<Name> info");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §euser §7<Name> setGroup <Gruppe> [Zeit]");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §euser §7<Name> permission add/remove <Permission>");
            sender.sendMessage(VIPPerms.getInstance().getPrefix());*/

        sender.sendMessage(VIPPerms.getInstance().getPrefix());
        sender.sendMessage(VIPPerms.getInstance().getPrefix() + "   §7§oGruppen- Befehle §8»");
        sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §ecreateGroup §8<§7Name§8>");
        sender.sendMessage(VIPPerms.getInstance().getPrefix());
        sender.sendMessage(VIPPerms.getInstance().getPrefix() + "   §7§oSpieler- Befehle §8»");
        sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §esetGroup §8<§7Spieler§8> <§7Gruppe§8> [§7Zeit§8]");
        sender.sendMessage(VIPPerms.getInstance().getPrefix());
        sender.sendMessage(VIPPerms.getInstance().getPrefix() + "   §7§oSystem- Befehle §8»");
        sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §ereload §8| §7Nur Gruppen- Cache");
        sender.sendMessage(VIPPerms.getInstance().getPrefix());
    }

}
