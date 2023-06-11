package com.custom.pdfboxLayout.drawing.cell;

import com.custom.pdfboxLayout.drawing.DrawingContext;
import com.custom.pdfboxLayout.layout.elements.Paragraph;
import com.custom.pdfboxLayout.layout.text.Alignment;
import com.custom.pdfboxLayout.layout.text.DrawContext;
import com.custom.pdfboxLayout.layout.text.Position;
import com.custom.pdfboxLayout.layout.text.annotations.AnnotationDrawListener;
import com.custom.pdfboxLayout.settings.HorizontalAlignment;
import com.custom.pdfboxLayout.structure.cell.paragraph.ParagraphCell;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import ca.on.gov.onbis.ckorf.pdfboxUtil.layout.text.*;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class ParagraphCellDrawer extends AbstractCellDrawer<ParagraphCell> {

    private static final Map<HorizontalAlignment, Alignment> ALIGNMENT_MAP = new EnumMap<>(HorizontalAlignment.class);
    static {
        ALIGNMENT_MAP.put(HorizontalAlignment.LEFT, Alignment.Left);
        ALIGNMENT_MAP.put(HorizontalAlignment.RIGHT, Alignment.Right);
        ALIGNMENT_MAP.put(HorizontalAlignment.CENTER, Alignment.Center);
        ALIGNMENT_MAP.put(HorizontalAlignment.JUSTIFY, Alignment.Justify);
    }

    public ParagraphCellDrawer(ParagraphCell cell) {
        this.cell = cell;
    }

    @Override
    public void drawContent(DrawingContext drawingContext) throws IOException {
        if (drawingContext.getPage() == null) {
            throw new PageNotSetException("Page is not set in drawing context. Please ensure the page is set on table drawer.");
        }

        Paragraph paragraph = cell.getParagraph().getWrappedParagraph();

        AnnotationDrawListener annotationDrawListener = createAndGetAnnotationDrawListenerWith(drawingContext);

        float x = drawingContext.getStartingPoint().x + cell.getPaddingLeft();
        float y = drawingContext.getStartingPoint().y + getAdaptionForVerticalAlignment();

        paragraph.drawText(
                drawingContext.getContentStream(),
                new Position(x, y),
                ALIGNMENT_MAP.getOrDefault(cell.getSettings().getHorizontalAlignment(), Alignment.Left),
                annotationDrawListener
        );

        annotationDrawListener.afterPage(null);
        annotationDrawListener.afterRender();
        drawingContext.getPage().getAnnotations().forEach(PDAnnotation::constructAppearances);
    }

    @Override
    protected float calculateInnerHeight() throws IOException {
        return cell.getParagraph().getWrappedParagraph().getHeight();
    }

    private AnnotationDrawListener createAndGetAnnotationDrawListenerWith(DrawingContext drawingContext) {
        return new AnnotationDrawListener(new DrawContext() {
                @Override
                public PDDocument getPdDocument() {
                    return null;
                }

                @Override
                public PDPage getCurrentPage() {
                    return drawingContext.getPage();
                }

                @Override
                public PDPageContentStream getCurrentPageContentStream() {
                    return drawingContext.getContentStream();
                }
            });
    }

    private class PageNotSetException extends RuntimeException {
        public PageNotSetException(String message) {
            super(message);
        }
    }
}
