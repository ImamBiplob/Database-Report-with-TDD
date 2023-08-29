package com.imambiplob.databasereport.service;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class CsvExportService {

    public void exportQueryResultToCsv(List<Object[]> results, String filePath, Object[] columns) {

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath))) {
            if (columns != null) {
                String[] columnNames = new String[columns.length];
                for (int i = 0; i < columns.length; i++) {
                    columnNames[i] = String.valueOf(columns[i]);  // Writing column names
                }
                csvWriter.writeNext(columnNames);
            }

            // Write the result rows
            for (Object[] row : results) {
                String[] csvRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    csvRow[i] = String.valueOf(row[i]);
                }
                csvWriter.writeNext(csvRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}