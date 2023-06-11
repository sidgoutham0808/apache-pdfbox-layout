package com.custom.pdfboxLayout.layout.util;

import com.custom.pdfboxLayout.layout.text.annotations.Annotations;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Provide compatible methods for API changes from pdfbox 1x to 2x.
 */
public class CompatibilityHelper {

    private final static String BULLET = Character.toString((char) 149);
    private final static String DOUBLE_ANGLE = Character.toString((char) 187);

    private static final String IMAGE_CACHE = "IMAGE_CACHE";
    private static Map<PDDocument, Map<String, Map<?, ?>>> documentCaches = new WeakHashMap<PDDocument, Map<String, Map<?, ?>>>();
    private static PDBorderStyleDictionary noBorder;

    /**
     * Returns the bullet character for the given level. Actually only two
     * bullets are used for odd and even levels. For odd levels the
     * {@link #BULLET bullet} character is used, for even it is the
     * {@link #DOUBLE_ANGLE double angle}. You may customize this by setting the
     * system properties <code>pdfbox.layout.bullet.odd</code> and/or
     * <code>pdfbox.layout.bullet.even</code>.
     * 
     * @param level
     *            the level to return the bullet for.
     * @return the bullet character for the level.
     */
    public static String getBulletCharacter(final int level) {
	if (level % 2 == 1) {
	    return System.getProperty("pdfbox.layout.bullet.odd", BULLET);
	}
	return System.getProperty("pdfbox.layout.bullet.even", DOUBLE_ANGLE);
    }

    public static void clip(final PDPageContentStream contentStream)
	    throws IOException {
	contentStream.clipPath(PathIterator.WIND_NON_ZERO);
    }

    public static void transform(final PDPageContentStream contentStream,
	    float a, float b, float c, float d, float e, float f)
	    throws IOException {
	contentStream.concatenate2CTM(a, b, c, d, e, f);
    }

    public static void curveTo(final PDPageContentStream contentStream,
	    float x1, float y1, float x2, float y2, float x3, float y3)
	    throws IOException {
	contentStream.addBezier312(x1, y1, x2, y2, x3, y3);
    }

    public static void curveTo1(final PDPageContentStream contentStream,
	    float x1, float y1, float x3, float y3) throws IOException {
	contentStream.addBezier31(x1, y1, x3, y3);
    }

    public static void fillNonZero(final PDPageContentStream contentStream)
	    throws IOException {
	contentStream.fill(PathIterator.WIND_NON_ZERO);
    }

    public static void showText(final PDPageContentStream contentStream,
	    final String text) throws IOException {
	contentStream.drawString(text);
    }

    public static void setTextTranslation(
	    final PDPageContentStream contentStream, final float x,
	    final float y) throws IOException {
	contentStream.setTextTranslation(x, y);
    }

    public static void moveTextPosition(
	    final PDPageContentStream contentStream, final float x,
	    final float y) throws IOException {
	contentStream.concatenate2CTM(1, 0, 0, 1, x, y);
    }

    public static PDPageContentStream createAppendablePDPageContentStream(
	    final PDDocument pdDocument, final PDPage page) throws IOException {
	return new PDPageContentStream(pdDocument, page, true, true);
    }

    /**
     * Renders the given page as an RGB image.
     *
     * @param document
     *            the document containing the page.
     * @param pageIndex
     *            the index of the page to render.
     * @param resolution
     *            the image resolution.
     * @return the rendered image
     * @throws IOException
     *             by pdfbox
     */
    public static BufferedImage createImageFromPage(final PDDocument document,
	    final int pageIndex, final int resolution) throws IOException {
	final PDPage page = (PDPage) document.getDocumentCatalog()
			.getPages().get(pageIndex);
	PDFRenderer pr = new PDFRenderer (document);
	return pr.renderImageWithDPI (0, 300);
    }

    public static int getPageRotation(final PDPage page) {
	return page.getRotation() == 0 ? 0 : page.getRotation();
    }

    public static PDAnnotationLink createLink(PDPage page, PDRectangle rect, Color color,
											  Annotations.HyperlinkAnnotation.LinkStyle linkStyle, final String uri) {
	PDAnnotationLink pdLink = createLink(page, rect, color, linkStyle);

	PDActionURI actionUri = new PDActionURI();
	actionUri.setURI(uri);
	pdLink.setAction(actionUri);
	return pdLink;
    }

    public static PDAnnotationLink createLink(PDPage page, PDRectangle rect, Color color,
											  Annotations.HyperlinkAnnotation.LinkStyle linkStyle, final PDDestination destination) {
	PDAnnotationLink pdLink = createLink(page, rect, color, linkStyle);

	PDActionGoTo gotoAction = new PDActionGoTo();
	gotoAction.setDestination(destination);
	pdLink.setAction(gotoAction);
	return pdLink;
    }

    private static PDAnnotationLink createLink(PDPage page, PDRectangle rect, Color color,
	    Annotations.HyperlinkAnnotation.LinkStyle linkStyle) {
	PDAnnotationLink pdLink = new PDAnnotationLink();
	pdLink.setBorderStyle(toBorderStyle(linkStyle));
	PDRectangle rotatedRect = transformToPageRotation(rect, page);
	pdLink.setRectangle(rotatedRect);
	setAnnotationColor(pdLink, color);
	return pdLink;
    }

