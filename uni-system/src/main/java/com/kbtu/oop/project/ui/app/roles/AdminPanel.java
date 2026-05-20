package com.kbtu.oop.project.ui.app.roles;

import com.kbtu.oop.project.model.user.Admin;
import com.kbtu.oop.project.ui.app.UiContext;
import com.kbtu.oop.project.ui.app.panels.EmployeeSupportRequestsPanel;
import com.kbtu.oop.project.ui.app.panels.LogsPanel;
import com.kbtu.oop.project.ui.app.panels.MessagesPanel;
import com.kbtu.oop.project.ui.app.panels.NewsPanel;
import com.kbtu.oop.project.ui.app.panels.UsersPanel;
import com.kbtu.oop.project.util.I18n;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class AdminPanel extends JPanel {
    private final JTabbedPane tabs = new JTabbedPane();

    public AdminPanel(UiContext context, Admin admin) {
        setLayout(new BorderLayout());

        tabs.addTab("", new UsersPanel(context.userService, admin.getId()));
        tabs.addTab("", new LogsPanel());
        tabs.addTab("", new NewsPanel(context.newsService, context.researchService, admin.getId()));
        tabs.addTab("", new MessagesPanel(context.messageService, context.userService, admin.getId()));
        tabs.addTab("", new EmployeeSupportRequestsPanel(context.supportRequestService, admin.getId()));
        applyTranslations();
        I18n.addListener(this::applyTranslations);

        add(tabs, BorderLayout.CENTER);
    }

    private void applyTranslations() {
        tabs.setTitleAt(0, I18n.get("tab.users"));
        tabs.setTitleAt(1, I18n.get("tab.logs"));
        tabs.setTitleAt(2, I18n.get("tab.news"));
        tabs.setTitleAt(3, I18n.get("tab.messages"));
        tabs.setTitleAt(4, I18n.get("tab.supportRequests"));
    }
}
