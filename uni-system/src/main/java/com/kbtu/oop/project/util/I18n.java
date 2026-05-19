package com.kbtu.oop.project.util;

import javax.swing.SwingUtilities;
import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class I18n {

    private static Locale locale = Locale.ENGLISH;

    private static ResourceBundle bundle = loadBundle(locale);

    private static final List<Runnable> listeners = new ArrayList<>();

    private I18n() {
    }

    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        bundle = loadBundle(locale);

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

    private static ResourceBundle loadBundle(Locale requestedLocale) {
        try {
            return ResourceBundle.getBundle("i18n.messages", requestedLocale);
        } catch (Exception ignored) {
        }

        String suffix = switch (requestedLocale.getLanguage()) {
            case "ru" -> "ru";
            case "kk" -> "kk";
            default -> "en";
        };
        Path path = Paths.get("src", "main", "resources", "i18n", "messages_" + suffix + ".properties");
        if (!Files.exists(path)) {
            throw new IllegalStateException("I18n bundle not found in classpath and filesystem: " + path);
        }

        try (InputStream inputStream = Files.newInputStream(path)) {
            return new PropertyResourceBundle(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load i18n bundle from " + path, exception);
        }
    }
}
