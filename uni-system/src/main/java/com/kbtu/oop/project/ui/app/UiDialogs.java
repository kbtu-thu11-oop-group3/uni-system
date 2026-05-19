package com.kbtu.oop.project.ui.app;

import com.kbtu.oop.project.model.communication.News;
import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.model.user.Employee;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.util.I18n;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

public final class UiDialogs {

    private UiDialogs() {
    }

    public static void showError(JPanel parent, String message) {
        JOptionPane.showMessageDialog(parent, message, I18n.get("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(JPanel parent, String message) {
        JOptionPane.showMessageDialog(parent, message, I18n.get("dialog.info.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showUserProfile(JPanel parent, User user) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(I18n.get("dialog.profile.name") + " " + user.getFullName());
        joiner.add(I18n.get("dialog.profile.username") + " " + safe(user.getUsername()));
        joiner.add(I18n.get("dialog.profile.email") + " " + safe(user.getEmail()));
        joiner.add(I18n.get("dialog.profile.active") + " " + user.isActive());
        if (user instanceof Employee employee) {
            joiner.add(I18n.get("dialog.profile.employeeCode") + " " + safe(employee.getEmployeeCode()));
            joiner.add(I18n.get("dialog.profile.department") + " " + safe(employee.getDepartment()));
        }
        if (user instanceof Teacher teacher) {
            joiner.add(I18n.get("dialog.profile.position") + " " + teacher.getPosition());
            joiner.add(I18n.get("dialog.profile.averageRating") + " " + teacher.getAverageRating());
        }
        if (user instanceof Student student) {
            joiner.add(I18n.get("dialog.profile.studentCode") + " " + safe(student.getStudentCode()));
            joiner.add(I18n.get("dialog.profile.school") + " " + (student.getSchool() != null ? student.getSchool().name() : ""));
            joiner.add(I18n.get("dialog.profile.year") + " " + student.getYearOfStudy());
            joiner.add(I18n.get("dialog.profile.credits") + " " + student.getCredits());
            joiner.add(I18n.get("dialog.profile.gpa") + " " + student.getGpa());
        }

        JOptionPane.showMessageDialog(parent, joiner.toString(), I18n.get("dialog.profile.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showCourseDetails(JPanel parent, Course course, List<Teacher> teachers) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(I18n.get("dialog.course.code") + " " + safe(course.getCode()));
        joiner.add(I18n.get("dialog.course.title") + " " + safe(course.getTitle()));
        joiner.add(I18n.get("dialog.course.credits") + " " + course.getCredits());
        joiner.add(I18n.get("dialog.course.type") + " " + course.getCourseType());
        joiner.add(I18n.get("dialog.course.description") + " " + safe(course.getDescription()));
        if (teachers != null && !teachers.isEmpty()) {
            String list = teachers.stream()
                    .map(t -> t.getFullName())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            joiner.add(I18n.get("dialog.course.teachers") + " " + list);
        }
        JOptionPane.showMessageDialog(parent, joiner.toString(), I18n.get("dialog.course.titleBar"), JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showNewsDetails(JPanel parent, News news) {
        StringJoiner joiner = new StringJoiner("\n\n");
        joiner.add(news.getTitle());
        joiner.add(news.getContent());
        String meta = I18n.get("dialog.news.topic") + " " + news.getTopic().name();
        if (news.isPinned()) {
            meta += " | " + I18n.get("dialog.news.pinned");
        }
        if (news.getCreatedAt() != null) {
            meta += " | " + news.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        }
        joiner.add(meta);
        JOptionPane.showMessageDialog(parent, joiner.toString(), I18n.get("dialog.news.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
