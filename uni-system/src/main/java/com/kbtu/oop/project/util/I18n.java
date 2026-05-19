package com.kbtu.oop.project.util;

import javax.swing.SwingUtilities;
import java.awt.Window;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {

    private static Locale locale = Locale.ENGLISH;

    private static ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);

    private static final List<Runnable> listeners = new ArrayList<>();

    private I18n() {
    }

    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        bundle = ResourceBundle.getBundle("i18n.messages", locale);

        List<Runnable> snapshot = new ArrayList<>(listeners);
        for (Runnable listener : snapshot) {
            listener.run();
        }

        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
    }

    public static Locale getLocale() {
        return locale;
    }

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    public static String getf(String key, Object... args) {
        return MessageFormat.format(get(key), args);
    }

    public static void addListener(Runnable listener) {
        listeners.add(listener);
    }
}
