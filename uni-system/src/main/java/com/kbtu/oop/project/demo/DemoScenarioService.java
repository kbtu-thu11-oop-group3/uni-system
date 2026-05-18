package com.kbtu.oop.project.demo;

import com.kbtu.oop.project.model.common.NewsTopic;
import com.kbtu.oop.project.model.common.PaperSortType;
import com.kbtu.oop.project.model.common.RequestStatus;
import com.kbtu.oop.project.model.common.UrgencyLevel;
import com.kbtu.oop.project.model.research.ResearchPaper;
import com.kbtu.oop.project.service.AuthService;
import com.kbtu.oop.project.service.ComplaintService;
import com.kbtu.oop.project.service.CourseService;
import com.kbtu.oop.project.service.GradeService;
import com.kbtu.oop.project.service.ManagerService;
import com.kbtu.oop.project.service.MessageService;
import com.kbtu.oop.project.service.NewsService;
import com.kbtu.oop.project.service.ResearchService;
import com.kbtu.oop.project.service.SupportRequestService;
import com.kbtu.oop.project.service.TranscriptService;

import java.util.List;

public class DemoScenarioService {

    private final AuthService authService = new AuthService();
    private final CourseService courseService = new CourseService();
    private final GradeService gradeService = new GradeService();
    private final ManagerService managerService = new ManagerService();
    private final MessageService messageService = new MessageService();
    private final ComplaintService complaintService = new ComplaintService();
    private final SupportRequestService supportRequestService = new SupportRequestService();
    private final ResearchService researchService = new ResearchService();
    private final NewsService newsService = new NewsService();
    private final TranscriptService transcriptService = new TranscriptService();

    public void runAll() {
        authService.login("student@uni.local", "student123");
        authService.login("teacher@uni.local", "teacher123");

        var enrollment = courseService.registerForCourse(DemoIds.STUDENT_ID, DemoIds.COURSE_OOP_ID);
        managerService.approveRegistration(DemoIds.MANAGER_ID, enrollment.getId());
        gradeService.putMark(DemoIds.TEACHER_ID, DemoIds.STUDENT_ID, DemoIds.COURSE_OOP_ID, 28, 27, 35);
        courseService.rateTeacher(DemoIds.STUDENT_ID, DemoIds.TEACHER_ID, 5);

        messageService.sendEmployeeMessage(DemoIds.TEACHER_ID, DemoIds.MANAGER_ID,
                "Exam room booking", "Need room 301 for final exam.");
        complaintService.sendComplaint(DemoIds.TEACHER_ID, List.of(DemoIds.STUDENT_ID), UrgencyLevel.MEDIUM,
                "Attendance issue", "Student missed multiple practice lessons.");

        var request = supportRequestService.createRequest(DemoIds.TEACHER_ID,
                "Projector issue", "Projector in room B-201 is not working", "Projector", "B-201");
        supportRequestService.listNewRequests(DemoIds.SUPPORT_ID);
        supportRequestService.updateStatus(DemoIds.SUPPORT_ID, request.getId(), RequestStatus.ACCEPTED);
        supportRequestService.updateStatus(DemoIds.SUPPORT_ID, request.getId(), RequestStatus.DONE);

        researchService.subscribeToJournal(DemoIds.STUDENT_ID);
        researchService.assignSupervisor(DemoIds.GRAD_STUDENT_ID, DemoIds.TEACHER_ID);

        ResearchPaper paper = new ResearchPaper();
        paper.setTitle("University Scheduling Optimization");
        paper.setJournal("KBTU Research Journal");
        paper.setAuthors(List.of("Talgat Professor"));
        paper.setPages(11);
        paper.setDoi("10.1000/kbtu.2026.099");
        paper.setPublicationDate(java.time.LocalDate.now());
        paper.setCitations(1);
        var published = researchService.publishPaper(DemoIds.TEACHER_ID, paper);

        newsService.announcePublishedPaper(DemoIds.TEACHER_ID, published.getId());
        newsService.publishNews(DemoIds.MANAGER_ID, "Registration opened", "Course registration is now open",
                NewsTopic.GENERAL);
        newsService.generateTopCitedResearcherNews(java.time.Year.now().getValue());

        var orderedPapers = researchService.printAllPapers(PaperSortType.BY_CITATIONS);
        var transcript = transcriptService.getTranscript(DemoIds.STUDENT_ID);

        System.out.println("Demo completed.");
        System.out.println("Papers sorted by citations: " + orderedPapers.size());
        System.out.println("Transcript marks count: " + transcript.getMarkIds().size());
        System.out.println("Support request final status: " + RequestStatus.DONE);
    }
}