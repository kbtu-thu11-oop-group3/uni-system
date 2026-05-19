package com.kbtu.oop.project.demo;

import com.kbtu.oop.project.model.common.CourseType;
import com.kbtu.oop.project.model.common.ManagerType;
import com.kbtu.oop.project.model.common.School;
import com.kbtu.oop.project.model.common.StudentType;
import com.kbtu.oop.project.model.common.TeacherPosition;
import com.kbtu.oop.project.model.course.Course;
import com.kbtu.oop.project.model.research.ResearchJournal;
import com.kbtu.oop.project.model.research.ResearchPaper;
import com.kbtu.oop.project.model.user.Admin;
import com.kbtu.oop.project.model.user.GraduateStudent;
import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.model.user.TechSupportSpecialist;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.util.DataPaths;
import com.kbtu.oop.project.util.JsonUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SeedDataService {

    public void resetAndSeed() {
        writeUsers();
        JsonUtil.writeCollection(DataPaths.coursesPath(), seedCourses());
        JsonUtil.writeCollection(DataPaths.researchPapersPath(), seedPapers());
        JsonUtil.writeCollection(DataPaths.researchJournalsPath(), seedJournals());
        JsonUtil.writeCollection(DataPaths.researcherProfilesPath(), new ArrayList<>());

        JsonUtil.writeCollection(DataPaths.gradesPath(), new ArrayList<>());
        JsonUtil.writeCollection(DataPaths.enrollmentsPath(), new ArrayList<>());
        JsonUtil.writeCollection(DataPaths.messagesPath(), new ArrayList<>());
        JsonUtil.writeCollection(DataPaths.complaintsPath(), new ArrayList<>());
        JsonUtil.writeCollection(DataPaths.supportRequestsPath(), new ArrayList<>());
        JsonUtil.writeCollection(DataPaths.newsPath(), new ArrayList<>());
        JsonUtil.writeCollection(DataPaths.newsCommentsPath(), new ArrayList<>());
        JsonUtil.writeCollection(DataPaths.researchProjectsPath(), new ArrayList<>());
        JsonUtil.writeCollection(DataPaths.actionLogPath(), new ArrayList<>());
    }

    private void writeUsers() {
        try {
            var users = seedUsers().toArray(User[]::new);
            java.nio.file.Files.createDirectories(DataPaths.usersPath().getParent());
            JsonUtil.mapper().writerFor(User[].class).writeValue(DataPaths.usersPath().toFile(), users);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Failed to write users seed", exception);
        }
    }

    private List<User> seedUsers() {
        Admin admin = new Admin();
        admin.setId(DemoIds.ADMIN_ID);
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setEmail("admin");
        admin.setPasswordHash("admin");
        admin.setDepartment("Administration");
        admin.setEmployeeCode("EMP-ADM-01");

        Manager manager = new Manager();
        manager.setId(DemoIds.MANAGER_ID);
        manager.setFirstName("Marat");
        manager.setLastName("Manager");
        manager.setEmail("manager@uni.local");
        manager.setPasswordHash("manager123");
        manager.setDepartment("OR Office");
        manager.setEmployeeCode("EMP-MNG-01");
        manager.setManagerType(ManagerType.OR);

        Teacher teacher = new Teacher();
        teacher.setId(DemoIds.TEACHER_ID);
        teacher.setFirstName("Talgat");
        teacher.setLastName("Professor");
        teacher.setEmail("teacher@uni.local");
        teacher.setPasswordHash("teacher123");
        teacher.setDepartment("SITE");
        teacher.setEmployeeCode("EMP-TCH-01");
        teacher.setPosition(TeacherPosition.PROFESSOR);
        teacher.getResearchPaperIds().add(DemoIds.PAPER_HIGH_ID);
        teacher.getResearchPaperIds().add(DemoIds.PAPER_MEDIUM_ID);
        teacher.getResearchPaperIds().add(DemoIds.PAPER_LOW_ID);

        Student student = new Student();
        student.setId(DemoIds.STUDENT_ID);
        student.setFirstName("Dana");
        student.setLastName("Student");
        student.setEmail("student@uni.local");
        student.setPasswordHash("student123");
        student.setStudentCode("STU-01");
        student.setSchool(School.SITE);
        student.setYearOfStudy(2);
        student.setCredits(0);
        student.setGpa(3.2);
        student.setFailedAttempts(0);
        student.setStudentType(StudentType.STUDENT);

        GraduateStudent graduateStudent = new GraduateStudent();
        graduateStudent.setId(DemoIds.GRAD_STUDENT_ID);
        graduateStudent.setFirstName("Aigerim");
        graduateStudent.setLastName("Graduate");
        graduateStudent.setEmail("grad@uni.local");
        graduateStudent.setPasswordHash("grad123");
        graduateStudent.setStudentCode("GRD-01");
        graduateStudent.setSchool(School.SITE);
        graduateStudent.setYearOfStudy(1);
        graduateStudent.setCredits(0);
        graduateStudent.setGpa(3.8);
        graduateStudent.setFailedAttempts(0);
        graduateStudent.setStudentType(StudentType.GRADUATE);
        graduateStudent.getResearchPaperIds().add(DemoIds.PAPER_MEDIUM_ID);

        TechSupportSpecialist support = new TechSupportSpecialist();
        support.setId(DemoIds.SUPPORT_ID);
        support.setFirstName("Ruslan");
        support.setLastName("Support");
        support.setEmail("support@uni.local");
        support.setPasswordHash("support123");
        support.setDepartment("IT Support");
        support.setEmployeeCode("EMP-IT-01");

        return List.of(admin, manager, teacher, student, graduateStudent, support);
    }

    private List<Course> seedCourses() {
        Course oop = new Course();
        oop.setId(DemoIds.COURSE_OOP_ID);
        oop.setCode("CS201");
        oop.setTitle("Object Oriented Programming");
        oop.setDescription("Core OOP course");
        oop.setCredits(5);
        oop.setCourseType(CourseType.MAJOR);
        oop.getInstructorIds().add(DemoIds.TEACHER_ID);

        Course db = new Course();
        db.setId(DemoIds.COURSE_DB_ID);
        db.setCode("CS250");
        db.setTitle("Databases");
        db.setDescription("Database systems and SQL");
        db.setCredits(4);
        db.setCourseType(CourseType.MINOR);

        return List.of(oop, db);
    }

    private List<ResearchPaper> seedPapers() {
        ResearchPaper high = new ResearchPaper();
        high.setId(DemoIds.PAPER_HIGH_ID);
        high.setTitle("Adaptive Learning in University Systems");
        high.setAuthors(List.of("Talgat Professor"));
        high.setJournal("KBTU Research Journal");
        high.setPages(12);
        high.setPublicationDate(LocalDate.now().minusMonths(2));
        high.setDoi("10.1000/kbtu.2026.001");
        high.setCitations(9);

        ResearchPaper medium = new ResearchPaper();
        medium.setId(DemoIds.PAPER_MEDIUM_ID);
        medium.setTitle("Retaking Courses and Performance Trends");
        medium.setAuthors(List.of("Talgat Professor", "Aigerim Graduate"));
        medium.setJournal("University Data Science Review");
        medium.setPages(9);
        medium.setPublicationDate(LocalDate.now().minusMonths(1));
        medium.setDoi("10.1000/kbtu.2026.002");
        medium.setCitations(4);

        ResearchPaper low = new ResearchPaper();
        low.setId(DemoIds.PAPER_LOW_ID);
        low.setTitle("Curriculum Adaptability Metrics");
        low.setAuthors(List.of("Talgat Professor"));
        low.setJournal("KBTU Applied Sciences");
        low.setPages(6);
        low.setPublicationDate(LocalDate.now().minusMonths(1));
        low.setDoi("10.1000/kbtu.2026.003");
        low.setCitations(3);

        return List.of(high, medium, low);
    }

    private List<ResearchJournal> seedJournals() {
        ResearchJournal journal = new ResearchJournal();
        journal.setName("KBTU Research Journal");
        journal.setDescription("University research journal");
        journal.setIssn("0000-0000");
        return List.of(journal);
    }
}