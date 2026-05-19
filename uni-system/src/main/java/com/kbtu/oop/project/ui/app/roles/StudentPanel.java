package com.kbtu.oop.project.ui.app.roles;

import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.ui.app.UiContext;
import com.kbtu.oop.project.ui.app.panels.NewsPanel;
import com.kbtu.oop.project.ui.app.panels.OrganizationsPanel;
import com.kbtu.oop.project.ui.app.panels.StudentCoursesPanel;
import com.kbtu.oop.project.ui.app.panels.StudentTeachersPanel;
import com.kbtu.oop.project.ui.app.panels.StudentTranscriptPanel;
import com.kbtu.oop.project.ui.app.panels.ResearchPanel;
import com.kbtu.oop.project.ui.app.panels.StudentLessonsPanel;
import com.kbtu.oop.project.util.I18n;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class StudentPanel extends JPanel {
    private final JTabbedPane tabs = new JTabbedPane();
    private final boolean showResearchTab;

    public StudentPanel(UiContext context, Student student, boolean isGraduate) {
        setLayout(new BorderLayout());
        showResearchTab = isGraduate || context.researchService.isResearcher(student.getId());

        StudentTeachersPanel teachersPanel = new StudentTeachersPanel(context.courseService, context.userService,
            student.getId());
        StudentCoursesPanel coursesPanel = new StudentCoursesPanel(
            context.courseService,
            context.userService,
            student.getId(),
            courseId -> {
                teachersPanel.setCourseFilter(courseId);
                tabs.setSelectedComponent(teachersPanel);
            });

        tabs.addTab("", coursesPanel);
        tabs.addTab("", teachersPanel);
        tabs.addTab("",
            new StudentTranscriptPanel(context.gradeService, context.courseService, student.getId()));
        tabs.addTab("", new OrganizationsPanel(context.studentOrganizationService, student.getId()));
        tabs.addTab("", new NewsPanel(context.newsService, context.researchService, student.getId()));
        tabs.addTab("", new StudentLessonsPanel(context.scheduleService, context.courseService, student.getId()));

        // Research tab for graduates and eligible researchers
        if (showResearchTab) {
            tabs.addTab("", new ResearchPanel(context.researchService, student.getId()));
        }
        applyTranslations();
        I18n.addListener(this::applyTranslations);

        add(tabs, BorderLayout.CENTER);
    }

    private void applyTranslations() {
        tabs.setTitleAt(0, I18n.get("tab.courses"));
        tabs.setTitleAt(1, I18n.get("tab.teachers"));
        tabs.setTitleAt(2, I18n.get("tab.transcript"));
        tabs.setTitleAt(3, I18n.get("tab.organizations"));
        tabs.setTitleAt(4, I18n.get("tab.news"));
        tabs.setTitleAt(5, I18n.get("tab.lessons"));
        if (showResearchTab) {
            tabs.setTitleAt(6, I18n.get("tab.research"));
        }
    }
}
