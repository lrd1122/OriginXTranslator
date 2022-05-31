package gx.lrd1122.OriginXTranslator.OriginXLoggerManager;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class OxLoggerManager {

    private File file;
    public OxLoggerManager(){}
    public OxLoggerManager initialize() throws IOException {
        File file = new File("log.txt");
        if(!file.exists()) file.createNewFile();
        this.file = file;
        return this;
    }
    public void queue(String str){
        try {
            String value = "[Queue] " + str;
            System.out.println(value);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8
            ));
            writer.write(value);
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void output(String str){
        try {
            String value = "[Output] " + str;
            System.out.println(value);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8
            ));
            writer.write(value);
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void error(String str){
        try {
            String value = "[Error] " + str;
            System.out.println(value);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8
            ));
            writer.write(value);
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
