package com.kbtu.oop.project.ui.app.table;

import com.kbtu.oop.project.util.I18n;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.util.regex.Pattern;

public final class TableUtils {

    private TableUtils() {
    }

    public static JTextField addSearchField(JPanel toolbar, JTable table) {
        TableRowSorter<?> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        JTextField searchField = new JTextField(18);
        searchField.setToolTipText(I18n.get("ui.search.tooltip"));
        toolbar.add(searchField);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                apply();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                apply();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                apply();
            }

            private void apply() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                    return;
                }
                String safe = Pattern.quote(text);
                sorter.setRowFilter(RowFilter.regexFilter(safe));
            }
        });

        return searchField;
    }
}
