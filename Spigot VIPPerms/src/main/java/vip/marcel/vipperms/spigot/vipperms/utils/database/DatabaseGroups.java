package vip.marcel.vipperms.spigot.vipperms.utils.database;

import vip.marcel.vipperms.spigot.vipperms.VIPPerms;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseGroups {

    private MySQL mySQL;

    private String table;

    public DatabaseGroups() {
        this.mySQL = VIPPerms.getInstance().getMySQL();

        this.table = VIPPerms.getInstance().getMySQL().getTables() + "groups";
    }

    public boolean groupExists(UUID uuid) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                return (resultSet.getString("UUID") != null);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean groupExists(String name) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE Name = ?");
            statement.setString(1, name);

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                return (resultSet.getString("Name") != null);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createGroup(String name) {
        final UUID uniqueId = new UUID(System.currentTimeMillis(), 0);

        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("INSERT INTO " + table + "(UUID, Name, Prefix, Suffix, Color, Interhances, Permissions) VALUES (?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, uniqueId.toString());
            statement.setString(2, name);
            statement.setString(3, "");
            statement.setString(4, "");
            statement.setString(5, "");
            statement.setString(6, "");
            statement.setString(7, "");

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID getUUID(String name) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE Name = ?");
            statement.setString(1, name);

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                return UUID.fromString(resultSet.getString("UUID"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
