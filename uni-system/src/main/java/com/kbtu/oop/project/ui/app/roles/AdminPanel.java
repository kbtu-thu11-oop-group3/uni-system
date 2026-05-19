package com.kbtu.oop.project.ui.app.roles;

import com.kbtu.oop.project.model.user.Admin;
import com.kbtu.oop.project.ui.app.UiContext;
import com.kbtu.oop.project.ui.app.panels.EmployeeSupportRequestsPanel;
import com.kbtu.oop.project.ui.app.panels.LogsPanel;
import com.kbtu.oop.project.ui.app.panels.MessagesPanel;
import com.kbtu.oop.project.ui.app.panels.NewsPanel;
import com.kbtu.oop.project.ui.app.panels.UsersPanel;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class AdminPanel extends JPanel {

    public AdminPanel(UiContext context, Admin admin) {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Users", new UsersPanel(context.userService, admin.getId()));
        tabs.addTab("Logs", new LogsPanel());
        tabs.addTab("News", new NewsPanel(context.newsService));
        tabs.addTab("Messages", new MessagesPanel(context.messageService, admin.getId()));
        tabs.addTab("Support Requests", new EmployeeSupportRequestsPanel(context.supportRequestService, admin.getId()));

        add(tabs, BorderLayout.CENTER);
    }
}
