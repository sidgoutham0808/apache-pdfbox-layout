package com.custom.pdfboxLayout.drawing;

import com.custom.pdfboxLayout.settings.BorderStyleInterface;
import lombok.Builder;
import lombok.Getter;

import java.awt.*;

@Builder(toBuilder = true)
@Getter
public class PositionedLine {

    private float width;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private Color color;
    private Color resetColor;
    private BorderStyleInterface borderStyle;

}
