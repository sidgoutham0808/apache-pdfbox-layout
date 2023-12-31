package com.custom.pdfboxLayout.layout.shape;

import org.apache.pdfbox.pdmodel.PDDocument;
import com.custom.pdfboxLayout.layout.text.DrawListener;
import com.custom.pdfboxLayout.layout.text.Position;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.*;
import java.io.IOException;

/**
 * Shapes can be used to either
 * {@link #draw(PDDocument, PDPageContentStream, Position, float, float, Color, com.custom.pdfboxLayout.layout.shape.Stroke, DrawListener)
 * stroke} or
 * {@link #fill(PDDocument, PDPageContentStream, Position, float, float, Color, DrawListener)
 * fill} the path of the shape, or simply
 * {@link #add(PDDocument, PDPageContentStream, Position, float, float) add the
 * path} of the shape to the drawing context.
 */
public interface Shape {

    /**
     * Draws (strokes) the shape.
     * 
     * @param pdDocument
     *            the underlying pdfbox document.
     * @param contentStream
     *            the stream to draw to.
     * @param upperLeft
     *            the upper left position to start drawing.
     * @param width
     *            the width of the bounding box.
     * @param height
     *            the height of the bounding box.
     * @param color
     *            the color to use.
     * @param stroke
     *            the stroke to use.
     * @param drawListener
     *            the listener to
     *            {@link DrawListener#drawn(Object, Position, float, float)
     *            notify} on drawn objects.
     * @throws IOException
     *             by pdfbox
     */
    void draw(PDDocument pdDocument, PDPageContentStream contentStream,
              Position upperLeft, float width, float height, Color color,
              Stroke stroke, DrawListener drawListener) throws IOException;

    /**
     * Fills the shape.
     * 
     * @param pdDocument
     *            the underlying pdfbox document.
     * @param contentStream
     *            the stream to draw to.
     * @param upperLeft
     *            the upper left position to start drawing.
     * @param width
     *            the width of the bounding box.
     * @param height
     *            the height of the bounding box.
     * @param color
     *            the color to use.
     * @param drawListener
     *            the listener to
     *            {@link DrawListener#drawn(Object, Position, float, float)
     *            notify} on drawn objects.
     * @throws IOException
     *             by pdfbox
     */
    void fill(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft, float width, float height, Color color,
	    DrawListener drawListener) throws IOException;

    /**
     * Adds (the path of) the shape without drawing anything.
     * 
     * @param pdDocument
     *            the underlying pdfbox document.
     * @param contentStream
     *            the stream to draw to.
     * @param upperLeft
     *            the upper left position to start drawing.
     * @param width
     *            the width of the bounding box.
     * @param height
     *            the height of the bounding box.
     * @throws IOException
     *             by pdfbox
     */
    void add(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft, float width, float height) throws IOException;

}
