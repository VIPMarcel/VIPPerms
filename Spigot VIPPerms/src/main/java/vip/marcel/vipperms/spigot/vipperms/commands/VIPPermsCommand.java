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

        return true;
    }

}
