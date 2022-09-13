package vip.marcel.vipperms.spigot.vipperms.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;

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
         */

        if(arguments[0].equalsIgnoreCase("help")) {

            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §egroup §7<Name> §8| §7Gruppe bearbeiten");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §euser §7<Name> §8| §7Spieler bearbeiten");

        } else if(arguments[0].equalsIgnoreCase("group")) {
        /*
        /vp group[0] <group>[1] create[2]
        /vp group[0] <group>[1] delete[2]
        /vp group[0] <group>[1] info[2]
        /vp group[0] <group>[1] prefix/suffix/color[2] <arguments>[3++]
        /vp group[0] <group>[1] interhace[2] add/remove[3] <Group-Name>[4]
        /vp group[0] <group>[1] permission[2] add/remove[3] <Permission>[4]
         */
        } else if(arguments[0].equalsIgnoreCase("user")) {
        /*
        /vp user[0] <user>[1] delete[2]
        /vp user[0] <user>[1] info[2]
        /vp user[0] <user>[1] setgroup[2] <Group>[3] <Time-String>[4]
        /vp user[0] <user>[1] permission[2] add/remove[3] <Permission>[4]
         */

            final UUID uuid = VIPPerms.getInstance().getMySQL().getDatabasePlayers().getUUID(arguments[1]);
            final String name = VIPPerms.getInstance().getMySQL().getDatabasePlayers().getName(uuid);

            if(!VIPPerms.getInstance().getMySQL().getDatabasePlayers().playerExists(uuid)) {
                sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Der Spieler §e" + arguments[1] + " §7ist nicht registriert.");
                return true;
            }


            if(arguments[2].equalsIgnoreCase("delete")) {

            } else if(arguments[2].equalsIgnoreCase("info")) {

            } else if(arguments[2].equalsIgnoreCase("setgroup")) {

            } else if(arguments[2].equalsIgnoreCase("permission")) {

            } else {

            }


        } else {
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Benutze §e/vp help §7für Hilfe.");
            return true;
        }

        return true;
    }

}
