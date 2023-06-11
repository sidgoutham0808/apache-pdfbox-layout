package com.custom.pdfboxLayout.structure.cell;

import com.custom.pdfboxLayout.drawing.Drawer;
import com.custom.pdfboxLayout.drawing.cell.TextCellDrawer;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class TextCell extends AbstractTextCell {

    @NonNull
    protected String text;

    protected Drawer createDefaultDrawer() {
        return new TextCellDrawer(this);
    }

}
