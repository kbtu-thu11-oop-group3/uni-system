package com.kbtu.oop.project.ui.app.table;

import javax.swing.table.AbstractTableModel;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GenericTableModel<T> extends AbstractTableModel {

    private final List<Column<T, ?>> columns;
    private final List<T> rows = new ArrayList<>();

    public GenericTableModel(List<Column<T, ?>> columns) {
        this.columns = columns;
    }

    public List<Column<T, ?>> getColumns() {
        return columns;
    }

    public void setRows(List<T> items) {
        rows.clear();
        rows.addAll(items);
        fireTableDataChanged();
    }

    public List<T> getRows() {
        return rows;
    }

    public T getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).getName();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columns.get(columnIndex).getType();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).isEditable();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        T row = rows.get(rowIndex);
        return columns.get(columnIndex).getGetter().apply(row);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Column<T, Object> column = (Column<T, Object>) columns.get(columnIndex);

        if (!column.isEditable() || column.getSetter() == null)
            return;

        Object converted = convertValue(aValue, column.getType());
        column.getSetter().accept(rows.get(rowIndex), converted);

        fireTableCellUpdated(rowIndex, columnIndex);
    }

    private Object convertValue(Object value, Class<?> type) {
        if (value == null || type.isInstance(value))
            return value;

        String text = value.toString().trim();

        if (type == String.class)
            return text;
        if (type == Integer.class || type == int.class)
            return Integer.parseInt(text);
        if (type == Double.class || type == double.class)
            return Double.parseDouble(text);
        if (type == Boolean.class || type == boolean.class)
            return Boolean.parseBoolean(text);
        if (type == UUID.class)
            return UUID.fromString(text);

        if (type.isEnum()) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Enum<?> e = Enum.valueOf((Class<Enum>) type, text.toUpperCase());
            return e;
        }

        return value;
    }

    public void configureTable(JTable table) {
        for (int i = 0; i < columns.size(); i++) {
            Column<T, ?> col = columns.get(i);

            var tc = table.getColumnModel().getColumn(i);
            tc.setPreferredWidth(col.getWidth());

            AlignRenderer renderer = new AlignRenderer(col.getAlignment());
            tc.setCellRenderer(renderer);
        }
    }
}