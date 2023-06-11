package com.custom.pdfboxLayout.structure.cell;

import com.custom.pdfboxLayout.drawing.Drawer;
import com.custom.pdfboxLayout.drawing.cell.VerticalTextCellDrawer;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class VerticalTextCell extends AbstractTextCell {

    @NonNull
    private String text;

    protected Drawer createDefaultDrawer() {
        return new VerticalTextCellDrawer(this);
    }

}
