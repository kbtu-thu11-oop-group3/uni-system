package com.kbtu.oop.project.ui.app.roles;

import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.ui.app.UiContext;
import com.kbtu.oop.project.ui.app.panels.NewsPanel;
import com.kbtu.oop.project.ui.app.panels.OrganizationsPanel;
import com.kbtu.oop.project.ui.app.panels.StudentCoursesPanel;
import com.kbtu.oop.project.ui.app.panels.StudentTeachersPanel;
import com.kbtu.oop.project.ui.app.panels.StudentTranscriptPanel;
import com.kbtu.oop.project.ui.app.panels.ResearchPanel;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class StudentPanel extends JPanel {

    public StudentPanel(UiContext context, Student student, boolean isGraduate) {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
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

        tabs.addTab("Courses", coursesPanel);
        tabs.addTab("Teachers", teachersPanel);
        tabs.addTab("Transcript",
            new StudentTranscriptPanel(context.gradeService, context.courseService, student.getId()));
        tabs.addTab("Organizations", new OrganizationsPanel(context.studentOrganizationService, student.getId()));
        tabs.addTab("News", new NewsPanel(context.newsService));

        // Research tab for graduates and eligible researchers
        if (isGraduate || context.researchService.isResearcher(student.getId())) {
            tabs.addTab("Research", new ResearchPanel(context.researchService, student.getId()));
        }

        add(tabs, BorderLayout.CENTER);
    }
}
