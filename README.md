# University System

Desktop university management system (Java 17, Swing, Maven) developed as a team project.

## Team

| #   | Full Name         | Role      | GitHub                                    |
| --- | ----------------- | --------- | ----------------------------------------- |
| 1   | Nurgali Nursultan | Team Lead | [aablty](https://github.com/aablty)       |
| 2   | Aimurzayev Alan   | Member    | [baalausam](https://github.com/baalausam) |
| 3   | Makmut Balaussa   | Member    | [Ne1vAlan](https://github.com/Ne1vAlan)   |

## Project Scope

- User roles: Admin, Manager, Teacher, Student, Graduate Student, Support.
- Academic workflows: courses, enrollments, grades, transcript, reports.
- Schedule & lessons: lesson assignment, picking lessons, collision checks, lesson journal.
- Research workflows: papers, projects, journals, subscriptions, announcements.
- Communication: news, comments, messages, support requests, complaints.

## Tech Stack

- Java 17
- Swing (desktop UI)
- Maven
- Jackson (JSON persistence)
- Lombok

## Run

From `uni-system/`:

```bash
mvn compile
mvn exec:java "-Dexec.mainClass=com.kbtu.oop.project.App"
```

## Data Storage

Persistence is file-based JSON.

Path:

- `uni-system/src/main/resources/data/`

Examples:

- `users.json`
- `courses.json`
- `enrollments.json`
- `grades.json`
- `lessons.json`
- `schedules.json`
- `lesson-journal.json`
- `news.json`
- `support-requests.json`
