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

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class TeacherPanel extends JPanel {

    public TeacherPanel(UiContext context, Teacher teacher) {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Courses", new CoursesPanel(context.courseService, context.managerService, teacher.getId(), false,
            teacher.getId(), true));
        tabs.addTab("Grades", new GradesPanel(context.gradeService, context.courseService,
            context.userService, teacher.getId()));
        tabs.addTab("Students", new StudentsPanel(context.userService));
        tabs.addTab("Complaints", new ComplaintsPanel(context.complaintService, teacher.getId()));
        tabs.addTab("News", new NewsPanel(context.newsService));
        tabs.addTab("Messages", new MessagesPanel(context.messageService, teacher.getId()));
        tabs.addTab("Support Requests", new EmployeeSupportRequestsPanel(context.supportRequestService, teacher.getId()));

        if (context.researchService.isResearcher(teacher.getId())) {
            tabs.addTab("Research", new ResearchPanel(context.researchService, teacher.getId()));
        }

        add(tabs, BorderLayout.CENTER);
    }
}
