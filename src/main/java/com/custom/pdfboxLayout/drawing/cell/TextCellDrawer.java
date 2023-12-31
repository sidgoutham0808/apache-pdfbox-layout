package com.custom.pdfboxLayout.drawing.cell;

import com.custom.pdfboxLayout.drawing.DrawingContext;
import com.custom.pdfboxLayout.drawing.DrawingUtil;
import com.custom.pdfboxLayout.drawing.PositionedStyledText;
import com.custom.pdfboxLayout.structure.cell.AbstractTextCell;
import com.custom.pdfboxLayout.util.PdfUtil;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.custom.pdfboxLayout.settings.HorizontalAlignment.*;

@NoArgsConstructor
public class TextCellDrawer<T extends AbstractTextCell> extends AbstractCellDrawer<AbstractTextCell> {

    public TextCellDrawer(T cell) {
        this.cell = cell;
    }

    @Override
    public void drawContent(DrawingContext drawingContext) throws IOException {
        final float startX = drawingContext.getStartingPoint().x;

        final PDFont currentFont = cell.getFont();
        final int currentFontSize = cell.getFontSize();
        final Color currentTextColor = cell.getTextColor();

        float yOffset = drawingContext.getStartingPoint().y + getAdaptionForVerticalAlignment();
        float xOffset = startX + cell.getPaddingLeft();

        final List<String> lines = calculateAndGetLines(currentFont, currentFontSize, cell.getMaxWidth());
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);

            yOffset -= calculateYOffset(currentFont, currentFontSize, i);

            final float textWidth = PdfUtil.getStringWidth(line, currentFont, currentFontSize);

            // Handle horizontal alignment by adjusting the xOffset
            if (cell.isHorizontallyAligned(RIGHT)) {
                xOffset = startX + (cell.getWidth() - (textWidth + cell.getPaddingRight()));

            } else if (cell.isHorizontallyAligned(CENTER)) {
                xOffset = startX + (cell.getWidth() - textWidth) / 2;

            } else if (cell.isHorizontallyAligned(JUSTIFY) && isNotLastLine(lines, i)) {
                drawingContext.getContentStream().setCharacterSpacing(calculateCharSpacingFor(line));
            }

            drawText(
                    drawingContext,
                    PositionedStyledText.builder()
                            .x(xOffset)
                            .y(yOffset)
                            .text(line)
                            .font(currentFont)
                            .fontSize(currentFontSize)
                            .color(currentTextColor)
                            .build()
            );
        }
    }

    @Override
    protected float calculateInnerHeight() {
        return cell.getTextHeight();
    }


    private float calculateYOffset(PDFont currentFont, int currentFontSize, int lineIndex) {
        return PdfUtil.getFontHeight(currentFont, currentFontSize) // font height
                + (lineIndex > 0 ? PdfUtil.getFontHeight(currentFont, currentFontSize) * cell.getLineSpacing() : 0f); // line spacing
    }

    static boolean isNotLastLine(List<String> lines, int i) {
        return i != lines.size() - 1;
    }

    // Code from https://stackoverflow.com/questions/20680430/is-it-possible-to-justify-text-in-pdfbox
    protected float calculateCharSpacingFor(String line) {
        float charSpacing = 0;
        if (line.length() > 1) {
            float size = PdfUtil.getStringWidth(line, cell.getFont(), cell.getFontSize());
            float free = cell.getWidthOfText() - size;
            if (free > 0) {
                charSpacing = free / (line.length() - 1);
            }
        }
        return charSpacing;
    }

    protected List<String> calculateAndGetLines(PDFont currentFont, int currentFontSize, float maxWidth) {
        return cell.isWordBreak()
                ? PdfUtil.getOptimalTextBreakLines(cell.getText(), currentFont, currentFontSize, maxWidth)
                : Collections.singletonList(cell.getText());
    }

    protected void drawText(DrawingContext drawingContext, PositionedStyledText positionedStyledText) throws IOException {
        DrawingUtil.drawText(
                drawingContext.getContentStream(),
                positionedStyledText
        );
    }

}
