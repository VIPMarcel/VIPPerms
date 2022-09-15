package vip.marcel.vipperms.spigot.vipperms.utils.database;

import com.google.common.collect.Lists;
import vip.marcel.vipperms.spigot.vipperms.VIPPerms;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

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
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE GroupName = ?");
            statement.setString(1, name);

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                return (resultSet.getString("GroupName") != null);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void createDefaultGroup() {
        final UUID uniqueId = UUID.fromString("00000183-31c6-bb2a-0000-000000000000");

        if(groupExists(uniqueId)) {
            return;
        }

        VIPPerms.getInstance().getLogger().log(Level.INFO, "Group 'default' automatically created.");

        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("INSERT INTO " + table + "(UUID, GroupName, TabSortIndex, Prefix, Suffix, Color, Interhances, Permissions) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, uniqueId.toString());
            statement.setString(2, "default");
            statement.setInt(3, 0);
            statement.setString(4, "");
            statement.setString(5, "");
            statement.setString(6, "");
            statement.setString(7, "");
            statement.setString(8, "group.default:-1");

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected List<UUID> loadAllGroups() {
        final List<UUID> groups = Lists.newArrayList();

        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table);
            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                groups.add(UUID.fromString(resultSet.getString("UUID")));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }

    public void createGroup(String name) {
        final UUID uniqueId = new UUID(System.currentTimeMillis(), 0);

        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("INSERT INTO " + table + "(UUID, GroupName, TabSortIndex, Prefix, Suffix, Color, Interhances, Permissions) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, uniqueId.toString());
            statement.setString(2, name);
            statement.setInt(3, 0);
            statement.setString(4, "");
            statement.setString(5, "");
            statement.setString(6, "");
            statement.setString(7, "");
            statement.setString(8, "group." + name + ":-1");

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGroup(UUID uuid) {
        if(groupExists(uuid)) {
            try {
                PreparedStatement statement = this.mySQL.getConnection().prepareStatement("DELETE FROM " + table + " WHERE UUID = ?");
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
                statement.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public UUID getUUID(String name) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE GroupName = ?");
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

    public int getTabSortIndex(UUID uuid) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                return resultSet.getInt("TabSortIndex");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getName(UUID uuid) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                return resultSet.getString("GroupName");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPrefix(UUID uuid) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                return resultSet.getString("Prefix");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSuffix(UUID uuid) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                return resultSet.getString("Suffix");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getColor(UUID uuid) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                return resultSet.getString("Color");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UUID> getInterhances(UUID uuid) {
        final List<UUID> interhancesList = new ArrayList<>();

        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                String interhances = resultSet.getString("Interhances");
                String[] splittet = interhances.split(";");

                try {
                    for(int i = 0; i < splittet.length; i++) {
                        interhancesList.add(UUID.fromString(splittet[i].replaceAll(";", "")));
                    }
                } catch (IllegalArgumentException ignore) {}

            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interhancesList;
    }

    public Map<String, Long> getPermissions(UUID uuid) {
        final Map<String, Long> permissionsMap = new HashMap<>();

        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                String permissions = resultSet.getString("Permissions");
                String[] splittet = permissions.split(";");

                final List<String> permissionsList = new ArrayList<>();

                for(int i = 0; i < splittet.length; i++) {
                    permissionsList.add(splittet[i].replaceAll(";", ""));
                }

                try {
                    for(String permission : permissionsList) {
                        String permissionName = permission.split(":")[0];
                        long timeStamp = Long.parseLong(permission.split(":")[1]);

                        permissionsMap.put(permissionName, timeStamp);
                    }
                } catch (ArrayIndexOutOfBoundsException ignore) {}

            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permissionsMap;
    }

    public void setName(UUID uuid, String name) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("UPDATE " + table + " SET GroupName = ? WHERE UUID = ?");
            statement.setString(1, name);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTabSortIndex(UUID uuid, int index) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("UPDATE " + table + " SET TabSortIndex = ? WHERE UUID = ?");
            statement.setInt(1, index);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPrefix(UUID uuid, String prefix) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("UPDATE " + table + " SET Prefix = ? WHERE UUID = ?");
            statement.setString(1, prefix);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setSuffix(UUID uuid, String suffix) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("UPDATE " + table + " SET Suffix = ? WHERE UUID = ?");
            statement.setString(1, suffix);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setColor(UUID uuid, String color) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("UPDATE " + table + " SET Color = ? WHERE UUID = ?");
            statement.setString(1, color);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setInterhances(UUID uuid, List<UUID> interhances) {
        try {
            final PreparedStatement statement = this.mySQL.getConnection().prepareStatement("UPDATE " + table + " SET Interhances = ? WHERE UUID = ?");

            final StringBuilder builder = new StringBuilder();

            for(UUID interhanceId : interhances) {
                builder.append(";").append(interhanceId.toString());
            }

            statement.setString(1, builder.toString().replaceFirst(";", ""));
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
