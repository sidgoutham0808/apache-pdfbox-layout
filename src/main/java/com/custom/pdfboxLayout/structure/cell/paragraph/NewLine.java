package com.custom.pdfboxLayout.structure.cell.paragraph;

import com.custom.pdfboxLayout.settings.Settings;
import com.custom.pdfboxLayout.layout.elements.Paragraph;

public class NewLine implements ParagraphProcessable {

    Float fontSize;

    NewLine(float fontSize) {
        this.fontSize = fontSize;
    }

    NewLine() {
    }

    @Override
    public void process(Paragraph paragraph, Settings settings) {
        float actualFontSize = fontSize != null ? fontSize : settings.getFontSize();
        paragraph.add(new com.custom.pdfboxLayout.layout.text.NewLine(actualFontSize));
    }

}
