package com.upwork.dsavitski.snipplets.googlespreadsheet;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;


/**
 * Util class-example for working with google shpreadsheet api
 */
public class SheetSaver {
    private static final Logger logger = Logger.getLogger(SheetSaver.class);


    private static Sheets service;
    private static String spreadsheetId;
    private static Integer mainSheetId;

    /**
     * Main aggregation method for export document
     */
    public static void export() {
        try {
            service = SheetService.getSheetsService();

            final Spreadsheet spreadsheet = createNewSpreadsheet();
            spreadsheetId = spreadsheet.getSpreadsheetId();
            mainSheetId = spreadsheet.getSheets().get(0).getProperties().getSheetId();
            logger.info("Created spreadsheet id: " + spreadsheetId);

            writeTitles();

            writeData();

            logger.info("All data written ok!");
        } catch (IOException e) {
            String error = "Error with google API sheet service: ";
            logger.error(error + Arrays.toString(e.getStackTrace()), e);
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Creates new SpreadSheet
     */
    private static Spreadsheet createNewSpreadsheet() throws IOException {
        // creating new properties
        final SpreadsheetProperties spreadsheetProperties = new SpreadsheetProperties();
        // document title in google drive
        spreadsheetProperties.setTitle(Config.SPREAD_SHEET_NAME);
        // creating new spreadsheet and assigning properties
        final Spreadsheet spreadsheet = new Spreadsheet();
        spreadsheet.setProperties(spreadsheetProperties);

        //creating new sheet and add it to spreadsheet
        final Sheet sheet = createNewSheet();
        spreadsheet.setSheets(Collections.singletonList(sheet));

        // sending creating request
        return service.spreadsheets().create(spreadsheet).execute();
    }

    /**
     * Creates new sheet (one working list)
     */
    private static Sheet createNewSheet() {
        final Sheet sheet = new Sheet();
        sheet.setProperties(new SheetProperties()
                .setTitle(Config.SHEET_NAME)
                .setGridProperties(new GridProperties()
                        .setFrozenRowCount(1) /* freeze first column*/
                        .setRowCount(8000))); /* document rows*/
        return sheet;
    }

    /**
     * Writes titles to the first row
     */
    private static void writeTitles() throws IOException {
        String writeRange = Config.SHEET_NAME + "!A1";

        List<List<Object>> writeData = new ArrayList<>();
        List<Object> dataRow = new ArrayList<>();
        Collections.addAll(dataRow, Config.SHEET_TITLES.split(";"));
        writeData.add(dataRow);

        // writing first row data
        ValueRange vr = new ValueRange()
                .setValues(writeData)
                .setMajorDimension("ROWS");
        service.spreadsheets().values()
                .update(spreadsheetId, writeRange, vr)
                .setValueInputOption("RAW")
                .execute();
    }

    /**
     * Step by step creating data for db.
     */
    private static void writeData() throws IOException {
        List<Request> requests = new ArrayList<>();
        List<RowData> rowDatas = new ArrayList<>();

        formatRows(rowDatas);
        addUpdateRowsRequest(requests, rowDatas);
        addUpdateDimensionsRequest(requests);

        // sending batch update request
        BatchUpdateSpreadsheetRequest body =
                new BatchUpdateSpreadsheetRequest().setRequests(requests);
        service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
    }


    /**
     * Main sheet creating with formatted rows
     */
    private static void formatRows(List<RowData> rowDatas) {
        List<List<String>> data = getTestData();

        int line = 2;
        for (List<String> currentRow : data) {
            String row1 = currentRow.get(0);
            Double row2 = Double.valueOf(currentRow.get(1));
            Double row3 = Double.valueOf(currentRow.get(2));

            // setting random format for cells
            CellFormat cellFormat = getRandomCellFormat();
            CellFormat percentagedFormat = getPercent(cellFormat);

            // creating cell array
            List<CellData> cellDatas = new ArrayList<>();
            cellDatas.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(row1)).setUserEnteredFormat(cellFormat));
            cellDatas.add(new CellData().setUserEnteredValue(new ExtendedValue().setNumberValue(row2)).setUserEnteredFormat(cellFormat));
            cellDatas.add(new CellData().setUserEnteredValue(new ExtendedValue().setNumberValue(row3)).setUserEnteredFormat(cellFormat));
            // insert formula
            cellDatas.add(new CellData().setUserEnteredValue(new ExtendedValue().setFormulaValue("=B" + line + "/C" + line))
                    .setUserEnteredFormat(percentagedFormat));
            rowDatas.add(new RowData().setValues(cellDatas));
            line++;
        }
    }

    /**
     * Adds percentage to cell format
     */
    private static CellFormat getPercent(CellFormat cellFormat) {
        return cellFormat.clone().setNumberFormat(
                new NumberFormat().setType("PERCENT").setPattern("0.00%"));
    }

    /**
     * Request for adding main data
     */
    private static void addUpdateRowsRequest(List<Request> requests, List<RowData> rowDatas) {
        UpdateCellsRequest updateCellsRequest = new UpdateCellsRequest();
        updateCellsRequest.setRows(rowDatas);
        updateCellsRequest.setFields("*");
        updateCellsRequest.setRange(new GridRange().setSheetId(mainSheetId)
                .setStartRowIndex(1));
        requests.add(new Request().setUpdateCells(updateCellsRequest));
    }

    /**
     * Update column an row dimension request
     */
    private static void addUpdateDimensionsRequest(List<Request> requests) {
        UpdateDimensionPropertiesRequest updateDimensionPropertiesRequest = new UpdateDimensionPropertiesRequest();
        updateDimensionPropertiesRequest.setRange(new DimensionRange()
                .setSheetId(mainSheetId)
                .setDimension("COLUMNS")
                .setStartIndex(0)
                .setEndIndex(2)
        );
        updateDimensionPropertiesRequest.setFields("*");
        updateDimensionPropertiesRequest.setProperties(new DimensionProperties().setPixelSize(300));
        requests.add(new Request().setUpdateDimensionProperties(updateDimensionPropertiesRequest));
    }

    /**
     * Generates new colored cell format
     */
    private static CellFormat getRandomCellFormat() {
        Random random = new Random();
        return new CellFormat().setBackgroundColor(new Color().setRed(random.nextFloat() * 0.75f + 0.25f)
                .setGreen(random.nextFloat() * 0.75f + 0.25f)
                .setBlue(random.nextFloat() * 0.75f + 0.25f)
                .setAlpha(0.1f));
    }

    /**
     * Returns example of test data
     */
    private static List<List<String>> getTestData() {
        List<List<String>> data = new ArrayList<>();
        for (int i = 2; i < 10; i++) {
            List<String> row = new ArrayList<>();
            row.add("row_First_" + i);
            row.add(String.valueOf(i));
            row.add(String.valueOf(i * i));
            data.add(row);
        }
        return data;
    }
}
