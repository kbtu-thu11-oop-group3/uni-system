package com.kbtu.oop.project.ui.app.roles;

import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.ui.app.UiContext;
import com.kbtu.oop.project.ui.app.panels.ComplaintsPanel;
import com.kbtu.oop.project.ui.app.panels.CoursesPanel;
import com.kbtu.oop.project.ui.app.panels.EmployeeSupportRequestsPanel;
import com.kbtu.oop.project.ui.app.panels.GradesPanel;
import com.kbtu.oop.project.ui.app.panels.MessagesPanel;
import com.kbtu.oop.project.ui.app.panels.NewsPanel;
import com.kbtu.oop.project.ui.app.panels.StudentsPanel;
import com.kbtu.oop.project.ui.app.panels.ResearchPanel;
import com.kbtu.oop.project.util.I18n;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class TeacherPanel extends JPanel {
    private final JTabbedPane tabs = new JTabbedPane();
    private final boolean showResearchTab;

    public TeacherPanel(UiContext context, Teacher teacher) {
        setLayout(new BorderLayout());
        showResearchTab = context.researchService.isResearcher(teacher.getId());

        tabs.addTab("", new CoursesPanel(context.courseService, context.managerService, teacher.getId(), false,
            teacher.getId(), true));
        tabs.addTab("", new GradesPanel(context.gradeService, context.courseService,
            context.userService, teacher.getId()));
        tabs.addTab("", new StudentsPanel(context.userService));
        tabs.addTab("", new ComplaintsPanel(context.complaintService, teacher.getId()));
        tabs.addTab("", new NewsPanel(context.newsService, context.researchService, teacher.getId()));
        tabs.addTab("", new MessagesPanel(context.messageService, teacher.getId()));
        tabs.addTab("", new EmployeeSupportRequestsPanel(context.supportRequestService, teacher.getId()));

        if (showResearchTab) {
            tabs.addTab("", new ResearchPanel(context.researchService, teacher.getId()));
        }
        applyTranslations();
        I18n.addListener(this::applyTranslations);

        add(tabs, BorderLayout.CENTER);
    }

    private void applyTranslations() {
        tabs.setTitleAt(0, I18n.get("tab.courses"));
        tabs.setTitleAt(1, I18n.get("tab.grades"));
        tabs.setTitleAt(2, I18n.get("tab.students"));
        tabs.setTitleAt(3, I18n.get("tab.complaints"));
        tabs.setTitleAt(4, I18n.get("tab.news"));
        tabs.setTitleAt(5, I18n.get("tab.messages"));
        tabs.setTitleAt(6, I18n.get("tab.supportRequests"));
        if (showResearchTab) {
            tabs.setTitleAt(7, I18n.get("tab.research"));
        }
    }
}
