package com.kbtu.oop.project.ui.app.roles;

import com.kbtu.oop.project.model.user.TechSupportSpecialist;
import com.kbtu.oop.project.ui.app.UiContext;
import com.kbtu.oop.project.ui.app.panels.MessagesPanel;
import com.kbtu.oop.project.ui.app.panels.NewsPanel;
import com.kbtu.oop.project.ui.app.panels.SupportRequestsPanel;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class SupportPanel extends JPanel {

    public SupportPanel(UiContext context, TechSupportSpecialist specialist) {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Support Requests", new SupportRequestsPanel(context.userService, context.supportRequestService, specialist.getId()));
        tabs.addTab("News", new NewsPanel(context.newsService));
        tabs.addTab("Messages", new MessagesPanel(context.messageService, specialist.getId()));

        add(tabs, BorderLayout.CENTER);
    }
}
