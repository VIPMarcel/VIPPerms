package vip.marcel.vipperms.proxy.vipperms.utils.config;

import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.util.List;

public interface ConfigHandler {

    Configuration getConfiguration();

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
