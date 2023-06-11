package com.custom.pdfboxLayout.structure.cell.paragraph;

import com.custom.pdfboxLayout.layout.text.FontDescriptor;
import com.custom.pdfboxLayout.layout.text.LayoutStyledText;
import com.custom.pdfboxLayout.layout.text.NewLine;
import com.custom.pdfboxLayout.util.PdfUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.pdfbox.pdmodel.font.PDFont;
import com.custom.pdfboxLayout.settings.Settings;
import com.custom.pdfboxLayout.layout.elements.Paragraph;

import java.awt.*;

@Builder
@Getter
public class StyledText implements ParagraphProcessable {

    @NonNull
    private String text;

    private Float fontSize;

    private PDFont font;

    private Color color;

    @Override
    public void process(Paragraph paragraph, Settings settings) {
        final float actualFontSize = getFontSize() != null ? getFontSize() : settings.getFontSize();
        final PDFont actualFont = getFont() != null ? getFont() : settings.getFont();
        final Color actualColor = getColor() != null ? getColor() : settings.getTextColor();

        // Handle new lines properly ...
        String[] lines = getText().split(PdfUtil.NEW_LINE_REGEX);
        for (int i = 0; i < lines.length; i++) {
            paragraph.add(new LayoutStyledText(lines[i], actualFontSize, actualFont, actualColor));
            if (i < lines.length - 1) {
                paragraph.add(new NewLine(new FontDescriptor(actualFont, actualFontSize)));
            }
        }

    }

}
