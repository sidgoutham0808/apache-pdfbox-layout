
package com.custom.pdfboxLayout;

import com.custom.pdfboxLayout.settings.HorizontalAlignment;
import com.custom.pdfboxLayout.settings.VerticalAlignment;
import com.custom.pdfboxLayout.structure.Row;
import com.custom.pdfboxLayout.structure.Table;
import com.custom.pdfboxLayout.structure.cell.TextCell;

import java.awt.*;
import java.io.IOException;

import static org.apache.pdfbox.pdmodel.font.PDType1Font.*;

public class CustomPdfGen {


    private final static Color BLUE_DARK = new Color(76, 129, 190);
    private final static Color BLUE_LIGHT_1 = new Color(186, 206, 230);
    private final static Color BLUE_LIGHT_2 = new Color(218, 230, 242);

    private final static Color GRAY_LIGHT_1 = new Color(245, 245, 245);
    private final static Color GRAY_LIGHT_2 = new Color(240, 240, 240);
    private final static Color GRAY_LIGHT_3 = new Color(216, 216, 216);

    private static final String FILE_NAME = "testTable.pdf";

    private final static Object[][] DATA = new Object[][]{
            {"Whiskyddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddds", 134.0, 145.0},
            {"Beerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr",   768.0, 677.0},
            {"Gin",    456.2, 612.0},
            {"Vodka",  302.3, 467.0}
    };

    public static void main(String[] args) throws IOException {
        createDocumentWithExcelLikeTables();
    }
    public static void createDocumentWithExcelLikeTables() throws IOException {
        GenUtils.createAndSaveDocumentWithTables(FILE_NAME,
                createSimpleExampleTable()
        );

//        TestUtils.getExpectedPdfFor(FILE_NAME);
//        TestUtils.getActualPdfFor(FILE_NAME);
    }

    private static Table createSimpleExampleTable() {

        final Table.TableBuilder tableBuilder = Table.builder()
                .addColumnsOfWidth(200, 50, 50, 50)
                .fontSize(8)
                .font(HELVETICA)
                .borderColor(Color.WHITE);

        // Add the header row ...
        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder().text("Product").horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1).build())
                .add(TextCell.builder().text("2018").borderWidth(1).build())
                .add(TextCell.builder().text("2019").borderWidth(1).build())
                .add(TextCell.builder().text("Total").borderWidth(1).build())
                .backgroundColor(BLUE_DARK)
                .textColor(Color.WHITE)
                .font(HELVETICA_BOLD)
                .fontSize(9)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .build());

        // ... and some data rows
        double grandTotal = 0;
        for (int i = 0; i < DATA.length; i++) {
            final Object[] dataRow = DATA[i];
            final double total = (double) dataRow[1] + (double) dataRow[2];
            grandTotal += total;

            tableBuilder.addRow(Row.builder()
                    .add(TextCell.builder().text(String.valueOf(dataRow[0])).horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1).build())
                    .add(TextCell.builder().text(dataRow[1] + " €").borderWidth(1).build())
                    .add(TextCell.builder().text(dataRow[2] + " €").borderWidth(1).build())
                    .add(TextCell.builder().text(total + " €").borderWidth(1).build())
                    .backgroundColor(i % 2 == 0 ? BLUE_LIGHT_2 : BLUE_LIGHT_2)
                    .horizontalAlignment(HorizontalAlignment.RIGHT)
                    .build());
        }

        // Add a final row

        tableBuilder.addRow(Row.builder()
                .add(TextCell.builder().text("This spans over 3 cells, is right aligned and its text is so long that it even breaks. " +
                        "Also it shows the grand total in the next cell and furthermore vertical alignment is shown:")
                        .colSpan(3)
                        .lineSpacing(1f)
                        .borderWidthTop(1)
                        .textColor(Color.WHITE)
                        .backgroundColor(BLUE_DARK)
                        .fontSize(6)
                        .font(HELVETICA_OBLIQUE)
                        .borderWidth(1)
                        .build())
                .add(TextCell.builder().text(grandTotal + " €").backgroundColor(Color.LIGHT_GRAY)
                        .font(HELVETICA_BOLD_OBLIQUE)
                        .verticalAlignment(VerticalAlignment.TOP)
                        .borderWidth(1)
                        .build())
                .horizontalAlignment(HorizontalAlignment.RIGHT)
                .build());


        return tableBuilder.build();
    }

}

