package gx.lrd1122.OriginXTranslator;

import org.bukkit.configuration.file.YamlConfiguration;

public class OriginXUtils {
    public static OriginXEnum getType(YamlConfiguration config, String key) {
        return config.isString(key) ? OriginXEnum.String :
                config.isBoolean(key) ? OriginXEnum.Boolean :
                        config.isInt(key) ? OriginXEnum.Integer :
                                config.isDouble(key) ? OriginXEnum.Double :
                                        config.isList(key) ? OriginXEnum.ArrayList :
                                                config.isConfigurationSection(key) ? OriginXEnum.Section :
                                                OriginXEnum.Unknown;
    }
    public static OriginXEnum getType(String str) {
        if (str.startsWith("Str")) return OriginXEnum.String;
        if (str.startsWith("Boo")) return OriginXEnum.Boolean;
        if (str.startsWith("Int")) return OriginXEnum.Integer;
        if (str.startsWith("Dou")) return OriginXEnum.Double;
        if (str.startsWith("Arr")) return OriginXEnum.ArrayList;
        if (str.startsWith("Sc")) return OriginXEnum.Section;
        else return OriginXEnum.Unknown;
    }
}
