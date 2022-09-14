package vip.marcel.vipperms.spigot.vipperms.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;
import vip.marcel.vipperms.spigot.vipperms.api.values.PlayerValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class VIPPermsCommand implements CommandExecutor {

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
                //TODO: Send plugin message channel (kick player)
            }

            if(arguments.length == 4) {
                VIPPerms.getInstance().updatePermissionsPlayer(uuid, PlayerValue.GROUPID, VIPPerms.getInstance().getPermissionsGroup(groupName.toLowerCase()).getUUID());
                VIPPerms.getInstance().updatePermissionsPlayer(uuid, PlayerValue.GROUP_EXPIRES, VIPPerms.getInstance().getGroupExpiresTimeHelper().getExpiresTimeMillis(arguments[3]));
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§e" + playerName + " §7hat die Gruppe §e" + groupName + " §8» §c" + arguments[3] + " §7erhalten.");
                //TODO: Send plugin message channel (kick player)
            }

        } else if(arguments[0].equalsIgnoreCase("reload")) {

            try {
                Method method = Class.forName("vip.marcel.vipperms.spigot.vipperms.VIPPerms").getDeclaredMethod("loadGroupsCache");
                method.setAccessible(true);
                method.invoke(VIPPerms.getInstance());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Du hast den §eGruppen- Cache §7neu initialisiert.");
            //TODO: Send plugin message channel

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

}
