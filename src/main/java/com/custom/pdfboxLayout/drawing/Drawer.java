package com.custom.pdfboxLayout.drawing;

import java.io.IOException;

public interface Drawer {

    void drawContent(DrawingContext drawingContext) throws IOException;

    void drawBackground(DrawingContext drawingContext) throws IOException;

    void drawBorders(DrawingContext drawingContext);

}
