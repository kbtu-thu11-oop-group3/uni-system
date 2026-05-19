package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ResearchProjectException;
import com.kbtu.oop.project.exception.ResearchSupervisorException;
import com.kbtu.oop.project.model.common.PaperSortType;
import com.kbtu.oop.project.model.common.TeacherPosition;
import com.kbtu.oop.project.model.research.ResearchJournal;
import com.kbtu.oop.project.model.research.ResearchPaper;
import com.kbtu.oop.project.model.research.ResearchProject;
import com.kbtu.oop.project.model.research.ResearcherProfile;
import com.kbtu.oop.project.model.user.GraduateStudent;
import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.model.user.Researcher;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.TechSupportSpecialist;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.pattern.factory.PaperSortStrategyFactory;
import com.kbtu.oop.project.pattern.observer.JournalPublisher;
import com.kbtu.oop.project.pattern.observer.UserJournalSubscriber;
import com.kbtu.oop.project.repository.ResearchJournalRepository;
import com.kbtu.oop.project.repository.ResearchProjectRepository;
import com.kbtu.oop.project.repository.ResearchRepository;
import com.kbtu.oop.project.repository.ResearcherProfileRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonResearchJournalRepository;
import com.kbtu.oop.project.repository.impl.JsonResearchProjectRepository;
import com.kbtu.oop.project.repository.impl.JsonResearchRepository;
import com.kbtu.oop.project.repository.impl.JsonResearcherProfileRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;
import com.kbtu.oop.project.util.I18n;

import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

public class ResearchService {

    private final ResearchRepository researchRepository;
    private final ResearchProjectRepository researchProjectRepository;
    private final ResearchJournalRepository researchJournalRepository;
    private final ResearcherProfileRepository researcherProfileRepository;
    private final UserRepository userRepository;
    private final ActionLogger actionLogger;
    private final JournalPublisher journalPublisher;
    private final NewsService newsService;

    public ResearchService() {
        this(new JsonResearchRepository(), new JsonResearchProjectRepository(), new JsonResearchJournalRepository(),
                new JsonResearcherProfileRepository(), new JsonUserRepository(), ActionLogger.getInstance(),
                new JournalPublisher(), new NewsService());
    }

    public ResearchService(ResearchRepository researchRepository,
            ResearchProjectRepository researchProjectRepository,
            ResearchJournalRepository researchJournalRepository,
            ResearcherProfileRepository researcherProfileRepository,
            UserRepository userRepository,
            ActionLogger actionLogger,
            JournalPublisher journalPublisher,
            NewsService newsService) {
        this.researchRepository = researchRepository;
        this.researchProjectRepository = researchProjectRepository;
        this.researchJournalRepository = researchJournalRepository;
        this.researcherProfileRepository = researcherProfileRepository;
        this.userRepository = userRepository;
        this.actionLogger = actionLogger;
        this.journalPublisher = journalPublisher;
        this.newsService = newsService;
    }

