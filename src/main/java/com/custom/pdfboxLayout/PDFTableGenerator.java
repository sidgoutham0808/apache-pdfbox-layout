package com.custom.pdfboxLayout;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.Arrays;

public class PDFTableGenerator {

    // Generates document from Table object
    public void generatePDF(Table1 table1) throws IOException {
        PDDocument doc = null;
        try {
            doc = new PDDocument();
            drawTable(doc, table1);
            doc.save("sample.pdf");
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
    }

    // Configures basic setup for the table and draws it page by page
    public void drawTable(PDDocument doc, Table1 table1) throws IOException {
        // Calculate pagination
        Integer rowsPerPage = Integer.valueOf((int) Math.floor(table1.getHeight() / table1.getRowHeight())) - 1; // subtract
        Integer numberOfPages = Integer.valueOf((int) Math.ceil(table1.getNumberOfRows().floatValue() / rowsPerPage));

        // Generate each page, get the content and draw it
        for (int pageCount = 0; pageCount < numberOfPages; pageCount++) {
            PDPage page = generatePage(doc, table1);
            PDPageContentStream contentStream = generateContentStream(doc, page, table1);
            String[][] currentPageContent = getContentForCurrentPage(table1, rowsPerPage, pageCount);
            drawCurrentPage(table1, currentPageContent, contentStream);
        }
    }

    // Draws current page table grid and border lines and content
    private void drawCurrentPage(Table1 table1, String[][] currentPageContent, PDPageContentStream contentStream)
            throws IOException {
        float tableTopY = table1.isLandscape() ? table1.getPageSize().getWidth() - table1.getMargin() : table1.getPageSize().getHeight() - table1.getMargin();

        // Draws grid and borders
        drawTableGrid(table1, currentPageContent, contentStream, tableTopY);

        // Position cursor to start drawing content
        float nextTextX = table1.getMargin() + table1.getCellMargin();
        // Calculate center alignment for text in cell considering font height
        float nextTextY = tableTopY - (table1.getRowHeight() / 2)
                - ((table1.getTextFont().getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * table1.getFontSize()) / 4);

        // Write column headers
        writeContentLine(table1.getColumnsNamesAsArray(), contentStream, nextTextX, nextTextY, table1);
        nextTextY -= table1.getRowHeight();
        nextTextX = table1.getMargin() + table1.getCellMargin();

        // Write content
        for (int i = 0; i < currentPageContent.length; i++) {
            writeContentLine(currentPageContent[i], contentStream, nextTextX, nextTextY, table1);
            nextTextY -= table1.getRowHeight();
            nextTextX = table1.getMargin() + table1.getCellMargin();
        }

        contentStream.close();
    }

    // Writes the content for one line
    private void writeContentLine(String[] lineContent, PDPageContentStream contentStream, float nextTextX, float nextTextY,
            Table1 table1) throws IOException {
        for (int i = 0; i < table1.getNumberOfColumns(); i++) {
            String text = lineContent[i];
            contentStream.beginText();
            contentStream.moveTextPositionByAmount(nextTextX, nextTextY);
            contentStream.drawString(text != null ? text : "");
            contentStream.endText();
            nextTextX += table1.getColumns().get(i).getWidth();
        }
    }

    private void drawTableGrid(Table1 table1, String[][] currentPageContent, PDPageContentStream contentStream, float tableTopY)
            throws IOException {
        // Draw row lines
        float nextY = tableTopY;
        for (int i = 0; i <= currentPageContent.length + 1; i++) {
            contentStream.drawLine(table1.getMargin(), nextY, table1.getMargin() + table1.getWidth(), nextY);
            nextY -= table1.getRowHeight();
        }

        // Draw column lines
        final float tableYLength = table1.getRowHeight() + (table1.getRowHeight() * currentPageContent.length);
        final float tableBottomY = tableTopY - tableYLength;
        float nextX = table1.getMargin();
        for (int i = 0; i < table1.getNumberOfColumns(); i++) {
            contentStream.drawLine(nextX, tableTopY, nextX, tableBottomY);
            nextX += table1.getColumns().get(i).getWidth();
        }
        contentStream.drawLine(nextX, tableTopY, nextX, tableBottomY);
    }

    private String[][] getContentForCurrentPage(Table1 table1, Integer rowsPerPage, int pageCount) {
        int startRange = pageCount * rowsPerPage;
        int endRange = (pageCount * rowsPerPage) + rowsPerPage;
        if (endRange > table1.getNumberOfRows()) {
            endRange = table1.getNumberOfRows();
        }
        return Arrays.copyOfRange(table1.getContent(), startRange, endRange);
    }

    private PDPage generatePage(PDDocument doc, Table1 table1) {
        PDPage page = new PDPage();
        page.setMediaBox(table1.getPageSize());
        page.setRotation(table1.isLandscape() ? 90 : 0);
        doc.addPage(page);
        return page;
    }

    private PDPageContentStream generateContentStream(PDDocument doc, PDPage page, Table1 table1) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(doc, page, false, false);
        // User transformation matrix to change the reference when drawing.
        // This is necessary for the landscape position to draw correctly
        if (table1.isLandscape()) {
            contentStream.concatenate2CTM(0, 1, -1, 0, table1.getPageSize().getWidth(), 0);
        }
        contentStream.setFont(table1.getTextFont(), table1.getFontSize());
        return contentStream;
    }
}
