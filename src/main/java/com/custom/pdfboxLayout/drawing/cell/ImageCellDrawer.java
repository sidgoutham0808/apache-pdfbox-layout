package com.custom.pdfboxLayout.drawing.cell;

import com.custom.pdfboxLayout.drawing.DrawingContext;
import com.custom.pdfboxLayout.settings.HorizontalAlignment;
import com.custom.pdfboxLayout.structure.cell.ImageCell;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.geom.Point2D;
import java.io.IOException;

@NoArgsConstructor
public class ImageCellDrawer extends AbstractCellDrawer<ImageCell> {

    public ImageCellDrawer(ImageCell cell) {
        this.cell = cell;
    }

    @Override
    public void drawContent(DrawingContext drawingContext) throws IOException {
        final PDPageContentStream contentStream = drawingContext.getContentStream();
        final float moveX = drawingContext.getStartingPoint().x;

        final Point2D.Float size = cell.getFitSize();
        final Point2D.Float drawAt = new Point2D.Float();

        // Handle horizontal alignment by adjusting the xOffset
        float xOffset = moveX + cell.getPaddingLeft();
        if (cell.getSettings().getHorizontalAlignment() == HorizontalAlignment.RIGHT) {
            xOffset = moveX + (cell.getWidth() - (size.x + cell.getPaddingRight()));

        } else if (cell.getSettings().getHorizontalAlignment() == HorizontalAlignment.CENTER) {
            final float diff = (cell.getWidth() - size.x) / 2;
            xOffset = moveX + diff;

        }

        drawAt.x = xOffset;
        drawAt.y = drawingContext.getStartingPoint().y + getAdaptionForVerticalAlignment() - size.y;

        contentStream.drawImage(cell.getImage(), drawAt.x, drawAt.y, size.x, size.y);
    }

    @Override
    protected float calculateInnerHeight() {
        return (float) cell.getFitSize().getY();
    }

}
