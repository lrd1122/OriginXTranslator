package gx.lrd1122.OriginXTranslator;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;

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

    public static void insertLine(File file, String str, int position) {
        try {
            File outFile = File.createTempFile(file.getName(), ".tmp");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8
            ));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outFile), StandardCharsets.UTF_8
            ));
            String thisLine;
            int i = 1;
            while ((thisLine = bufferedReader.readLine()) != null) {
                if (i == position) {
                    bufferedWriter.write(str + "\n");
                }
                bufferedWriter.write(thisLine + "\n");
                i++;
            }
            bufferedReader.close();
            bufferedWriter.close();
            file.delete();
            outFile.renameTo(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
