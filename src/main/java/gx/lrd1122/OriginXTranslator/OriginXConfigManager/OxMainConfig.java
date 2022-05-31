package gx.lrd1122.OriginXTranslator.OriginXConfigManager;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class OxMainConfig {
    public static File initialize() throws IOException {
        File file = new File("config.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        addDefault(yaml, "BlackListOptions.Enable", false);
        addDefault(yaml, "BlackListOptions.endBlackList", new ArrayList<>());
        addDefault(yaml, "BlackListOptions.wholeBlackList", new ArrayList<>());
        addDefault(yaml, "WhiteListOptions.Enable", false);
        addDefault(yaml, "WhiteListOptions.endWhiteList", new ArrayList<>());
        addDefault(yaml, "WhiteListOptions.wholeWhiteList", new ArrayList<>());
        OxConfigManager.isBlackList = yaml.getBoolean("BlackListOptions.Enable");
        OxConfigManager.isWhiteList = yaml.getBoolean("WhiteListOptions.Enable");
        OxConfigManager.endBlackList = yaml.getStringList("BlackListOptions.endBlackList");
        OxConfigManager.endWhiteList = yaml.getStringList("WhiteListOptions.endWhiteList");
        OxConfigManager.wholeBlackList = yaml.getStringList("BlackListOptions.wholeBlackList");
        OxConfigManager.wholeWhiteList = yaml.getStringList("BlackListOptions.wholeWhiteList");
        yaml.save(file);
        return file;
    }
    private static void addDefault(YamlConfiguration yaml, String key, Object value){
        if(yaml.get(key) == null) yaml.set(key, value);
    }
}
