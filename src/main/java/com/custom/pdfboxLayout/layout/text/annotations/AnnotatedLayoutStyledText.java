package com.custom.pdfboxLayout.layout.text.annotations;

import com.custom.pdfboxLayout.layout.text.FontDescriptor;
import com.custom.pdfboxLayout.layout.text.LayoutStyledText;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Extension of styled text that supports annotations.
 */
public class AnnotatedLayoutStyledText extends LayoutStyledText implements Annotated {

    private List<Annotation> annotations = new ArrayList<Annotation>();

    /**
     * Creates a styled text.
     * 
     * @param text
     *            the text to draw. Must not contain line feeds ('\n').
     * @param fontDescriptor
     *            the font to use.
     * @param color
     *            the color to use.
     * @param baselineOffset
     *            the offset of the baseline.
     * @param leftMargin
     *            the margin left to the text.
     * @param rightMargin
     *            the margin right to the text.
     * @param annotations
     *            the annotations associated with the text.
     */
    public AnnotatedLayoutStyledText(final String text,
                                     final FontDescriptor fontDescriptor, final Color color,
                                     final float leftMargin, final float rightMargin,
                                     final float baselineOffset,
                                     Collection<? extends Annotation> annotations) {
	super(text, fontDescriptor, color, baselineOffset, leftMargin,
		rightMargin);
	if (annotations != null) {
	    this.annotations.addAll(annotations);
	}
    }

    /**
     * Creates a styled text.
     * 
     * @param text
     *            the text to draw. Must not contain line feeds ('\n').
     * @param size
     *            the size of the font.
     * @param font
     *            the font to use..
     * @param color
     *            the color to use.
     * @param baselineOffset
     *            the offset of the baseline.
     * @param annotations
     *            the annotations associated with the text.
     */
    public AnnotatedLayoutStyledText(String text, float size, PDFont font,
									 Color color, final float baselineOffset,
									 Collection<? extends Annotation> annotations) {
	this(text, new FontDescriptor(font, size), color, baselineOffset, 0, 0,
		annotations);
    }

    @Override
    public Iterator<Annotation> iterator() {
	return annotations.iterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> Iterable<T> getAnnotationsOfType(Class<T> type) {
	List<T> result = null;
	for (Annotation annotation : annotations) {
	    if (type.isAssignableFrom(annotation.getClass())) {
		if (result == null) {
		    result = new ArrayList<T>();
		}
		result.add((T) annotation);
	    }
	}

	if (result == null) {
	    return Collections.emptyList();
	}
	return result;
    }

    /**
     * Adds an annotation.
     * 
     * @param annotation
     *            the annotation to add.
     */
    public void addAnnotation(final Annotation annotation) {
	annotations.add(annotation);
    }

    /**
     * Adds all annotations.
     * 
     * @param annos
     *            the annotations to add.
     */
    public void addAllAnnotation(final Collection<Annotation> annos) {
	annotations.addAll(annos);
    }

    @Override
    public AnnotatedLayoutStyledText inheritAttributes(String text, float leftMargin,
													   float rightMargin) {
	return new AnnotatedLayoutStyledText(text, getFontDescriptor(), getColor(),
		getBaselineOffset(), leftMargin, rightMargin, annotations);
    }
}
