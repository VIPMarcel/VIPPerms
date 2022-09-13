package vip.marcel.vipperms.spigot.vipperms.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;

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
        /vp <group> create
        /vp <group> delete
        /vp <group> info
        /vp <group> set prefix/suffix/color <arguments>
        /vp <group> interhace add/remove <Group-Name>
        /vp <group> permission add/remove <Permission>

        /vp <user> delete
        /vp <user> info
        /vp <user> setgroup <Group> <Time-String>
        /vp <user> permission add/remove <Permission>
         */

        if(arguments[0].equalsIgnoreCase("help")) {

            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §egroup §7<Name> §8| §7Gruppe bearbeiten");
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "§8/§bvp §euser §7<Name> §8| §7Spieler bearbeiten");

        } else {
            sender.sendMessage(VIPPerms.getInstance().getPrefix() + "Benutze §e/vp help §7für Hilfe.");
            return true;
        }

        return true;
    }

}
