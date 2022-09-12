package vip.marcel.vipperms.spigot.vipperms.utils.database;

import com.google.common.collect.Maps;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabasePlayers {

    private MySQL mySQL;

    private String table;

    public DatabasePlayers() {
        this.mySQL = VIPPerms.getInstance().getMySQL();

        this.table = VIPPerms.getInstance().getMySQL().getTables() + "players";
    }

    public boolean playerExists(UUID uuid) {
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

    public void createPlayer(UUID uuid, String name) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("INSERT INTO " + table + "(UUID, PlayerName, GroupId, GroupExpires, Permissions) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, uuid.toString());
            statement.setString(2, name);
            statement.setString(3, UUID.fromString("00000183-31c6-bb2a-0000-000000000000").toString());
            statement.setLong(4, -1);
            statement.setString(5, "");

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getName(UUID uuid) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                return resultSet.getString("PlayerName");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UUID getGroupUUID(UUID uuid) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                return UUID.fromString(resultSet.getString("GroupId"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getGroupExpires(UUID uuid) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                return resultSet.getLong("GroupExpires");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Map<String, Long> getPermissions(UUID uuid) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                String permissions = resultSet.getString("Permissions");
                String[] splittet = permissions.split(";");

                final List<String> permissionsList = new ArrayList<>();
                final Map<String, Long> permissionsMap = new HashMap<>();

                for(int i = 0; i < splittet.length; i++) {
                    permissionsList.add(splittet[i].replaceAll(";", ""));
                }

                for(String permission : permissionsList) {
                    String permissionName = permission.split(":")[0];
                    long timeStamp = Long.parseLong(permission.split(":")[1]);

                    permissionsMap.put(permissionName, timeStamp);
                }

                return permissionsMap;
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Maps.newHashMap();
    }

    public void setName(UUID uuid, String name) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("UPDATE " + table + " SET PlayerName = ? WHERE UUID = ?");
            statement.setString(1, name);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setGroup(UUID uuid, UUID groupUniqueId) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("UPDATE " + table + " SET GroupId = ? WHERE UUID = ?");
            statement.setString(1, groupUniqueId.toString());
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setGroupExpires(UUID uuid, long groupExpiresAt) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("UPDATE " + table + " SET GroupExpires = ? WHERE UUID = ?");
            statement.setLong(1, groupExpiresAt);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPermissions(UUID uuid, Map<String, Long> permissions) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("UPDATE " + table + " SET Permissions = ? WHERE UUID = ?");

            final StringBuilder builder = new StringBuilder();

            for(String permission : permissions.keySet()) {
                builder.append(";").append(permission).append(":").append(permissions.get(permission));
            }

            statement.setString(1, builder.toString().replaceFirst(";", ""));
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