    public boolean isResearcher(UUID userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException(I18n.getf("errors.userNotFoundById", userId)));
            if (user instanceof Researcher)
                return true;
            if (researcherProfileRepository.findByUserId(userId).isPresent())
                return true;
            return isEligibleResearcher(user);
        } catch (Exception e) {
            return false;
        }
    }

    public List<ResearchPaper> findAllPapers() {
        return researchRepository.findAll();
    }

    public ResearchPaper publishPaper(UUID researcherId, ResearchPaper paper) {
        ResearchJournal journal = resolveDefaultJournal();
        return publishPaper(researcherId, journal.getId(), paper);
    }

    public void subscribeToJournal(UUID userId) {
        ResearchJournal journal = resolveDefaultJournal();
        subscribeToJournal(userId, journal.getId());
    }

    public ResearchPaper publishPaper(UUID researcherId, UUID journalId, ResearchPaper paper) {
        Researcher researcher = resolveResearcher(researcherId);
        ResearchJournal journal = researchJournalRepository.findById(journalId)
                .orElseThrow(() -> new NotFoundException(I18n.getf("errors.journalNotFoundById", journalId)));

        ResearchPaper saved = researchRepository.save(paper);
        researcher.getResearchPaperIds().add(saved.getId());
        saveResearcher(researcherId, researcher);

        notifyJournalSubscribers(journal, saved);
        newsService.announcePublishedPaper(researcherId, saved.getId());
        try {
            newsService.generateTopCitedResearcherNews(LocalDate.now().getYear());
        } catch (Exception ignored) {
            // No-op: top cited news requires enough data and should not block publishing.
        }
        actionLogger.log(researcherId, "PUBLISH_PAPER", "Published paper " + saved.getId());
        return saved;
    }

    public void subscribeToJournal(UUID userId, UUID journalId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(I18n.getf("errors.userNotFoundById", userId)));
        ResearchJournal journal = researchJournalRepository.findById(journalId)
                .orElseThrow(() -> new NotFoundException(I18n.getf("errors.journalNotFoundById", journalId)));
        if (!journal.getSubscriberIds().contains(userId)) {
            journal.getSubscriberIds().add(userId);
            researchJournalRepository.save(journal);
        }
        journalPublisher.subscribe(new UserJournalSubscriber(userId));
        actionLogger.log(userId, "JOURNAL_SUBSCRIBE", "Subscribed to journal " + journal.getName());
    }

    public void unsubscribeFromJournal(UUID userId, UUID journalId) {
        ResearchJournal journal = researchJournalRepository.findById(journalId)
                .orElseThrow(() -> new NotFoundException(I18n.getf("errors.journalNotFoundById", journalId)));
        if (journal.getSubscriberIds().remove(userId)) {
            researchJournalRepository.save(journal);
        }
        actionLogger.log(userId, "JOURNAL_UNSUBSCRIBE", "Unsubscribed from journal " + journal.getName());
    }

    public List<ResearchJournal> findAllJournals() {
        return researchJournalRepository.findAll();
    }

    public ResearchJournal createJournal(UUID managerId, ResearchJournal journal) {
        ensureManager(managerId);
        ResearchJournal saved = researchJournalRepository.save(journal);
        actionLogger.log(managerId, "CREATE_JOURNAL", "Created journal " + saved.getId());
        return saved;
    }

    public ResearchJournal updateJournal(UUID managerId, ResearchJournal journal) {
        ensureManager(managerId);
        ResearchJournal saved = researchJournalRepository.save(journal);
        actionLogger.log(managerId, "UPDATE_JOURNAL", "Updated journal " + saved.getId());
        return saved;
    }

    public void deleteJournal(UUID managerId, UUID journalId) {
        ensureManager(managerId);
        researchJournalRepository.deleteById(journalId);
        actionLogger.log(managerId, "DELETE_JOURNAL", "Deleted journal " + journalId);
    }

    public void assignSupervisor(UUID graduateStudentId, UUID supervisorId) {
        User graduateCandidate = userRepository.findById(graduateStudentId)
                .orElseThrow(() -> new NotFoundException(I18n.getf("errors.graduateStudentNotFoundById", graduateStudentId)));
        if (!(graduateCandidate instanceof GraduateStudent graduateStudent)) {
            throw new ResearchSupervisorException(I18n.get("errors.supervisorOnlyForGraduateStudents"));
        }

        User supervisorCandidate = userRepository.findById(supervisorId)
                .orElseThrow(() -> new NotFoundException(I18n.getf("errors.supervisorNotFoundById", supervisorId)));
        if (!(supervisorCandidate instanceof Teacher teacher)) {
            throw new ResearchSupervisorException(I18n.get("errors.assignedSupervisorMustBeProfessor"));
        }
        if (teacher.getPosition() != TeacherPosition.PROFESSOR) {
            throw new ResearchSupervisorException(I18n.get("errors.assignedSupervisorMustBeProfessor"));
        }

        Researcher researcher = resolveResearcher(supervisorId);
        if (!(researcher instanceof Teacher)) {
            throw new ResearchSupervisorException(I18n.get("errors.assignedSupervisorMustBeProfessor"));
        }
        List<ResearchPaper> supervisorPapers = resolveResearchPapers(researcher);
        if (researcher.calculateHIndex(supervisorPapers) < 3) {
            throw new ResearchSupervisorException(I18n.get("errors.supervisorHIndexMin"));
        }

        graduateStudent.setSupervisorId(supervisorId);
        userRepository.save(graduateStudent);
        actionLogger.log(supervisorId, "ASSIGN_SUPERVISOR", "Assigned to graduate student " + graduateStudentId);
    }

    public List<ResearchPaper> printPapers(UUID researcherId, Comparator<ResearchPaper> comparator) {
        Researcher researcher = resolveResearcher(researcherId);
        return researcher.printPapers(resolveResearchPapers(researcher), comparator);
    }

    public List<ResearchPaper> printAllPapers(Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> papers = researchRepository.findAll();
        papers.sort(comparator);
        return papers;
    }

    public List<ResearchPaper> printAllPapers(PaperSortType paperSortType) {
        return printAllPapers(PaperSortStrategyFactory.create(paperSortType));
    }

    public List<ResearchProject> findAllProjects() {
        return researchProjectRepository.findAll();
    }

    public ResearchProject createProject(UUID researcherId, ResearchProject project) {
        if (!project.getParticipantIds().contains(researcherId)) {
            project.getParticipantIds().add(researcherId);
        }
        ResearchProject saved = researchProjectRepository.save(project);
        actionLogger.log(researcherId, "CREATE_RESEARCH_PROJECT", "Created project " + saved.getId());
        return saved;
    }

    public void joinProject(UUID researcherId, UUID projectId) {
        resolveResearcher(researcherId);
        ResearchProject project = researchProjectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Research project not found: " + projectId));
        if (!project.getParticipantIds().contains(researcherId)) {
            project.getParticipantIds().add(researcherId);
            researchProjectRepository.save(project);
        }
        actionLogger.log(researcherId, "JOIN_RESEARCH_PROJECT", "Joined project " + projectId);
    }

    public void leaveProject(UUID researcherId, UUID projectId) {
        Researcher researcher = resolveResearcher(researcherId);
        ResearchProject project = researchProjectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Research project not found: " + projectId));
        if (!project.getParticipantIds().contains(researcherId)) {
            throw new ResearchProjectException("Researcher is not a project participant");
        }
        project.getParticipantIds().remove(researcherId);
        project.getPublishedPaperIds().removeIf(paperId -> researcher.getResearchPaperIds().contains(paperId));
        researchProjectRepository.save(project);
        actionLogger.log(researcherId, "LEAVE_RESEARCH_PROJECT", "Left project " + projectId);
    }

    public void publishProjectPaper(UUID researcherId, UUID projectId, UUID paperId) {
        Researcher researcher = resolveResearcher(researcherId);
        ResearchProject project = researchProjectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Research project not found: " + projectId));
        researchRepository.findById(paperId)
                .orElseThrow(() -> new NotFoundException("Research paper not found: " + paperId));
        if (!project.getParticipantIds().contains(researcherId)) {
            throw new ResearchProjectException("Researcher is not a project participant");
        }
        if (!researcher.getResearchPaperIds().contains(paperId)) {
            throw new ResearchProjectException("Researcher does not own this paper");
        }
        if (!project.getPublishedPaperIds().contains(paperId)) {
            project.getPublishedPaperIds().add(paperId);
            researchProjectRepository.save(project);
        }
        actionLogger.log(researcherId, "PUBLISH_PROJECT_PAPER", "Published paper " + paperId
                + " in project " + projectId);
    }

    public ResearchJournal findJournalById(UUID journalId) {
        return researchJournalRepository.findById(journalId)
                .orElseThrow(() -> new NotFoundException("Journal not found: " + journalId));
    }

    private List<ResearchPaper> resolveResearchPapers(Researcher researcher) {
        return researchRepository.findAll().stream()
                .filter(paper -> researcher.getResearchPaperIds().contains(paper.getId()))
                .toList();
    }

    private Researcher resolveResearcher(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Researcher not found: " + userId));
        if (!isEligibleResearcher(user)) {
            throw new ResearchProjectException("User is not eligible to be a researcher: " + userId);
        }
        if (user instanceof Researcher researcher) {
            return researcher;
        }
        return researcherProfileRepository.findByUserId(userId)
                .orElseGet(() -> createResearcherProfile(userId));
    }

    private void saveResearcher(UUID userId, Researcher researcher) {
        if (researcher instanceof User user) {
            userRepository.save(user);
            return;
        }
        if (researcher instanceof ResearcherProfile profile) {
            researcherProfileRepository.save(profile);
            return;
        }
        throw new ResearchProjectException("Unsupported researcher type for user " + userId);
    }

    private ResearcherProfile createResearcherProfile(UUID userId) {
        ResearcherProfile profile = new ResearcherProfile();
        profile.setUserId(userId);
        return researcherProfileRepository.save(profile);
    }

    private boolean isEligibleResearcher(User user) {
        if (user instanceof GraduateStudent) {
            return true;
        }
        if (user instanceof Teacher teacher) {
            return teacher.getPosition() == TeacherPosition.PROFESSOR
                    || teacher.getPosition() == TeacherPosition.SENIOR_LECTURER
                    || (teacher.getPosition() == TeacherPosition.LECTURER && teacher.getAverageRating() >= 4.0);
        }
        if (user instanceof Student student) {
            return student.getYearOfStudy() >= 2 && student.getGpa() >= 3.5;
        }
        if (user instanceof TechSupportSpecialist) {
            return false;
        }
        String department = user instanceof com.kbtu.oop.project.model.user.Employee employee
                ? employee.getDepartment()
                : null;
        return department != null && department.toLowerCase().contains("research");
    }

    private void notifyJournalSubscribers(ResearchJournal journal, ResearchPaper paper) {
        for (UUID subscriberId : journal.getSubscriberIds()) {
            new UserJournalSubscriber(subscriberId).onPaperPublished(paper);
        }
    }

    private ResearchJournal resolveDefaultJournal() {
        return researchJournalRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResearchProjectException("No research journals available"));
    }

    private void ensureManager(UUID managerId) {
        User user = userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("User not found: " + managerId));
        if (!(user instanceof Manager)) {
            throw new ResearchProjectException("Only manager can perform this action");
        }
    }
}
