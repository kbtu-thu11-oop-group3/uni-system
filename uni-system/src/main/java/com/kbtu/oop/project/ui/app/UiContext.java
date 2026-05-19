package com.kbtu.oop.project.ui.app;

import com.kbtu.oop.project.service.AuthService;
import com.kbtu.oop.project.service.ComplaintService;
import com.kbtu.oop.project.service.CourseService;
import com.kbtu.oop.project.service.GradeService;
import com.kbtu.oop.project.service.ManagerService;
import com.kbtu.oop.project.service.MessageService;
import com.kbtu.oop.project.service.NewsService;
import com.kbtu.oop.project.service.StudentOrganizationService;
import com.kbtu.oop.project.service.SupportRequestService;
import com.kbtu.oop.project.service.TranscriptService;
import com.kbtu.oop.project.service.UserService;
import com.kbtu.oop.project.service.ResearchService;

public class UiContext {

    public final AuthService authService = new AuthService();
    public final UserService userService = new UserService();
    public final CourseService courseService = new CourseService();
    public final GradeService gradeService = new GradeService();
    public final ManagerService managerService = new ManagerService();
    public final NewsService newsService = new NewsService();
    public final ResearchService researchService = new ResearchService();
    public final MessageService messageService = new MessageService();
    public final SupportRequestService supportRequestService = new SupportRequestService();
    public final ComplaintService complaintService = new ComplaintService();
    public final TranscriptService transcriptService = new TranscriptService();
    public final StudentOrganizationService studentOrganizationService = new StudentOrganizationService();
}
