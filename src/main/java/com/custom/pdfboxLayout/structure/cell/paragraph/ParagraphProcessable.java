package com.custom.pdfboxLayout.structure.cell.paragraph;

import com.custom.pdfboxLayout.layout.elements.Paragraph;
import com.custom.pdfboxLayout.settings.Settings;

import java.io.IOException;

public interface ParagraphProcessable {

    void process(Paragraph paragraph, Settings settings) throws IOException;

}
