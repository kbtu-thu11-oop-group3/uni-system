package com.kbtu.oop.project.ui.app.roles;

import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.ui.app.UiContext;
import com.kbtu.oop.project.ui.app.panels.ComplaintsManagementPanel;
import com.kbtu.oop.project.ui.app.panels.CoursesPanel;
import com.kbtu.oop.project.ui.app.panels.EmployeeSupportRequestsPanel;
import com.kbtu.oop.project.ui.app.panels.EnrollmentsPanel;
import com.kbtu.oop.project.ui.app.panels.MessagesPanel;
import com.kbtu.oop.project.ui.app.panels.NewsManagementPanel;
import com.kbtu.oop.project.ui.app.panels.OrganizationsManagementPanel;
import com.kbtu.oop.project.ui.app.panels.ReportsPanel;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class ManagerPanel extends JPanel {

    public ManagerPanel(UiContext context, Manager manager) {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Courses", new CoursesPanel(context.courseService, context.managerService, manager.getId(), true, null, false));
        tabs.addTab("Enrollments", new EnrollmentsPanel(context.managerService, context.userService,
            context.courseService, manager.getId()));
        tabs.addTab("News", new NewsManagementPanel(context.newsService, manager.getId()));
        tabs.addTab("Reports", new ReportsPanel(context.gradeService, context.courseService));
        tabs.addTab("Complaints", new ComplaintsManagementPanel(context.complaintService, manager.getId()));
        tabs.addTab("Organizations", new OrganizationsManagementPanel(context.studentOrganizationService, manager.getId()));
        tabs.addTab("Messages", new MessagesPanel(context.messageService, manager.getId()));
        tabs.addTab("Support Requests", new EmployeeSupportRequestsPanel(context.supportRequestService, manager.getId()));

        add(tabs, BorderLayout.CENTER);
    }
}
