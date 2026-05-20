package com.kbtu.oop.project.ui.app.roles;

import com.kbtu.oop.project.model.user.TechSupportSpecialist;
import com.kbtu.oop.project.ui.app.UiContext;
import com.kbtu.oop.project.ui.app.panels.MessagesPanel;
import com.kbtu.oop.project.ui.app.panels.NewsPanel;
import com.kbtu.oop.project.ui.app.panels.SupportRequestsPanel;
import com.kbtu.oop.project.util.I18n;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class SupportPanel extends JPanel {
    private final JTabbedPane tabs = new JTabbedPane();

    public SupportPanel(UiContext context, TechSupportSpecialist specialist) {
        setLayout(new BorderLayout());

        tabs.addTab("", new SupportRequestsPanel(context.userService, context.supportRequestService, specialist.getId()));
        tabs.addTab("", new NewsPanel(context.newsService, context.researchService, specialist.getId()));
        tabs.addTab("", new MessagesPanel(context.messageService, context.userService, specialist.getId()));
        applyTranslations();
        I18n.addListener(this::applyTranslations);

        add(tabs, BorderLayout.CENTER);
    }

    private void applyTranslations() {
        tabs.setTitleAt(0, I18n.get("tab.supportRequests"));
        tabs.setTitleAt(1, I18n.get("tab.news"));
        tabs.setTitleAt(2, I18n.get("tab.messages"));
    }
}