    private static PDBorderStyleDictionary toBorderStyle(
	    final Annotations.HyperlinkAnnotation.LinkStyle linkStyle) {
	if (linkStyle == Annotations.HyperlinkAnnotation.LinkStyle.none) {
	    return getNoBorder();
	}
	PDBorderStyleDictionary borderStyle = new PDBorderStyleDictionary();
	borderStyle.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
	return borderStyle;
    }

    /**
     * Sets the color in the annotation.
     * 
     * @param annotation
     *            the annotation.
     * @param color
     *            the color to set.
     */
    public static void setAnnotationColor(final PDAnnotation annotation,
	    Color color) {
	annotation.setColor(toPDColor(color));
    }

    private static PDColor toPDColor(final Color color) {
	float[] components = new float[] {
				color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f };
	return new PDColor(components, PDDeviceRGB.INSTANCE);
    }

    /**
     * Return the quad points representation of the given rect.
     * 
     * @param rect
     *            the rectangle.
     * @return the quad points.
     */
    public static float[] toQuadPoints(final PDRectangle rect) {
	return toQuadPoints(rect, 0, 0);
    }

    /**
     * Return the quad points representation of the given rect.
     * 
     * @param rect
     *            the rectangle.
     * @param xOffset
     *            the offset in x-direction to add.
     * @param yOffset
     *            the offset in y-direction to add.
     * @return the quad points.
     */
    public static float[] toQuadPoints(final PDRectangle rect, float xOffset,
	    float yOffset) {
	float[] quads = new float[8];
	quads[0] = rect.getLowerLeftX() + xOffset; // x1
	quads[1] = rect.getUpperRightY() + yOffset; // y1
	quads[2] = rect.getUpperRightX() + xOffset; // x2
	quads[3] = quads[1]; // y2
	quads[4] = quads[0]; // x3
	quads[5] = rect.getLowerLeftY() + yOffset; // y3
	quads[6] = quads[2]; // x4
	quads[7] = quads[5]; // y5
	return quads;
    }

    /**
     * Transform the quad points in order to match the page rotation
     * @param quadPoints the quad points.
     * @param page the page.
     * @return the transformed quad points.
     */
    public static float[] transformToPageRotation(
	    final float[] quadPoints, final PDPage page) {
	AffineTransform transform = transformToPageRotation(page);
	if (transform == null) {
	    return quadPoints;
	}
	float[] rotatedPoints = new float[quadPoints.length];
	transform.transform(quadPoints, 0, rotatedPoints, 0, 4);
	return rotatedPoints;
    }
    
    /**
     * Transform the rectangle in order to match the page rotation
     * @param rect the rectangle.
     * @param page the page.
     * @return the transformed rectangle.
     */
    public static PDRectangle transformToPageRotation(
	    final PDRectangle rect, final PDPage page) {
	AffineTransform transform = transformToPageRotation(page);
	if (transform == null) {
	    return rect;
	}
	float[] points = new float[] {rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getUpperRightX(), rect.getUpperRightY()};
	float[] rotatedPoints = new float[4]; 
	transform.transform(points, 0, rotatedPoints, 0, 2);
	PDRectangle rotated = new PDRectangle();
	rotated.setLowerLeftX(rotatedPoints[0]);
	rotated.setLowerLeftY(rotatedPoints[1]);
	rotated.setUpperRightX(rotatedPoints[2]);
	rotated.setUpperRightY(rotatedPoints[3]);
	return rotated;
    }
    
    private static AffineTransform transformToPageRotation(final PDPage page) {
	int pageRotation = getPageRotation(page);
	if (pageRotation == 0) {
	    return null;
	}
	float pageWidth = page.getMediaBox().getHeight();
	float pageHeight = page.getMediaBox().getWidth();
	AffineTransform transform = new AffineTransform();
	transform.rotate(pageRotation * Math.PI / 180, pageHeight / 2,
		pageWidth / 2);
	double offset = Math.abs(pageHeight - pageWidth) / 2;
	transform.translate(-offset, offset);
	return transform;
    }

    private static PDBorderStyleDictionary getNoBorder() {
	if (noBorder == null) {
	    noBorder = new PDBorderStyleDictionary();
	    noBorder.setWidth(0);
	}
	return noBorder;
    }

    private static synchronized Map<String, Map<?, ?>> getDocumentCache(
	    final PDDocument document) {
	Map<String, Map<?, ?>> cache = documentCaches.get(document);
	if (cache == null) {
	    cache = new HashMap<String, Map<?, ?>>();
	    documentCaches.put(document, cache);
	}
	return cache;
    }


	private static synchronized Map<BufferedImage, PDImageXObject> getImageCache(
			final PDDocument document) {
		Map<String, Map<?, ?>> documentCache = getDocumentCache(document);
		@SuppressWarnings("unchecked")
		Map<BufferedImage, PDImageXObject> imageCache = (Map<BufferedImage, PDImageXObject>) documentCache
				.get(IMAGE_CACHE);
		if (imageCache == null) {
			imageCache = new HashMap<BufferedImage, PDImageXObject>();
			documentCache.put(IMAGE_CACHE, imageCache);
		}
		return imageCache;
	}


}
