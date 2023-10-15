package com.imambiplob.databasereport.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class PdfService {
    public void writeRunResultToPDF(List<Object[]> results, Object[] columns, String filePath, String title) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float tableWidth = 500;
            float y = 700; // Initialize the initial y-coordinate here

            // Write title above the line
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(100, y + 10);
            contentStream.showText(title);
            contentStream.endText();

            // Draw line above the table
            contentStream.moveTo(50, y);
            contentStream.lineTo(50 + tableWidth, y);
            contentStream.stroke();

            // Move y to draw the table
            y -= 25;

            // Write columns as the first row in bold
            y = writeRow(contentStream, columns, true, y);

            // Write results
            for (Object[] row : results) {
                y = writeRow(contentStream, row, false, y);
            }

            contentStream.close(); // Close the content stream
            document.save(new File(filePath));
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeRunResultToPDFForSingleColumn(List<Object> results, Object[] columns, String filePath, String title) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float tableWidth = 500;
            float y = 700; // Initialize the initial y-coordinate here

            // Write title above the line
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(100, y + 10);
            contentStream.showText(title);
            contentStream.endText();

            // Draw line above the table
            contentStream.moveTo(50, y);
            contentStream.lineTo(50 + tableWidth, y);
            contentStream.stroke();

            // Move y to draw the table
            y -= 25;

            // Write columns as the first row in bold
            y = writeRow(contentStream, columns, true, y);

            // Write results
            for (Object row : results) {
                y = writeRowForSingleColumn(contentStream, row, false, y);
            }

            contentStream.close(); // Close the content stream
            document.save(new File(filePath));
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float writeRow(PDPageContentStream contentStream, Object[] row, boolean isBold, float y) throws IOException {
        float startY = y;
        for (int i = 0; i < row.length; i++) {
            if (isBold) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            } else {
                contentStream.setFont(PDType1Font.HELVETICA, 12);
            }
            contentStream.beginText();
            contentStream.newLineAtOffset((i * 100) + 50, y);
            contentStream.showText(row[i].toString());
            contentStream.endText();
        }
        return startY - 20;
    }

    private float writeRowForSingleColumn(PDPageContentStream contentStream, Object row, boolean isBold, float y) throws IOException {
        float startY = y;

        if (isBold) {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        } else {
            contentStream.setFont(PDType1Font.HELVETICA, 12);
        }
        contentStream.beginText();
        contentStream.newLineAtOffset((0 * 100) + 50, y);
        contentStream.showText(row.toString());
        contentStream.endText();

        return startY - 20;
    }
}
