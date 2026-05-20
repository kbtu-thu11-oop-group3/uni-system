package com.kbtu.oop.project.ui.app.roles;

import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.ui.app.UiContext;
import com.kbtu.oop.project.ui.app.panels.ComplaintsManagementPanel;
import com.kbtu.oop.project.ui.app.panels.CoursesPanel;
import com.kbtu.oop.project.ui.app.panels.EmployeeSupportRequestsPanel;
import com.kbtu.oop.project.ui.app.panels.EnrollmentsPanel;
import com.kbtu.oop.project.ui.app.panels.MessagesPanel;
import com.kbtu.oop.project.ui.app.panels.NewsManagementPanel;
import com.kbtu.oop.project.ui.app.panels.NewsPanel;
import com.kbtu.oop.project.ui.app.panels.OrganizationsManagementPanel;
import com.kbtu.oop.project.ui.app.panels.ReportsPanel;
import com.kbtu.oop.project.ui.app.panels.ResearchJournalsManagementPanel;
import com.kbtu.oop.project.ui.app.panels.LessonsManagementPanel;
import com.kbtu.oop.project.util.I18n;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class ManagerPanel extends JPanel {
    private final JTabbedPane tabs = new JTabbedPane();

    public ManagerPanel(UiContext context, Manager manager) {
        setLayout(new BorderLayout());

        tabs.addTab("",
                new CoursesPanel(context.courseService, context.managerService, manager.getId(), true, null, false));
        tabs.addTab("", new EnrollmentsPanel(context.managerService, context.userService, context.courseService,
                manager.getId()));
        tabs.addTab("", new NewsManagementPanel(context.newsService, manager.getId()));
        tabs.addTab("", new NewsPanel(context.newsService, context.researchService, manager.getId()));
        tabs.addTab("", new ReportsPanel(context.gradeService, context.courseService));
        tabs.addTab("", new ComplaintsManagementPanel(context.complaintService, manager.getId()));
        tabs.addTab("", new OrganizationsManagementPanel(context.studentOrganizationService, manager.getId()));
        tabs.addTab("", new MessagesPanel(context.messageService, context.userService, manager.getId()));
        tabs.addTab("", new EmployeeSupportRequestsPanel(context.supportRequestService, manager.getId()));
        tabs.addTab("", new ResearchJournalsManagementPanel(context.researchService, manager.getId()));
        tabs.addTab("", new LessonsManagementPanel(context.scheduleService, context.courseService, context.userService,
                manager.getId()));
        applyTranslations();
        I18n.addListener(this::applyTranslations);

        add(tabs, BorderLayout.CENTER);
    }

    private void applyTranslations() {
        tabs.setTitleAt(0, I18n.get("tab.courses"));
        tabs.setTitleAt(1, I18n.get("tab.enrollments"));
        tabs.setTitleAt(2, I18n.get("tab.news"));
        tabs.setTitleAt(3, I18n.get("tab.newsFeed"));
        tabs.setTitleAt(4, I18n.get("tab.reports"));
        tabs.setTitleAt(5, I18n.get("tab.complaints"));
        tabs.setTitleAt(6, I18n.get("tab.organizations"));
        tabs.setTitleAt(7, I18n.get("tab.messages"));
        tabs.setTitleAt(8, I18n.get("tab.supportRequests"));
        tabs.setTitleAt(9, I18n.get("tab.journals"));
        tabs.setTitleAt(10, I18n.get("tab.lessons"));
    }
}
