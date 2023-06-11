package com.custom.pdfboxLayout.structure.cell.paragraph;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import com.custom.pdfboxLayout.settings.Settings;
import com.custom.pdfboxLayout.layout.elements.Paragraph;
import com.custom.pdfboxLayout.layout.text.BaseFont;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

@Builder
@Getter
public class Markup implements ParagraphProcessable {

    public enum MarkupSupportedFont {
        TIMES, COURIER, HELVETICA
    }

    public static final Map<MarkupSupportedFont, BaseFont> FONT_MAP = new EnumMap<>(MarkupSupportedFont.class);
    static {
        FONT_MAP.put(MarkupSupportedFont.HELVETICA, BaseFont.Helvetica);
        FONT_MAP.put(MarkupSupportedFont.COURIER, BaseFont.Courier);
        FONT_MAP.put(MarkupSupportedFont.TIMES, BaseFont.Times);
    }

    @NonNull
    private String markup;

    @NonNull
    private MarkupSupportedFont font;

    private Float fontSize;

    @Override
    public void process(Paragraph paragraph, Settings settings) throws IOException {
        final Float fontSize = getFontSize() != null ? getFontSize() : settings.getFontSize();
        paragraph.addMarkup(getMarkup(), fontSize, FONT_MAP.get(getFont()));
    }

}
