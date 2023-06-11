package com.custom.pdfboxLayout.layout.text;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import com.custom.pdfboxLayout.layout.util.CompatibilityHelper;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * A text of line containing only {@link LayoutStyledText}s. It may be terminated by a
 * {@link #getNewLine() new line}.
 */
public class TextLine implements TextSequence {
    
    /**
     * The font ascent.
     */
    private static final String ASCENT = "ascent";
    /**
     * The font height.
     */
    private static final String HEIGHT = "height";
    /**
     * The text width.
     */
    private static final String WIDTH = "width";

    private final List<LayoutStyledText> layoutStyledTextList = new ArrayList<LayoutStyledText>();
    private NewLine newLine;
    private Map<String, Object> cache = new HashMap<String, Object>();

    private void clearCache() {
	cache.clear();
    }

    private void setCachedValue(final String key, Object value) {
	cache.put(key, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T getCachedValue(final String key, Class<T> type) {
	return (T) cache.get(key);
    }

    /**
     * Adds a styled text.
     * 
     * @param fragment
     *            the fagment to add.
     */
    public void add(final LayoutStyledText fragment) {
	layoutStyledTextList.add(fragment);
	clearCache();
    }

    /**
     * Adds all styled texts of the given text line.
     * 
     * @param textLine
     *            the text line to add.
     */
    public void add(final TextLine textLine) {
	for (LayoutStyledText fragment : textLine.getStyledTexts()) {
	    add(fragment);
	}
    }

    /**
     * @return the terminating new line, may be <code>null</code>.
     */
    public NewLine getNewLine() {
	return newLine;
    }

    /**
     * Sets the new line.
     * 
     * @param newLine
     *            the new line.
     */
    public void setNewLine(NewLine newLine) {
	this.newLine = newLine;
	clearCache();
    }

    /**
     * @return the styled texts building up this line.
     */
    public List<LayoutStyledText> getStyledTexts() {
	return Collections.unmodifiableList(layoutStyledTextList);
    }

    @Override
    public Iterator<TextFragment> iterator() {
	return new TextLineIterator(layoutStyledTextList.iterator(), newLine);
    }

    /**
     * @return <code>true</code> if the line contains neither styled text nor a
     *         new line.
     */
    public boolean isEmpty() {
	return layoutStyledTextList.isEmpty() && newLine == null;
    }

    @Override
    public float getWidth() throws IOException {
	Float width = getCachedValue(WIDTH, Float.class);
	if (width == null) {
	    width = 0f;
	    for (TextFragment fragment : this) {
		width += fragment.getWidth();
	    }
	    setCachedValue(WIDTH, width);
	}
	return width;
    }

    @Override
    public float getHeight() throws IOException {
	Float height = getCachedValue(HEIGHT, Float.class);
	if (height == null) {
	    height = 0f;
	    for (TextFragment fragment : this) {
		height = Math.max(height, fragment.getHeight());
	    }
	    setCachedValue(HEIGHT, height);
	}
	return height;
    }

    /**
     * @return the (max) ascent of this line.
     * @throws IOException
     *             by pdfbox.
     */
    protected float getAscent() throws IOException {
	Float ascent = getCachedValue(ASCENT, Float.class);
	if (ascent == null) {
	    ascent = 0f;
	    for (TextFragment fragment : this) {
		float currentAscent = fragment.getFontDescriptor().getSize()
			* fragment.getFontDescriptor().getFont()
				.getFontDescriptor().getAscent() / 1000;
		ascent = Math.max(ascent, currentAscent);
	    }
	    setCachedValue(ASCENT, ascent);
	}
	return ascent;
    }

    @Override
    public void drawText(PDPageContentStream contentStream, Position upperLeft,
	    Alignment alignment, DrawListener drawListener) throws IOException {
	drawAligned(contentStream, upperLeft, alignment, getWidth(), drawListener);
    }

    public void drawAligned(PDPageContentStream contentStream, Position upperLeft,
							Alignment alignment, float availableLineWidth,
							DrawListener drawListener) throws IOException {
	contentStream.saveGraphicsState();
	contentStream.beginText();

	float x = upperLeft.getX();
	float y = upperLeft.getY() - getAscent(); // the baseline
	float offset = TextSequenceUtil.getOffset(this, availableLineWidth, alignment);
	x += offset;
	CompatibilityHelper.setTextTranslation(contentStream, x, y);
	float extraWordSpacing = 0;
	if (alignment == Alignment.Justify && (getNewLine() instanceof WrappingNewLine) ){
	    extraWordSpacing = (availableLineWidth - getWidth()) / (layoutStyledTextList.size()-1);
	}
	
	FontDescriptor lastFontDesc = null;
	float lastBaselineOffset = 0;
	Color lastColor = null;
	float gap = 0;
	for (LayoutStyledText layoutStyledText : layoutStyledTextList) {
	    if (!layoutStyledText.getFontDescriptor().equals(lastFontDesc)) {
		lastFontDesc = layoutStyledText.getFontDescriptor();
		contentStream.setFont(lastFontDesc.getFont(),
			lastFontDesc.getSize());
	    }
	    if (!layoutStyledText.getColor().equals(lastColor)) {
		lastColor = layoutStyledText.getColor();
		contentStream.setNonStrokingColor(lastColor);
	    }
	    if (layoutStyledText.getLeftMargin() > 0) {
		gap += layoutStyledText.getLeftMargin();
	    }

	    boolean moveBaseline = layoutStyledText.getBaselineOffset() != lastBaselineOffset;
	    if (moveBaseline || gap > 0) {
		float baselineDelta = lastBaselineOffset - layoutStyledText.getBaselineOffset();
		lastBaselineOffset = layoutStyledText.getBaselineOffset();
		CompatibilityHelper.moveTextPosition(contentStream, gap, baselineDelta);
		x += gap;
	    }
	    if (layoutStyledText.getText().length() > 0) {
		CompatibilityHelper.showText(contentStream,
			layoutStyledText.getText());
	    }

	    if (drawListener != null) {
		float currentUpperLeft = y + layoutStyledText.getAsent();
		drawListener.drawn(layoutStyledText,
			new Position(x, currentUpperLeft),
			layoutStyledText.getWidthWithoutMargin(),
			layoutStyledText.getHeight());
	    }
	    x += layoutStyledText.getWidthWithoutMargin();

	    gap = extraWordSpacing;
	    if (layoutStyledText.getRightMargin() > 0) {
		gap += layoutStyledText.getRightMargin();
	    }
	}
	contentStream.endText();
	contentStream.restoreGraphicsState();
    }

    @Override
    public String toString() {
	return "TextLine [styledText=" + layoutStyledTextList + ", newLine="
		+ newLine + "]";
    }

    /**
     * An iterator for the text line. See {@link TextLine#iterator()}.
     */
    private static class TextLineIterator implements Iterator<TextFragment> {

	private Iterator<LayoutStyledText> styledText;
	private NewLine newLine;

	/**
	 * Creates an iterator of the given styled texts with an optional
	 * trailing new line.
	 * 
	 * @param styledText
	 *            the text fragments to iterate.
	 * @param newLine
	 *            the optional trailing new line.
	 */
	public TextLineIterator(Iterator<LayoutStyledText> styledText, NewLine newLine) {
	    super();
	    this.styledText = styledText;
	    this.newLine = newLine;
	}

	@Override
	public boolean hasNext() {
	    return styledText.hasNext() || newLine != null;
	}

	@Override
	public TextFragment next() {
	    TextFragment next = null;
	    if (styledText.hasNext()) {
		next = styledText.next();
	    } else if (newLine != null) {
		next = newLine;
		newLine = null;
	    }
	    return next;
	}

	@Override
	public void remove() {
	    throw new UnsupportedOperationException();
	}

    }

}
