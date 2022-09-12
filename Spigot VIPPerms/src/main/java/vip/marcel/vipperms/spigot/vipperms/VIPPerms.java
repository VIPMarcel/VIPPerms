package vip.marcel.vipperms.spigot.vipperms;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import vip.marcel.vipperms.spigot.vipperms.utils.config.DatabaseConfiguration;
import vip.marcel.vipperms.spigot.vipperms.utils.database.MySQL;

public final class VIPPerms extends JavaPlugin {

    private static VIPPerms instance;

    private DatabaseConfiguration databaseConfiguration;

    private MySQL mySQL;

    @Override
    public void onEnable() {
        instance = this;
        this.init();
    }

    @Override
    public void onDisable() {
        this.mySQL.disconnect();
    }

    public static VIPPerms getInstance() {
        return instance;
    }

    private void init() {
        this.databaseConfiguration = new DatabaseConfiguration();

        this.mySQL = new MySQL();
        this.mySQL.connect();
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return this.databaseConfiguration;
    }

    public MySQL getMySQL() {
        return this.mySQL;
    }

}
