package gx.lrd1122.OriginXTranslator;

import gx.lrd1122.OriginXTranslator.OriginXConfigManager.OxConfigManager;
import gx.lrd1122.OriginXTranslator.OriginXConfigManager.OxMainConfig;
import gx.lrd1122.OriginXTranslator.OriginXLoggerManager.OxLoggerManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class OriginXTranslator {
    public static OxLoggerManager logger;
    public static void main(String[] args) throws IOException {
        File inputDir = new File("Input");
        File queueDir = new File("Queue");
        File outputDir = new File("Output");
        File configFile = new File("config.yml");
        HashMap<String, HashMap<Integer, String>> annotationFileMap = new HashMap<>();
        logger = new OxLoggerManager().initialize();
        if(!configFile.exists()) configFile.createNewFile();
        OxMainConfig.initialize();
        HashMap<Integer, String> annotationStrs = new HashMap<>();
        if (!inputDir.exists()) inputDir.mkdir();
        if (!outputDir.exists()) outputDir.mkdir();
        if (!queueDir.exists()) queueDir.mkdir();
        File[] inputFiles = inputDir.listFiles();
        for (File file : inputFiles) {
            if (file.getName().endsWith(".yml")) {
                annotationStrs = new HashMap<>();
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                File queueFile = new File(queueDir, file.getName());
                boolean generate = !queueFile.exists();
                if (!queueFile.exists()) queueFile.createNewFile();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
                );
                String str;
                int line = 1;
                while ((str = bufferedReader.readLine()) != null){
                    if(str.replace(" ", "").startsWith("#")){
                        annotationStrs.put(line, str);
                        line++;
                    }
                }
                annotationFileMap.put(file.getName(), annotationStrs);
                if (generate) {
                    YamlConfiguration queueYaml = YamlConfiguration.loadConfiguration(queueFile);
                    List<String> keys = new ArrayList<>(yaml.getKeys(true));
                    for (int i = 0; i < keys.size(); i++) {
                        String info = file.getName() + " -> ";
                        String key = keys.get(i);
                        info += key + " -> ";
                        OriginXEnum type = OriginXUtils.getType(yaml, key);
                        String value;
                        String keySet = "D";
                        if (type != null) {
                            switch (type) {
                                case String:
                                    value = yaml.getString(key);
                                    if (value.endsWith(".")
                                            || value.endsWith("!")
                                            || value.endsWith("?")) value += " ";
                                    queueYaml.set(keySet + ";" + i, value);
                                    info += yaml.getString(key);
                                    break;
                                case Integer:
                                case Double:
                                    queueYaml.set(keySet + ";"  + i, yaml.get(key));
                                    info += yaml.get(key);
                                    break;
                                case ArrayList:
                                    queueYaml.set(keySet + ";"  + i, yaml.getStringList(key));
                                    info += yaml.getStringList(key);
                                    break;
                                case Boolean:
                                    queueYaml.set(keySet + ";"  + i, "1");
                                    info += yaml.getBoolean(key);
                                    break;
                                case Section:
                                    queueYaml.set(keySet + ";"  + i, "1");
                                    info += yaml.getConfigurationSection(key).getName();
                                    break;
                                case Unknown:
                                    queueYaml.set(keySet + ";"  + i, yaml.getString(key));
                                    info += yaml.getString(key);
                                    break;
                            }
                            System.out.println(info);
                        }
                    }
                    queueYaml.set("F", new ArrayList<>(annotationStrs.values()));
                    queueYaml.save(queueFile);
                    System.out.println("[Input]" + file.getName() + " 待翻译文件已存入Queue文件夹内");
                }
            }
        }
        for (File file : queueDir.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                if(file.length() < 1) { file.delete(); continue;}
                File fix = File.createTempFile(file.getName(), "temp");
                if (!fix.exists()) fix.createNewFile();
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
                        String keystr = StringUtils.substringBefore(tempStr, ": '");
                        if(keystr.length() > 0){
                            String temp = keystr;
                            keystr = keystr.replace("；", ";");
                            tempStr = tempStr.replace(temp, keystr);
                        }
                        if(tempStr.contains(":") && !tempStr.contains(": ")){
                            tempStr = tempStr.replaceFirst(":", ": ");
                        }
                        tempStr = tempStr.replace("：", ": ");
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
                File inputFile = new File(inputDir, file.getName());
                File outputFile = new File(outputDir, file.getName());
                if (!outputFile.exists()) outputFile.createNewFile();
                else outputFile.delete();
                outputFile.createNewFile();
                YamlConfiguration outputYaml = YamlConfiguration.loadConfiguration(outputFile);
                List<String> keys = new ArrayList<>(inputYaml.getKeys(true));
                for (int i = 0; i < keys.size(); i++) {
                    String key = keys.get(i);
                    String queueKey = new ArrayList<>(queueYaml.getKeys(true)).get(i);
                    OriginXEnum type = OriginXUtils.getType(queueYaml, queueKey);
                    System.out.println("[Output]" + file.getName() + " -> " + key + ": " + queueYaml.get(queueKey));
                    if (OxConfigManager.isWhiteList && !queueKey.equals("F")) {
                        if (!OxConfigManager.wholeWhiteList.contains(key)
                                && !OxConfigManager.endWhiteList.contains(
                                        key.split("\\.")[key.split("\\.").length - 1])) {
                            if (type.equals(OriginXEnum.String)) {
                                outputYaml.set(key, inputYaml.getString(key));
                            } else if (type.equals(OriginXEnum.ArrayList)) {
                                outputYaml.set(key, inputYaml.getStringList(key));
                            } else {
                                outputYaml.set(key, inputYaml.get(key));
                            }
                        } else {
                            if (type.equals(OriginXEnum.String)) {
                                outputYaml.set(key, queueYaml.getString(queueKey));
                            } else if (type.equals(OriginXEnum.ArrayList)) {
                                outputYaml.set(key, queueYaml.getStringList(queueKey));
                            } else {
                                outputYaml.set(key, queueYaml.get(queueKey));
                            }
                        }
                    } else if (OxConfigManager.isBlackList && !queueKey.equals("F")) {
                        if (OxConfigManager.wholeBlackList.contains(key)
                                || OxConfigManager.endBlackList.contains(
                                        key.split("\\.")[key.split("\\.").length - 1])) {
                            if (type.equals(OriginXEnum.String)) {
                                outputYaml.set(key, inputYaml.getString(key));
                            } else if (type.equals(OriginXEnum.ArrayList)) {
                                outputYaml.set(key, inputYaml.getStringList(key));
                            } else {
                                outputYaml.set(key, inputYaml.get(key));
                            }
                        } else {
                            if (type.equals(OriginXEnum.String)) {
                                outputYaml.set(key, queueYaml.getString(queueKey));
                            } else if (type.equals(OriginXEnum.ArrayList)) {
                                outputYaml.set(key, queueYaml.getStringList(queueKey));
                            } else {
                                outputYaml.set(key, queueYaml.get(queueKey));
                            }
                        }
                    } else {
                        if (type.equals(OriginXEnum.String)) {
                            outputYaml.set(key, queueYaml.getString(queueKey));
                        } else if (type.equals(OriginXEnum.ArrayList)) {
                            outputYaml.set(key, queueYaml.getStringList(queueKey));
                        } else {
                            outputYaml.set(key, queueYaml.get(queueKey));
                        }
                    }
                }
                outputYaml.save(outputFile);
                List<String> annotations = queueYaml.getStringList("F");
                List<Integer> annotationPos = new ArrayList<>(
                        annotationFileMap.get(file.getName()).keySet());
                for (int i = 0; i < annotationPos.size(); i++) {
                    int pos = annotationPos.get(i);
                    try {
                        OriginXUtils.insertLine(outputFile, annotations.get(i), pos);
                    }catch (Exception e){
                        logger.error("创建注释时出错!");
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("已完成! 按下 Enter 键关闭!");
        System.in.read();
    }
}
