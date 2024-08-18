package bulls.tool.util;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExcelFileWriter {
    public final String filename;
    private final Map<String, Sheet> sheetMap;
    private final Workbook wb;

    private ExcelFileWriter(String filename, Workbook wb) {
        this.filename = filename;
        this.sheetMap = new ConcurrentHashMap<>();
        this.wb = wb;

        // for (Sheet sheet : wb)로 iteration이 가능하지만 이렇게 하면 sheet.getSheetName()의 결과가 xml 경로가 된다
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            sheetMap.put(sheet.getSheetName(), sheet);
        }
    }

    public CellType getCellType(String sheetName, int rowIdx, int columnIdx) {
        Cell cell = getCell(sheetName, rowIdx, columnIdx);
        return cell.getCellType();
    }

    public String getString(String sheetName, int rowIdx, int columnIdx) {
        Cell cell = getCell(sheetName, rowIdx, columnIdx);
        if (cell.getCellType() == CellType.STRING)
            return cell.getStringCellValue();
        else if (cell.getCellType() == CellType.BLANK)
            return "";

        return null;
    }

    // integer, double, date
    public Double getNumeric(String sheetName, int rowIdx, int columnIdx) {
        Cell cell = getCell(sheetName, rowIdx, columnIdx);
        if (cell.getCellType() == CellType.NUMERIC)
            return cell.getNumericCellValue();
        else if (cell.getCellType() == CellType.BLANK)
            return 0.0;

        return null;
    }

    public LocalDateTime getDateTime(String sheetName, int rowIdx, int columnIdx) {
        Cell cell = getCell(sheetName, rowIdx, columnIdx);
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell))
            return cell.getLocalDateTimeCellValue();

        return null;
    }

    public void putString(String sheetName, int rowIdx, int columnIdx, String data) {
        Cell cell = getCell(sheetName, rowIdx, columnIdx);
        cell.setCellValue(data);
    }

    public void putInteger(String sheetName, int rowIdx, int columnIdx, int data) {
        Cell cell = getCell(sheetName, rowIdx, columnIdx);
        cell.setCellValue(data);
    }

    public void putDouble(String sheetName, int rowIdx, int columnIdx, double data) {
        putDouble(sheetName, rowIdx, columnIdx, data, -1);
    }

    public void putDouble(String sheetName, int rowIdx, int columnIdx, double data, int decimalDigits) {
        CellStyle style = null;
        if (decimalDigits >= 0) {
            String formatString;
            if (decimalDigits == 0)
                formatString = "0";
            else
                formatString = "0." + "0".repeat(decimalDigits);

            style = wb.createCellStyle();
            style.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat(formatString));
        }

        Cell cell = getCell(sheetName, rowIdx, columnIdx);
        cell.setCellValue(data);
        if (style != null)
            cell.setCellStyle(style);
    }

    public void putTime(String sheetName, int rowIdx, int columnIdx, LocalTime time, String formatString) {
        putDateTime(sheetName, rowIdx, columnIdx, LocalDateTime.of(LocalDate.now(), time), formatString);
    }

    public void putDate(String sheetName, int rowIdx, int columnIdx, LocalDate date, String formatString) {
        putDateTime(sheetName, rowIdx, columnIdx, LocalDateTime.of(date, LocalTime.MIN), formatString);
    }

    public void putDateTime(String sheetName, int rowIdx, int columnIdx, LocalDateTime dateTime, String formatString) {
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat(formatString));
        Cell cell = getCell(sheetName, rowIdx, columnIdx);
        cell.setCellValue(dateTime);
        cell.setCellStyle(style);
    }

    @NotNull
    private Cell getCell(String sheetName, int rowIdx, int columnIdx) {
        Sheet sheet = getSheet(sheetName);

        Row row = sheet.getRow(rowIdx);
        if (row == null)
            row = sheet.createRow(rowIdx);

        Cell cell = row.getCell(columnIdx);
        if (cell == null)
            cell = row.createCell(columnIdx);

        return cell;
    }

    @NotNull
    private Sheet getSheet(String sheetName) {
        return sheetMap.computeIfAbsent(sheetName, wb::createSheet);
    }

    public static ExcelFileWriter createNewFile(String filename) {
        try {
            File f = new File(filename);
            if (f.exists())
                f.delete();

            Workbook wb = WorkbookFactory.create(true);
            return new ExcelFileWriter(filename, wb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ExcelFileWriter load(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists())
                return null;

            ZipSecureFile.setMinInflateRatio(0);
            Workbook wb = WorkbookFactory.create(new FileInputStream(filename));
            return new ExcelFileWriter(filename, wb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean writeFile() {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            wb.write(fos);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
