package bulls.dmaLog.loader;

import bulls.designTemplate.GeneralFileReader;
import bulls.dmaLog.DMALogFileTypeChecker;

import java.io.*;
import java.util.LinkedList;

public class DMALogReader implements GeneralFileReader {

    private final String path;
    private final DMALogFileTypeChecker fileTypeChecker;

    private LinkedList<String> logFilePaths;
    private BufferedReader fileReader;

    public DMALogReader(String path, DMALogFileTypeChecker fileTypeChecker) {
        this.path = path;
        this.fileTypeChecker = fileTypeChecker;
    }

    @Override
    public boolean init() {
        File folder = new File(path);
        File[] logFileList = folder.listFiles((dir, name) -> fileTypeChecker.check(name));
        if (logFileList == null)
            return false;

        logFilePaths = new LinkedList<>();
        for (File f : logFileList)
            logFilePaths.add(f.getAbsolutePath());

        return true;
    }

    @Override
    public String next() {
        if (fileReader == null) {
            if (logFilePaths.size() == 0)
                return null;
            try {
                fileReader = new BufferedReader(new FileReader(logFilePaths.poll()));
            } catch (FileNotFoundException e) {
                return null;
            }
        }

        String line;

        try {
            line = fileReader.readLine();
        } catch (IOException e) {
            return null;
        }

        while (line == null) {
            try {
                fileReader.close();
            } catch (IOException e) {
                // Do nothing
            }

            if (logFilePaths.size() == 0)
                return null;
            try {
                fileReader = new BufferedReader(new FileReader(logFilePaths.poll()));
            } catch (FileNotFoundException e) {
                return null;
            }

            try {
                line = fileReader.readLine();
            } catch (IOException e) {
                return null;
            }
        }

        return line;
    }

    @Override
    public boolean close() {
        try {
            fileReader.close();
        } catch (IOException e) {
            return false;
        }

        fileReader = null;
        logFilePaths = null;

        return true;
    }
}
