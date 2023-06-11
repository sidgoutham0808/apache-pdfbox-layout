package com.custom.pdfboxLayout.layout.shape;

import com.custom.pdfboxLayout.layout.text.DrawListener;
import com.custom.pdfboxLayout.layout.text.Position;
import com.custom.pdfboxLayout.layout.util.CompatibilityHelper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.*;
import java.io.IOException;

/**
 * Abstract base class for shapes which performs the
 * {@link #fill(PDDocument, PDPageContentStream, Position, float, float, Color, DrawListener)}
 * and (@link
 * {@link #draw(PDDocument, PDPageContentStream, Position, float, float, Color, com.custom.pdfboxLayout.layout.shape.Stroke, DrawListener)}
 * .
 */
public abstract class AbstractShape implements Shape {

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
                     Position upperLeft, float width, float height, Color color,
                     Stroke stroke, DrawListener drawListener) throws IOException {

	add(pdDocument, contentStream, upperLeft, width, height);

	if (stroke != null) {
	    stroke.applyTo(contentStream);
	}
	if (color != null) {
	    contentStream.setStrokingColor(color);
	}
	contentStream.stroke();

	if (drawListener != null) {
	    drawListener.drawn(this, upperLeft, width, height);
	}

    }

    @Override
    public void fill(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft, float width, float height, Color color,
	    DrawListener drawListener) throws IOException {

	add(pdDocument, contentStream, upperLeft, width, height);

	if (color != null) {
	    contentStream.setNonStrokingColor(color);
	}
	CompatibilityHelper.fillNonZero(contentStream);

	if (drawListener != null) {
	    drawListener.drawn(this, upperLeft, width, height);
	}

    }

}
