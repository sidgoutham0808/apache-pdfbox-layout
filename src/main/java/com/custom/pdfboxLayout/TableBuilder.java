package com.custom.pdfboxLayout;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.List;

public class TableBuilder {

    private Table1 table1 = new Table1();

    public TableBuilder setHeight(float height) {
        table1.setHeight(height);
        return this;
    }

    public TableBuilder setNumberOfRows(Integer numberOfRows) {
        table1.setNumberOfRows(numberOfRows);
        return this;
    }

    public TableBuilder setRowHeight(float rowHeight) {
        table1.setRowHeight(rowHeight);
        return this;
    }

    public TableBuilder setContent(String[][] content) {
        table1.setContent(content);
        return this;
    }

    public TableBuilder setColumns(List<Column> columns) {
        table1.setColumns(columns);
        return this;
    }

    public TableBuilder setCellMargin(float cellMargin) {
        table1.setCellMargin(cellMargin);
        return this;
    }

    public TableBuilder setMargin(float margin) {
        table1.setMargin(margin);
        return this;
    }

    public TableBuilder setPageSize(PDRectangle pageSize) {
        table1.setPageSize(pageSize);
        return this;
    }

    public TableBuilder setLandscape(boolean landscape) {
        table1.setLandscape(landscape);
        return this;
    }

    public TableBuilder setTextFont(PDFont textFont) {
        table1.setTextFont(textFont);
        return this;
    }

    public TableBuilder setFontSize(float fontSize) {
        table1.setFontSize(fontSize);
        return this;
    }

    public Table1 build() {
        return table1;
    }
}
