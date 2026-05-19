package com.kbtu.oop.project.ui.app.table;

import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.swing.SwingConstants;

public class Column<T, V> {

    private final String name;
    private final Class<V> type;
    private final Function<T, V> getter;
    private final BiConsumer<T, V> setter;
    private final boolean editable;
    private final int alignment;
    private final int width;

    private Column(Builder<T, V> b) {
        this.name = b.name;
        this.type = b.type;
        this.getter = b.getter;
        this.setter = b.setter;
        this.editable = b.editable;
        this.alignment = b.alignment;
        this.width = b.width;
    }

    public String getName() {
        return name;
    }

    public Class<V> getType() {
        return type;
    }

    public Function<T, V> getGetter() {
        return getter;
    }

    public BiConsumer<T, V> getSetter() {
        return setter;
    }

    public boolean isEditable() {
        return editable;
    }

    public int getAlignment() {
        return alignment;
    }

    public int getWidth() {
        return width;
    }

    public static <T, V> Builder<T, V> builder() {
        return new Builder<>();
    }

    public static class Builder<T, V> {
        private String name;
        private Class<V> type;
        private Function<T, V> getter;
        private BiConsumer<T, V> setter;
        private boolean editable;
        private int alignment = SwingConstants.LEFT;
        private int width = 120;

        public Builder<T, V> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T, V> type(Class<V> type) {
            this.type = type;
            return this;
        }

        public Builder<T, V> getter(Function<T, V> getter) {
            this.getter = getter;
            return this;
        }

        public Builder<T, V> setter(BiConsumer<T, V> setter) {
            this.setter = setter;
            return this;
        }

        public Builder<T, V> editable(boolean editable) {
            this.editable = editable;
            return this;
        }

        public Builder<T, V> alignment(int alignment) {
            this.alignment = alignment;
            return this;
        }

        public Builder<T, V> width(int width) {
            this.width = width;
            return this;
        }

        public Column<T, V> build() {
            return new Column<>(this);
        }
    }
}