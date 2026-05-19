package com.kbtu.oop.project.ui.app.table;

import javax.swing.table.DefaultTableCellRenderer;

public class AlignRenderer extends DefaultTableCellRenderer {

    public AlignRenderer(int alignment) {
        setHorizontalAlignment(alignment);
    }
}