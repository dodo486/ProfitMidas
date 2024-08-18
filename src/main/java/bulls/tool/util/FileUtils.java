package bulls.tool.util;


import bulls.log.DefaultLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class FileUtils {


    public static InputStream openInputStream(String filePath) {
        try {
            InputStream i = new FileInputStream(filePath);
            return i;
        } catch (FileNotFoundException e) {
            DefaultLogger.logger.error("error found", e);
        }
        return null;
    }

    public static List<String> readFile(String filename) {
        List<String> lineList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;

                lineList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return lineList;
    }

}
