package gx.lrd1122.OriginXTranslator;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OriginXTranslator {
    public static void main(String[] args) throws IOException {
        File inputDir = new File("Input");
        File queueDir = new File("Queue");
        File outputDir = new File("Output");
        if (!inputDir.exists()) inputDir.mkdir();
        if (!outputDir.exists()) outputDir.mkdir();
        if (!queueDir.exists()) queueDir.mkdir();
        File[] inputFiles = inputDir.listFiles();
        for (File file : inputFiles) {
            if (file.getName().endsWith(".yml")) {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                File queueFile = new File(queueDir, file.getName());
                boolean generate = !queueFile.exists();
                if (!queueFile.exists()) queueFile.createNewFile();
                if (generate) {
                    YamlConfiguration queueYaml = YamlConfiguration.loadConfiguration(queueFile);
                    List<String> keys = new ArrayList<>(yaml.getKeys(true));
                    for (int i = 0; i < keys.size(); i++) {
                        String info = file.getName() + " -> ";
                        String key = keys.get(i);
                        info += key + " -> ";
                        OriginXEnum type = OriginXUtils.getType(yaml, key);
                        String value;
                        if (type != null) {
                            switch (type) {
                                case String:
                                    value = yaml.getString(key);
                                    if(value.endsWith(".")
                                    || value.endsWith("!")
                                    || value.endsWith("?")) value +=  " ";
                                    queueYaml.set("Str;" + i, value);
                                    info += yaml.getString(key);
                                    break;
                                case Integer:
                                    queueYaml.set("Int;" + i, yaml.getInt(key));
                                    info += yaml.getInt(key);
                                    break;
                                case Double:
                                    queueYaml.set("Dou;" + i, yaml.getDouble(key));
                                    info += yaml.getDouble(key);
                                    break;
                                case ArrayList:
                                    queueYaml.set("Arr;" + i, yaml.getStringList(key).toString());
                                    info += yaml.getStringList(key);
                                    break;
                                case Boolean:
                                    queueYaml.set("Boo;" + i, yaml.getBoolean(key));
                                    info += yaml.getBoolean(key);
                                    break;
                                case Section:
                                    queueYaml.set("Sc;" + i, "1");
                                    info += yaml.getConfigurationSection(key).getName();
                                    break;
                                case Unknown:
                                    queueYaml.set("Unk;" + i, yaml.getString(key));
                                    info += yaml.getString(key);
                                    break;
                            }
                            System.out.println(info);
                        }
                    }
                    queueYaml.save(queueFile);
                    System.out.println("[Input]" + file.getName() + " 待翻译文件已存入Queue文件夹内");
                }
            }
        }
        for (File file : queueDir.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                File fix = new File(queueDir, file.getName().replace(".yml", "")
                        + ".temp");
                if (!fix.exists()) fix.createNewFile();
                if (file.getName().endsWith(".yml")) {
                    if (file.length() > 0) {
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
                        );
                        BufferedWriter bufferedWriter = new BufferedWriter(
                                new OutputStreamWriter(new FileOutputStream(fix), StandardCharsets.UTF_8)
                        );
                        String tempStr;
                        bufferedWriter.flush();
                        while ((tempStr = bufferedReader.readLine()) != null) {
                            bufferedWriter.write(tempStr + "\r\n");
                        }
                        bufferedReader.close();
                        bufferedWriter.close();
                    }
                    if (fix.length() > 0) {
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(new FileInputStream(fix), StandardCharsets.UTF_8)
                        );
                        BufferedWriter bufferedWriter = new BufferedWriter(
                                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)
                        );
                        String tempStr;
                        bufferedWriter.flush();
                        while ((tempStr = bufferedReader.readLine()) != null) {
                            String str = StringUtils.substringAfter(tempStr, ": '");
                            if (str.length() > 0) {
                                str = str.substring(0, str.length() - 1);
                                String temp = str;
                                str = str.replaceAll("'", "’");
                                str = str.replaceAll("\"", "“");
                                tempStr = tempStr.replace(temp, str);
                            }
                            tempStr = (tempStr.toString().replace("：", ": "));
                            bufferedWriter.write(tempStr + "\r\n");
                            System.out.println("[Queue]" + file.getName() + " -> " + tempStr);
                        }
                        bufferedReader.close();
                        bufferedWriter.close();
                        fix.delete();
                    }
                    YamlConfiguration queueYaml = YamlConfiguration.loadConfiguration(file);
                    YamlConfiguration inputYaml = YamlConfiguration.loadConfiguration(
                            new File(inputDir, file.getName())
                    );
                    File outputFile = new File(outputDir, file.getName());
                    if (!outputFile.exists()) outputFile.createNewFile();
                    YamlConfiguration outputYaml = YamlConfiguration.loadConfiguration(outputFile);
                    List<String> keys = new ArrayList<>(inputYaml.getKeys(true));
                    for (int i = 0; i < keys.size(); i++) {
                        String key = keys.get(i);
                        String queueKey = new ArrayList<>(queueYaml.getKeys(true)).get(i);
                        OriginXEnum type = OriginXUtils.getType(queueKey);
                        System.out.println("[Output]" + file.getName() + " -> " + key + ": " + queueYaml.get(queueKey));
                        if (type.equals(OriginXEnum.String)) {
                            outputYaml.set(key, queueYaml.getString(queueKey));
                        } else {
                            outputYaml.set(key, queueYaml.get(queueKey));
                        }
                    }
                    outputYaml.save(outputFile);
                }
            }
        }
    }
}
