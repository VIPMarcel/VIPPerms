package vip.marcel.vipperms.spigot.vipperms.utils.database;

import org.bukkit.Bukkit;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;
import vip.marcel.vipperms.spigot.vipperms.utils.config.DatabaseConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class MySQL {

    private DatabaseConfiguration databaseConfiguration;

    private final String hostname, database, username, password;
    protected String tables;
    private final int port;

    private Connection connection;

    private DatabaseGroups databaseGroups;
    private DatabasePlayers databasePlayers;

    public MySQL() {
        this.databaseConfiguration = new DatabaseConfiguration();

        this.hostname = this.databaseConfiguration.getString("Database.MySQL.Hostname");
        this.database = this.databaseConfiguration.getString("Database.MySQL.Database");
        this.tables = this.databaseConfiguration.getString("Database.MySQL.Tables");
        this.username = this.databaseConfiguration.getString("Database.MySQL.Username");
        this.password = this.databaseConfiguration.getString("Database.MySQL.Password");
        this.port = this.databaseConfiguration.getInteger("Database.MySQL.Port");
    }

    public void connect() {
        try {
            if(this.connection == null) {
                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
                VIPPerms.getInstance().getLogger().log(Level.INFO, "MySQL connection successfully opend!");
            }

            {
                final PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + this.tables + "groups (id INT AUTO_INCREMENT PRIMARY KEY, UUID TEXT, GroupName TEXT, TabSortIndex TEXT, Prefix TEXT, Suffix TEXT, Color TEXT, Interhances TEXT, Permissions TEXT)");
                statement.executeUpdate();
                statement.close();
            }

            {
                final PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + this.tables + "players (id INT AUTO_INCREMENT PRIMARY KEY, UUID TEXT, PlayerName TEXT, GroupId TEXT, GroupExpires TEXT, Permissions TEXT)");
                statement.executeUpdate();
                statement.close();
            }

        } catch(SQLException e) {
            e.printStackTrace();
            VIPPerms.getInstance().getLogger().log(Level.INFO, "MySQL connection failed, plugin disabled!");
            Bukkit.getPluginManager().disablePlugin(VIPPerms.getInstance());
        }

        this.databaseGroups = new DatabaseGroups();
        this.databasePlayers = new DatabasePlayers();

        this.databaseGroups.createDefaultGroup();
    }

    public void disconnect() {
        try {
            if(this.connection != null) {
                this.connection.close();
                VIPPerms.getInstance().getLogger().log(Level.INFO, "MySQL connection successfully closed!");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public List<UUID> getAllPermissionsGroups() {
        return this.databaseGroups.loadAllGroups();
    }

    public Connection getConnection() {
        return this.connection;
    }

    protected String getTables() {
        return this.tables;
    }

    public DatabaseGroups getDatabaseGroups() {
        return this.databaseGroups;
    }

    public DatabasePlayers getDatabasePlayers() {
        return this.databasePlayers;
    }

}
