package com.imambiplob.databasereport.service;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class CsvExportServiceForSingleColumnResult {
    public File exportQueryResultToCsv(List<Object> results, String filePath, Object[] columns) {

        File file = new File(filePath);

        try {

            FileWriter fileWriter = new FileWriter(filePath);
            CSVWriter csvWriter = CsvExportService.writeColumnNames(fileWriter, columns);

            /* Write the result rows */
            for (Object row : results) {

                String[] csvRow;
                csvRow = new String[]{(String) row};

                csvWriter.writeNext(csvRow);

            }

            fileWriter.flush();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return file;

    }
}
