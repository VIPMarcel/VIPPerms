package vip.marcel.vipperms.spigot.vipperms.utils.database;

import vip.marcel.vipperms.spigot.vipperms.VIPPerms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class MySQL {

    private final String hostname, database, username, password;
    protected String tables;
    private final int port;

    private Connection connection;

    private DatabaseGroups databaseGroups;
    private DatabasePlayers databasePlayers;

    public MySQL() {
        this.hostname = VIPPerms.getInstance().getDatabaseConfiguration().getString("Database.MySQL.Hostname");
        this.database = VIPPerms.getInstance().getDatabaseConfiguration().getString("Database.MySQL.Database");
        this.tables = VIPPerms.getInstance().getDatabaseConfiguration().getString("Database.MySQL.Tables");
        this.username = VIPPerms.getInstance().getDatabaseConfiguration().getString("Database.MySQL.Username");
        this.password = VIPPerms.getInstance().getDatabaseConfiguration().getString("Database.MySQL.Password");
        this.port = VIPPerms.getInstance().getDatabaseConfiguration().getInteger("Database.MySQL.Port");

        this.databaseGroups = new DatabaseGroups();
        this.databasePlayers = new DatabasePlayers();
    }

    public void connect() {
        try {
            if(this.connection == null) {
                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
                VIPPerms.getInstance().getLogger().log(Level.INFO, "MySQL connection successfully opend!");
            }

            {
                PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + this.tables + "groups(id INT AUTO_INCREMENT PRIMARY KEY, UUID TEXT, Name TEXT, Prefix TEXT, Suffix TEXT, Color TEXT, Interhances TEXT, Permissions TEXT)");
                statement.executeUpdate();
                statement.close();
            }

            {
                PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + this.tables + "players(id INT AUTO_INCREMENT PRIMARY KEY, UUID TEXT, Name TEXT, Group TEXT, Permissions TEXT)");
                statement.executeUpdate();
                statement.close();
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
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
