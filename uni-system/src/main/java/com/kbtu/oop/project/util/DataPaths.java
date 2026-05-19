package com.kbtu.oop.project.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class DataPaths {

    private static final Path DATA_DIR = Paths.get("src", "main", "resources", "data");

    private DataPaths() {
    }

    public static Path usersPath() {
        return DATA_DIR.resolve("users.json");
    }

    public static Path coursesPath() {
        return DATA_DIR.resolve("courses.json");
    }

    public static Path gradesPath() {
        return DATA_DIR.resolve("grades.json");
    }

    public static Path enrollmentsPath() {
        return DATA_DIR.resolve("enrollments.json");
    }

    public static Path researchPapersPath() {
        return DATA_DIR.resolve("research-papers.json");
    }

    public static Path researchProjectsPath() {
        return DATA_DIR.resolve("research-projects.json");
    }

    public static Path researchJournalsPath() {
        return DATA_DIR.resolve("research-journals.json");
    }

    public static Path researcherProfilesPath() {
        return DATA_DIR.resolve("researcher-profiles.json");
    }

    public static Path newsPath() {
        return DATA_DIR.resolve("news.json");
    }

    public static Path actionLogPath() {
        return DATA_DIR.resolve("action-log.json");
    }

    public static Path messagesPath() {
        return DATA_DIR.resolve("messages.json");
    }

    public static Path complaintsPath() {
        return DATA_DIR.resolve("complaints.json");
    }

    public static Path supportRequestsPath() {
        return DATA_DIR.resolve("support-requests.json");
    }

    public static Path newsCommentsPath() {
        return DATA_DIR.resolve("news-comments.json");
    }

    public static Path studentOrganizationsPath() {
        return DATA_DIR.resolve("student-organizations.json");
    }
}