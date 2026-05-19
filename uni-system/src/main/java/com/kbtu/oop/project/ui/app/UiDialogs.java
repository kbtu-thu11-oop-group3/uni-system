package com.kbtu.oop.project.ui.app;

import com.kbtu.oop.project.model.communication.News;
import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.model.user.Employee;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.User;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

public final class UiDialogs {

    private UiDialogs() {
    }

    public static void showError(JPanel parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(JPanel parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showUserProfile(JPanel parent, User user) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Name: " + user.getFullName());
        joiner.add("Username: " + safe(user.getUsername()));
        joiner.add("Email: " + safe(user.getEmail()));
        joiner.add("Active: " + user.isActive());
        if (user instanceof Employee employee) {
            joiner.add("Employee Code: " + safe(employee.getEmployeeCode()));
            joiner.add("Department: " + safe(employee.getDepartment()));
        }
        if (user instanceof Teacher teacher) {
            joiner.add("Position: " + teacher.getPosition());
            joiner.add("Average Rating: " + teacher.getAverageRating());
        }
        if (user instanceof Student student) {
            joiner.add("Student Code: " + safe(student.getStudentCode()));
            joiner.add("School: " + (student.getSchool() != null ? student.getSchool().name() : ""));
            joiner.add("Year: " + student.getYearOfStudy());
            joiner.add("Credits: " + student.getCredits());
            joiner.add("GPA: " + student.getGpa());
        }

        JOptionPane.showMessageDialog(parent, joiner.toString(), "Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showCourseDetails(JPanel parent, Course course, List<Teacher> teachers) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Code: " + safe(course.getCode()));
        joiner.add("Title: " + safe(course.getTitle()));
        joiner.add("Credits: " + course.getCredits());
        joiner.add("Type: " + course.getCourseType());
        joiner.add("Description: " + safe(course.getDescription()));
        if (teachers != null && !teachers.isEmpty()) {
            String list = teachers.stream()
                    .map(t -> t.getFullName())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            joiner.add("Teachers: " + list);
        }
        JOptionPane.showMessageDialog(parent, joiner.toString(), "Course", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showNewsDetails(JPanel parent, News news) {
        StringJoiner joiner = new StringJoiner("\n\n");
        joiner.add(news.getTitle());
        joiner.add(news.getContent());
        String meta = "Topic: " + news.getTopic().name();
        if (news.isPinned()) {
            meta += " | PINNED";
        }
        if (news.getCreatedAt() != null) {
            meta += " | " + news.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        }
        joiner.add(meta);
        JOptionPane.showMessageDialog(parent, joiner.toString(), "News", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
