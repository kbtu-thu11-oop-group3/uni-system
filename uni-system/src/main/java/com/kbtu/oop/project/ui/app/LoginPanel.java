package com.kbtu.oop.project.ui.app;

import com.kbtu.oop.project.exception.AuthException;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.util.I18n;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.function.Consumer;

public class LoginPanel extends JPanel {

    private final UiContext context;
    private final Consumer<User> onLoginSuccess;

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    private final JLabel usernameLabel = new JLabel();
    private final JLabel passwordLabel = new JLabel();
    private final JLabel errorLabel = new JLabel(" ");

    private final JButton loginButton = new JButton();

    private final JComboBox<String> languageBox = new JComboBox<>();

    public LoginPanel(UiContext context, Consumer<User> onLoginSuccess) {

        this.context = context;
        this.onLoginSuccess = onLoginSuccess;

        buildUi();
        applyTranslations();

        setupLanguageSelector();

        I18n.addListener(this::applyTranslations);
    }

    private void buildUi() {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        langPanel.add(languageBox);

        add(langPanel, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;

        add(usernameLabel, gbc);

        gbc.gridx = 1;

        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;

        add(passwordLabel, gbc);

        gbc.gridx = 1;

        add(passwordField, gbc);

        loginButton.addActionListener(event -> login());

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;

        add(loginButton, gbc);

        gbc.gridy = 4;

        errorLabel.setForeground(Color.RED);

        add(errorLabel, gbc);
    }

    private void setupLanguageSelector() {

        languageBox.addItem("EN");
        languageBox.addItem("RU");
        languageBox.addItem("KZ");

        languageBox.setSelectedItem("EN");

        languageBox.addActionListener(e -> {

            String selected = (String) languageBox.getSelectedItem();

            if (selected == null)
                return;

            switch (selected) {

                case "RU" ->
                    I18n.setLocale(new Locale("ru"));

                case "KZ" ->
                    I18n.setLocale(new Locale("kk"));

                default ->
                    I18n.setLocale(Locale.ENGLISH);
            }
        });
    }

    private void applyTranslations() {

        usernameLabel.setText(
                I18n.get("login.username"));

        passwordLabel.setText(
                I18n.get("login.password"));

        loginButton.setText(
                I18n.get("login.login"));
    }

    private void login() {

        String username = usernameField.getText().trim();

        String password = new String(passwordField.getPassword());

        try {

            User user = context.authService.login(username, password);

            applyUserLanguage(user);

            errorLabel.setText(" ");

            onLoginSuccess.accept(user);

        } catch (AuthException exception) {

            errorLabel.setText(exception.getMessage());
        }
    }

    private void applyUserLanguage(User user) {

        switch (user.getLanguage()) {

            case RU -> {
                I18n.setLocale(new Locale("ru"));
                languageBox.setSelectedItem("RU");
            }

            case KZ -> {
                I18n.setLocale(new Locale("kk"));
                languageBox.setSelectedItem("KZ");
            }

            default -> {
                I18n.setLocale(Locale.ENGLISH);
                languageBox.setSelectedItem("EN");
            }
        }
    }

    public void reset() {

        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
    }
}