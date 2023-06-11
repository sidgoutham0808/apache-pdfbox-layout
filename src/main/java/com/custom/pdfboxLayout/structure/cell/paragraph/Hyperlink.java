package com.custom.pdfboxLayout.structure.cell.paragraph;

import com.custom.pdfboxLayout.layout.elements.Paragraph;
import com.custom.pdfboxLayout.layout.text.annotations.AnnotatedLayoutStyledText;
import com.custom.pdfboxLayout.layout.text.annotations.Annotations;
import com.custom.pdfboxLayout.settings.Settings;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.util.Collections;

@Builder
@Getter
public class Hyperlink implements ParagraphProcessable {

    @NonNull
    private String text;

    @NonNull
    private String url;

    private PDFont font;

    private Float fontSize;

    @Builder.Default
    private Color color = Color.BLUE;

    @Builder.Default
    private float baselineOffset = 1f;

    @Override
    public void process(Paragraph paragraph, Settings settings) {
        Annotations.HyperlinkAnnotation hyperlink =
                new Annotations.HyperlinkAnnotation(
                        getUrl(),
                        Annotations.HyperlinkAnnotation.LinkStyle.ul
                );

        paragraph.add(
                new AnnotatedLayoutStyledText(
                        getText(),
                        getFontSize() != null ? getFontSize() : settings.getFontSize(),
                        getFont() != null ? getFont() : settings.getFont(),
                        getColor(),
                        getBaselineOffset(),
                        Collections.singleton(hyperlink)
                )
        );
    }

}