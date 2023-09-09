package com.imambiplob.databasereport.service;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class CsvExportService {

    public File exportQueryResultToCsv(List<Object[]> results, String filePath, Object[] columns) {

        File file = new File(filePath);

        try {

            FileWriter fileWriter = new FileWriter(filePath);
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            if (columns != null) {

                String[] columnNames = new String[columns.length];

                for (int i = 0; i < columns.length; i++) {
                    columnNames[i] = String.valueOf(columns[i]);  /* Writing column names */
                }

                csvWriter.writeNext(columnNames);

            }

            /* Write the result rows */
            for (Object[] row : results) {

                String[] csvRow = new String[row.length];

                for (int i = 0; i < row.length; i++) {
                    csvRow[i] = String.valueOf(row[i]);
                }

                csvWriter.writeNext(csvRow);

            }

            fileWriter.flush();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return file;

    }

}