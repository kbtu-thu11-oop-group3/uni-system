package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ResearchProjectException;
import com.kbtu.oop.project.exception.ResearchSupervisorException;
import com.kbtu.oop.project.model.common.PaperSortType;
import com.kbtu.oop.project.model.research.ResearchPaper;
import com.kbtu.oop.project.model.user.GraduateStudent;
import com.kbtu.oop.project.model.user.Researcher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.pattern.factory.PaperSortStrategyFactory;
import com.kbtu.oop.project.pattern.observer.JournalPublisher;
import com.kbtu.oop.project.pattern.observer.UserJournalSubscriber;
import com.kbtu.oop.project.repository.ResearchRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonResearchRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ResearchService {

    private final ResearchRepository researchRepository;
    private final UserRepository userRepository;
    private final ActionLogger actionLogger;
    private final JournalPublisher journalPublisher;

    public ResearchService() {
        this(new JsonResearchRepository(), new JsonUserRepository(), ActionLogger.getInstance(),
                new JournalPublisher());
    }

    public ResearchService(ResearchRepository researchRepository,
            UserRepository userRepository,
            ActionLogger actionLogger,
            JournalPublisher journalPublisher) {
        this.researchRepository = researchRepository;
        this.userRepository = userRepository;
        this.actionLogger = actionLogger;
        this.journalPublisher = journalPublisher;
    }

    public List<ResearchPaper> findAllPapers() {
        return researchRepository.findAll();
    }

    public ResearchPaper publishPaper(UUID researcherId, ResearchPaper paper) {
        User user = userRepository.findById(researcherId)
                .orElseThrow(() -> new NotFoundException("Researcher not found: " + researcherId));
        if (!(user instanceof Researcher researcher)) {
            throw new ResearchProjectException("User is not a researcher: " + researcherId);
        }

        ResearchPaper saved = researchRepository.save(paper);
        researcher.getResearchPaperIds().add(saved.getId());
        userRepository.save(user);
        journalPublisher.notifyPublished(saved);
        actionLogger.log(researcherId, "PUBLISH_PAPER", "Published paper " + saved.getId());
        return saved;
    }

    public void subscribeToJournal(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        journalPublisher.subscribe(new UserJournalSubscriber(userId));
        actionLogger.log(userId, "JOURNAL_SUBSCRIBE", "Subscribed to university journal notifications");
    }

    public void assignSupervisor(UUID graduateStudentId, UUID supervisorId) {
        User graduateCandidate = userRepository.findById(graduateStudentId)
                .orElseThrow(() -> new NotFoundException("Graduate student not found: " + graduateStudentId));
        if (!(graduateCandidate instanceof GraduateStudent graduateStudent)) {
            throw new ResearchSupervisorException("Supervisor can be assigned only to graduate students");
        }

        User supervisorCandidate = userRepository.findById(supervisorId)
                .orElseThrow(() -> new NotFoundException("Supervisor not found: " + supervisorId));
        if (!(supervisorCandidate instanceof Researcher researcher)) {
            throw new ResearchSupervisorException("Assigned supervisor is not a researcher");
        }
        List<ResearchPaper> supervisorPapers = resolveResearchPapers(researcher);
        if (researcher.calculateHIndex(supervisorPapers) < 3) {
            throw new ResearchSupervisorException("Research supervisor h-index must be at least 3");
        }

        graduateStudent.setSupervisorId(supervisorId);
        userRepository.save(graduateStudent);
        actionLogger.log(supervisorId, "ASSIGN_SUPERVISOR", "Assigned to graduate student " + graduateStudentId);
    }

    public List<ResearchPaper> printPapers(UUID researcherId, Comparator<ResearchPaper> comparator) {
        User researcherCandidate = userRepository.findById(researcherId)
                .orElseThrow(() -> new NotFoundException("Researcher not found: " + researcherId));
        if (!(researcherCandidate instanceof Researcher researcher)) {
            throw new ResearchProjectException("User is not a researcher: " + researcherId);
        }
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

    private List<ResearchPaper> resolveResearchPapers(Researcher researcher) {
        return researchRepository.findAll().stream()
                .filter(paper -> researcher.getResearchPaperIds().contains(paper.getId()))
                .toList();
    }
}