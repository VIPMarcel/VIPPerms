package vip.marcel.vipperms.spigot.vipperms.utils.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public interface ConfigHandler {

    YamlConfiguration getConfiguration();

    File getFile();

    void saveConfig();

    void reloadConfiguration();

    String getString(String common);

    Integer getInteger(String common);

    Double getDouble(String common);

    Float getFloat(String common);

    List<String> getStringList(String common);

    Boolean getBoolean(String common);

    Object getObject(String common, Class clazz);

    void set(String common, Object value);

}
