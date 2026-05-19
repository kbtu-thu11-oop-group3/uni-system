package com.kbtu.oop.project.ui.app;

import com.kbtu.oop.project.model.common.Language;
import com.kbtu.oop.project.model.user.*;
import com.kbtu.oop.project.ui.app.roles.*;
import com.kbtu.oop.project.util.I18n;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class RolePanel extends JPanel {

    private final JLabel userLabel = new JLabel();

    private final JButton profileButton = new JButton();

    private final JButton logoutButton = new JButton();

    private final JComboBox<String> languageBox = new JComboBox<>();

    private final User user;

    public RolePanel(UiContext context, User user, Runnable onLogout) {

        this.user = user;

        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());

        JPanel actions = new JPanel();

        profileButton.addActionListener(
                event -> UiDialogs.showUserProfile(this, user));

        logoutButton.addActionListener(
                event -> onLogout.run());

        setupLanguageSelector(context);

        actions.add(languageBox);
        actions.add(profileButton);
        actions.add(logoutButton);

        header.add(userLabel, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        add(createRolePanel(context, user), BorderLayout.CENTER);

        applyTranslations();

        I18n.addListener(this::applyTranslations);
    }

    private void setupLanguageSelector(UiContext context) {

        languageBox.addItem("EN");
        languageBox.addItem("RU");
        languageBox.addItem("KZ");

        switch (user.getLanguage()) {
            case RU -> languageBox.setSelectedItem("RU");
            case KZ -> languageBox.setSelectedItem("KZ");
            default -> languageBox.setSelectedItem("EN");
        }

        languageBox.addActionListener(e -> {

            String selected = (String) languageBox.getSelectedItem();

            if (selected == null)
                return;

            switch (selected) {

                case "RU" -> {
                    I18n.setLocale(new Locale("ru"));
                    user.setLanguage(Language.RU);
                }

                case "KZ" -> {
                    I18n.setLocale(new Locale("kk"));
                    user.setLanguage(Language.KZ);
                }

                default -> {
                    I18n.setLocale(Locale.ENGLISH);
                    user.setLanguage(Language.EN);
                }
            }

            context.userService.updateUser(user);
        });
    }

    private void applyTranslations() {

        userLabel.setText(
                I18n.get("role.loggedInAs")
                        + " "
                        + user.getFullName());

        profileButton.setText(
                I18n.get("role.profile"));

        logoutButton.setText(
                I18n.get("role.logout"));
    }

    private JPanel createRolePanel(
            UiContext context,
            User user) {

        if (user instanceof Admin admin) {
            return new AdminPanel(context, admin);
        }

        if (user instanceof Manager manager) {
            return new ManagerPanel(context, manager);
        }

        if (user instanceof Teacher teacher) {
            return new TeacherPanel(context, teacher);
        }

        if (user instanceof GraduateStudent graduateStudent) {
            return new StudentPanel(context, graduateStudent, true);
        }

        if (user instanceof Student student) {
            return new StudentPanel(context, student, false);
        }

        if (user instanceof TechSupportSpecialist specialist) {
            return new SupportPanel(context, specialist);
        }

        JPanel panel = new JPanel();

        panel.add(new JLabel("Unsupported user role"));

        return panel;
    }
}