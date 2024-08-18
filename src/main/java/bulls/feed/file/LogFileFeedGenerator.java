package bulls.feed.file;

import bulls.designTemplate.GeneralFileReader;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LogFileFeedGenerator implements GeneralFileReader {
    private final File feedFile;
    BufferedReader feedBR;
    InputStreamReader feedIS;

    public LogFileFeedGenerator(String feedFilePath) {
        this.feedFile = new File(feedFilePath);
    }

    @Override
    public boolean init() {
        try {
            if (feedFile.getName().endsWith("zip")) {
                ZipInputStream zis = new ZipInputStream(new FileInputStream(feedFile));
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    String extractedFileName = FilenameUtils.getName(entry.getName());
                    String zipFileName = FilenameUtils.getName(feedFile.getName());
                    if (zipFileName.startsWith(extractedFileName)) {
                        feedIS = new InputStreamReader(new BufferedInputStream(zis, 1000000000), "EUC-KR");
                        break;
                    }
                }
            } else
                feedIS = new InputStreamReader(new FileInputStream(feedFile), "EUC-KR");
            feedBR = new BufferedReader(feedIS);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean close() {
        try {
            feedBR.close();
            feedIS.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public String next() {
        try {
            if (feedBR.ready())
                return feedBR.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

