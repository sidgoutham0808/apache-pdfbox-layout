package com.custom.pdfboxLayout.drawing.cell;

import ca.on.gov.onbis.ckorf.pdfboxUtil.drawing.*;
import com.custom.pdfboxLayout.drawing.*;
import com.custom.pdfboxLayout.settings.VerticalAlignment;
import com.custom.pdfboxLayout.structure.cell.AbstractCell;
import com.custom.pdfboxLayout.util.FloatUtil;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;

public abstract class AbstractCellDrawer<T extends AbstractCell> implements Drawer {

    protected T cell;

    public AbstractCellDrawer<T> withCell(T cell) {
        this.cell = cell;
        return this;
    }

    @Override
    public void drawBackground(DrawingContext drawingContext) throws IOException {
        if (cell.hasBackgroundColor()) {
            final PDPageContentStream contentStream = drawingContext.getContentStream();
            final Point2D.Float start = drawingContext.getStartingPoint();

            final float rowHeight = cell.getRow().getHeight();
            final float height = Math.max(cell.getHeight(), rowHeight);
            final float y = rowHeight < cell.getHeight()
                    ? start.y + rowHeight - cell.getHeight()
                    : start.y;

            DrawingUtil.drawRectangle(contentStream,
                    PositionedRectangle.builder()
                            .x(start.x)
                            .y(y)
                            .width(cell.getWidth())
                            .height(height)
                            .color(cell.getBackgroundColor())
                            .build()
            );
        }
    }

    @Override
    public abstract void drawContent(DrawingContext drawingContext) throws IOException;

    @Override
    public void drawBorders(DrawingContext drawingContext) {
        final Point2D.Float start = drawingContext.getStartingPoint();
        final PDPageContentStream contentStream = drawingContext.getContentStream();

        final float cellWidth = cell.getWidth();

        final float rowHeight = cell.getRow().getHeight();
        final float height = Math.max(cell.getHeight(), rowHeight);
        final float sY = rowHeight < cell.getHeight()
                ? start.y + rowHeight - cell.getHeight()
                : start.y;

        // Handle the cell's borders
        final Color cellBorderColorTop = cell.getBorderColorTop();
        final Color cellBorderColorBottom = cell.getBorderColorBottom();
        final Color cellBorderColorLeft = cell.getBorderColorLeft();
        final Color cellBorderColorRight = cell.getBorderColorRight();

        final Color rowBorderColor = cell.getRow().getBorderColor();

        if (cell.hasBorderTop() || cell.hasBorderBottom()) {
            final float correctionLeft = cell.getBorderWidthLeft() / 2;
            final float correctionRight = cell.getBorderWidthRight() / 2;

            if (cell.hasBorderTop()) {
                try {
                    DrawingUtil.drawLine(contentStream, PositionedLine.builder()
                            .startX(start.x - correctionLeft)
                            .startY(start.y + rowHeight)
                            .endX(start.x + cellWidth + correctionRight)
                            .endY(start.y + rowHeight)
                            .width(cell.getBorderWidthTop())
                            .color(cellBorderColorTop)
                            .resetColor(rowBorderColor)
                            .borderStyle(cell.getBorderStyleTop())
                            .build()
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (cell.hasBorderBottom()) {
                try {
                    DrawingUtil.drawLine(contentStream, PositionedLine.builder()
                            .startX(start.x - correctionLeft)
                            .startY(sY)
                            .endX(start.x + cellWidth + correctionRight)
                            .endY(sY)
                            .width(cell.getBorderWidthBottom())
                            .color(cellBorderColorBottom)
                            .resetColor(rowBorderColor)
                            .borderStyle(cell.getBorderStyleBottom())
                            .build()
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (cell.hasBorderLeft() || cell.hasBorderRight()) {
            final float correctionTop = cell.getBorderWidthTop() / 2;
            final float correctionBottom = cell.getBorderWidthBottom() / 2;

            if (cell.hasBorderLeft()) {
                try {
                    DrawingUtil.drawLine(contentStream, PositionedLine.builder()
                            .startX(start.x)
                            .startY(sY - correctionBottom)
                            .endX(start.x)
                            .endY(sY + height + correctionTop)
                            .width(cell.getBorderWidthLeft())
                            .color(cellBorderColorLeft)
                            .resetColor(rowBorderColor)
                            .borderStyle(cell.getBorderStyleLeft())
                            .build()
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (cell.hasBorderRight()) {
                try {
                    DrawingUtil.drawLine(contentStream, PositionedLine.builder()
                            .startX(start.x + cellWidth)
                            .startY(sY - correctionBottom)
                            .endX(start.x + cellWidth)
                            .endY(sY + height + correctionTop)
                            .width(cell.getBorderWidthRight())
                            .color(cellBorderColorRight)
                            .resetColor(rowBorderColor)
                            .borderStyle(cell.getBorderStyleRight())
                            .build()
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected boolean rowHeightIsBiggerThanOrEqualToCellHeight() {
        return cell.getRow().getHeight() > cell.getHeight()
                || FloatUtil.isEqualInEpsilon(cell.getRow().getHeight(), cell.getHeight());
    }

    protected float getRowSpanAdaption() {
        return cell.getRowSpan() > 1
                ? cell.calculateHeightForRowSpan() - cell.getRow().getHeight()
                : 0;
    }

    protected float calculateOuterHeight() {
        return cell.getRowSpan() > 1 ? cell.getHeight() : cell.getRow().getHeight();
    }

    protected float getAdaptionForVerticalAlignment() throws IOException {
        if (rowHeightIsBiggerThanOrEqualToCellHeight() || cell.getRowSpan() > 1) {

            if (cell.isVerticallyAligned(VerticalAlignment.MIDDLE)) {
                return (calculateOuterHeight() / 2 + calculateInnerHeight() / 2) - getRowSpanAdaption();

            } else if (cell.isVerticallyAligned(VerticalAlignment.BOTTOM)) {
                return (calculateInnerHeight() + cell.getPaddingBottom()) - getRowSpanAdaption();
            }
        }

        // top alignment (default case)
        return cell.getRow().getHeight() - cell.getPaddingTop(); // top position
    }

    protected abstract float calculateInnerHeight() throws IOException;
}
