package bulls.dmaLog.loader;

import bulls.designTemplate.GeneralFileReader;
import bulls.dmaLog.DMALogFileTypeChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DMAZipReader implements GeneralFileReader {

    private final String zipFileName;
    private final DMALogFileTypeChecker fileTypeChecker;

    private ZipFile zipFile;
    private LinkedList<ZipEntry> zipEntryList;
    private BufferedReader fileReader;

    public DMAZipReader(String zipFileName, DMALogFileTypeChecker fileTypeChecker) {
        this.zipFileName = zipFileName;
        this.fileTypeChecker = fileTypeChecker;
    }

    @Override
    public boolean init() {
        try {
            zipFile = new ZipFile(zipFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        zipEntryList = new LinkedList<>();
        zipFile.stream().filter(entry -> fileTypeChecker.check(entry.getName())).forEach(zipEntryList::add);

        return true;
    }

    @Override
    public String next() {
        if (fileReader == null) {
            if (zipEntryList.size() == 0)
                return null;
            try {
                fileReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntryList.poll())));
            } catch (IOException e) {
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

            if (zipEntryList.size() == 0)
                return null;
            try {
                fileReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntryList.poll())));
            } catch (IOException e) {
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
        zipEntryList = null;

        return true;
    }
}

