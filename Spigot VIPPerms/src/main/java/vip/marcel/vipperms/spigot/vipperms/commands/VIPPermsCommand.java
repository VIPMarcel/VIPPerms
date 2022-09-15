package vip.marcel.vipperms.spigot.vipperms.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;
import vip.marcel.vipperms.spigot.vipperms.api.values.PlayerValue;
import vip.marcel.vipperms.spigot.vipperms.events.PlayerGroupChangeEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class VIPPermsCommand implements CommandExecutor, TabExecutor {

    public VIPPermsCommand() {
        VIPPerms.getInstance().getCommand("vipperms").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {

        if(!sender.hasPermission("vipperms.*") && sender instanceof Player) {
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + VIPPerms.getInstance().getNoPermissions());
            return true;
        }

        if(arguments.length == 0) {
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Benutze §e/vp help §7für Hilfe.");
            return true;
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
                return true;
            }
            final String groupName = arguments[1].toLowerCase();

            if(VIPPerms.getInstance().getMySQL().getDatabaseGroups().groupExists(groupName)) {
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Die Gruppe §e" + groupName + " §7ist bereits registriert.");
                return true;
            }

            VIPPerms.getInstance().getMySQL().getDatabaseGroups().createGroup(groupName);
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Gruppe §e" + groupName + " §7erstellt.");

        } else if(arguments[0].equalsIgnoreCase("setGroup")) {

            if(arguments.length == 1) {
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Gebe einen §eSpielernamen §7an, um eine Gruppe zu setzen.");
                return true;
            }
            final String playerName = arguments[1];
            final UUID uuid = VIPPerms.getInstance().getMySQL().getDatabasePlayers().getUUID(playerName);

            if(uuid == null) {
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Der Spieler §e" + playerName + " §7ist nicht registriert.");
                return true;
            }

            if(arguments.length == 2) {
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Du musst einen §eGruppennamen §7angeben und ggf. eine §eZeit§7.");
                return true;
            }
            final String groupName = arguments[2];

            if(!VIPPerms.getInstance().getMySQL().getDatabaseGroups().groupExists(groupName)) {
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Die Gruppe §e" + groupName + " §7ist nicht registriert.");
                return true;
            }

            if(arguments.length == 3) {
                VIPPerms.getInstance().updatePermissionsPlayer(uuid, PlayerValue.GROUPID, VIPPerms.getInstance().getPermissionsGroup(groupName.toLowerCase()).getUUID());
                VIPPerms.getInstance().updatePermissionsPlayer(uuid, PlayerValue.GROUP_EXPIRES, (long) -1);
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§e" + playerName + " §7hat die Gruppe §e" + groupName + " §8» §aLifetime §7erhalten.");

                if(VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Settings.BungeeCord")) {
                    if(Bukkit.getPlayer(uuid) != null) {
                        sendReloadPlayer(Bukkit.getPlayer(uuid));
                    }
                } else {
                    if(VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Kick.Enable")) {
                        if(Bukkit.getPlayer(uuid) != null) {
                            String kickMessage = ChatColor.translateAlternateColorCodes('&', VIPPerms.getInstance().getSettingsConfiguration().getString("Kick.Message"));
                            String name = VIPPerms.getInstance().getPermissionsGroup(groupName).getName();
                            name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();

                            kickMessage = kickMessage.replace("{groupcolor}", VIPPerms.getInstance().getPermissionsGroup(groupName).getColor());
                            kickMessage = kickMessage.replace("{groupname}", name);
                            kickMessage = kickMessage.replace("{time}", "Lifetime");

                            Bukkit.getPlayer(uuid).kickPlayer(kickMessage);
                        }
                        Bukkit.getPluginManager().callEvent(new PlayerGroupChangeEvent(uuid, VIPPerms.getInstance().getPermissionsGroup(groupName).getUUID(), false));
                    } else {
                        if(Bukkit.getPlayer(uuid) != null) {
                            VIPPerms.getInstance().resetPlayerPermissions(Bukkit.getPlayer(uuid));
                            VIPPerms.getInstance().setPlayerPermissions(Bukkit.getPlayer(uuid), true);

                            Bukkit.getServer().getOnlinePlayers().forEach(players -> {
                                VIPPerms.getInstance().setScoreboard(players);
                            });
                        }
                        Bukkit.getPluginManager().callEvent(new PlayerGroupChangeEvent(uuid, VIPPerms.getInstance().getPermissionsGroup(groupName).getUUID(), true));
                    }
                }

            }

            if(arguments.length == 4) {
                VIPPerms.getInstance().updatePermissionsPlayer(uuid, PlayerValue.GROUPID, VIPPerms.getInstance().getPermissionsGroup(groupName.toLowerCase()).getUUID());
                VIPPerms.getInstance().updatePermissionsPlayer(uuid, PlayerValue.GROUP_EXPIRES, VIPPerms.getInstance().getGroupExpiresTimeHelper().getExpiresTimeMillis(arguments[3]));
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§e" + playerName + " §7hat die Gruppe §e" + groupName + " §8» §c" + arguments[3] + " §7erhalten.");

                if(VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Settings.BungeeCord")) {
                    if(Bukkit.getPlayer(uuid) != null) {
                        sendReloadPlayer(Bukkit.getPlayer(uuid));
                    }
                } else {
                    if(VIPPerms.getInstance().getSettingsConfiguration().getBoolean("Kick.Enable")) {
                        if(Bukkit.getPlayer(uuid) != null) {
                            String kickMessage = ChatColor.translateAlternateColorCodes('&', VIPPerms.getInstance().getSettingsConfiguration().getString("Kick.Message"));
                            String name = VIPPerms.getInstance().getPermissionsGroup(groupName).getName();
                            name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();

                            kickMessage = kickMessage.replace("{groupcolor}", VIPPerms.getInstance().getPermissionsGroup(groupName).getColor());
                            kickMessage = kickMessage.replace("{groupname}", name);
                            kickMessage = kickMessage.replace("{time}", arguments[3]);

                            Bukkit.getPlayer(uuid).kickPlayer(kickMessage);
                        }
                        Bukkit.getPluginManager().callEvent(new PlayerGroupChangeEvent(uuid, VIPPerms.getInstance().getPermissionsGroup(groupName).getUUID(), false));
                    } else {
                        if(Bukkit.getPlayer(uuid) != null) {
                            VIPPerms.getInstance().resetPlayerPermissions(Bukkit.getPlayer(uuid));
                            VIPPerms.getInstance().setPlayerPermissions(Bukkit.getPlayer(uuid), true);

                            Bukkit.getServer().getOnlinePlayers().forEach(players -> {
                                VIPPerms.getInstance().setScoreboard(players);
                            });
                        }
                        Bukkit.getPluginManager().callEvent(new PlayerGroupChangeEvent(uuid, VIPPerms.getInstance().getPermissionsGroup(groupName).getUUID(), true));
                    }
                }

            }

        } else if(arguments[0].equalsIgnoreCase("reload")) {

            try {
                Method method = Class.forName("vip.marcel.vipperms.spigot.vipperms.VIPPerms").getDeclaredMethod("loadGroupsCache");
                method.setAccessible(true);
                method.invoke(VIPPerms.getInstance());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            Bukkit.getServer().getOnlinePlayers().forEach(players -> {
                VIPPerms.getInstance().setScoreboard(players);
            });

            sendReloadGroups();
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Du hast den §eGruppen- Cache §7neu initialisiert.");

        } else {
            sendHelpTopic(sender);
        }

        return true;
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

    private void sendReloadPlayer(Player player) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(outStream);

        try {
            out.writeUTF(player.getUniqueId().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(VIPPerms.getInstance(), "vipperms:reloadplayer", outStream.toByteArray());
    }

    private void sendReloadGroups() {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Bukkit.getServer().sendPluginMessage(VIPPerms.getInstance(), "vipperms:reloadplayer", outStream.toByteArray());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
        List<String> output = new ArrayList<>();

        if(command.getName().equalsIgnoreCase("vipperms") | command.getName().equalsIgnoreCase("vp")) {
            if(sender.hasPermission("vipperms.*")) {

                if(arguments.length == 1) {
                    output.add("help");
                    output.add("creategroup");
                    output.add("setgroup");
                    output.add("reload");
                }

                if(arguments[0].equalsIgnoreCase("setgroup")) {
                    if(arguments.length == 2) {
                        for(Player players : Bukkit.getServer().getOnlinePlayers()) {
                            output.add(players.getName());
                        }
                    } else if(arguments.length == 3) {
                        VIPPerms.getInstance().getPermissionsGroups().forEach(group -> {
                            output.add(group.getName());
                        });
                    }
                }

            }
        }

        return output.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
