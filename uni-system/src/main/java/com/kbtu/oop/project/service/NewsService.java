package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ValidationException;
import com.kbtu.oop.project.model.common.NewsTopic;
import com.kbtu.oop.project.model.common.School;
import com.kbtu.oop.project.model.communication.News;
import com.kbtu.oop.project.model.communication.NewsComment;
import com.kbtu.oop.project.model.research.ResearchJournal;
import com.kbtu.oop.project.model.research.ResearchPaper;
import com.kbtu.oop.project.model.research.ResearcherProfile;
import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.model.user.Researcher;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.NewsCommentRepository;
import com.kbtu.oop.project.repository.NewsRepository;
import com.kbtu.oop.project.repository.ResearchRepository;
import com.kbtu.oop.project.repository.ResearchJournalRepository;
import com.kbtu.oop.project.repository.ResearcherProfileRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonNewsCommentRepository;
import com.kbtu.oop.project.repository.impl.JsonNewsRepository;
import com.kbtu.oop.project.repository.impl.JsonResearchRepository;
import com.kbtu.oop.project.repository.impl.JsonResearchJournalRepository;
import com.kbtu.oop.project.repository.impl.JsonResearcherProfileRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsCommentRepository newsCommentRepository;
    private final ResearchRepository researchRepository;
    private final ResearchJournalRepository researchJournalRepository;
    private final ResearcherProfileRepository researcherProfileRepository;
    private final UserRepository userRepository;
    private final ActionLogger actionLogger;

    public NewsService() {
        this(new JsonNewsRepository(), new JsonNewsCommentRepository(), new JsonResearchRepository(),
                new JsonResearchJournalRepository(),
                new JsonResearcherProfileRepository(), new JsonUserRepository(), ActionLogger.getInstance());
    }

    public NewsService(NewsRepository newsRepository,
            NewsCommentRepository newsCommentRepository,
            ResearchRepository researchRepository,
            ResearchJournalRepository researchJournalRepository,
            ResearcherProfileRepository researcherProfileRepository,
            UserRepository userRepository,
            ActionLogger actionLogger) {
        this.newsRepository = newsRepository;
        this.newsCommentRepository = newsCommentRepository;
        this.researchRepository = researchRepository;
        this.researchJournalRepository = researchJournalRepository;
        this.researcherProfileRepository = researcherProfileRepository;
        this.userRepository = userRepository;
        this.actionLogger = actionLogger;
    }

    public News publishNews(UUID managerId, String title, String content, NewsTopic topic) {
        ensureManager(managerId);
        News news = new News();
        news.setTitle(title);
        news.setContent(content);
        news.setTopic(topic);
        if (topic == NewsTopic.RESEARCH) {
            news.setPinned(true);
        }
        News saved = newsRepository.save(news);
        actionLogger.log(managerId, "PUBLISH_NEWS", "Published news " + saved.getId());
        return saved;
    }

    public News updateNews(UUID managerId, News news) {
        ensureManager(managerId);
        News saved = newsRepository.save(news);
        actionLogger.log(managerId, "UPDATE_NEWS", "Updated news " + saved.getId());
        return saved;
    }

    public void deleteNews(UUID managerId, UUID newsId) {
        ensureManager(managerId);
        newsRepository.deleteById(newsId);
        actionLogger.log(managerId, "DELETE_NEWS", "Deleted news " + newsId);
    }

    public NewsComment addComment(UUID userId, UUID newsId, String text) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News not found: " + newsId));

        NewsComment comment = new NewsComment();
        comment.setNewsId(newsId);
        comment.setAuthorId(userId);
        comment.setText(text);
        NewsComment savedComment = newsCommentRepository.save(comment);
        news.getCommentIds().add(savedComment.getId());
        newsRepository.save(news);

        actionLogger.log(userId, "ADD_NEWS_COMMENT", "Commented on news " + newsId);
        return savedComment;
    }

    public List<News> listNewsOrdered() {
        List<News> news = new ArrayList<>(newsRepository.findAll());
        news.sort(Comparator
                .comparing(News::isPinned).reversed()
                .thenComparing(News::getCreatedAt).reversed());
        return news;
    }

    public News announcePublishedPaper(UUID researcherId, UUID paperId) {
        ResearchPaper paper = researchRepository.findById(paperId)
                .orElseThrow(() -> new NotFoundException("Research paper not found: " + paperId));
        ResearchJournal journal = researchJournalRepository.findById(paper.getJournalId())
                .orElseThrow(() -> new NotFoundException("Research journal not found: " + paper.getJournalId()));
        return publishResearchNews(researcherId,
                "Research publication: " + paper.getTitle(),
                "A new paper has been published in " + journal.getName() + ". DOI: " + paper.getDoi(),
                NewsTopic.RESEARCH);
    }

    public News generateTopCitedResearcherNews(int year) {
        User topResearcher = userRepository.findAll().stream()
                .filter(user -> !resolveResearchPaperIds(user).isEmpty())
                .max(Comparator.comparingInt(user -> yearCitations(user, year)))
                .orElseThrow(() -> new NotFoundException("No researchers found"));

        int citations = yearCitations(topResearcher, year);
        return publishResearchNews(topResearcher.getId(),
                "Top cited researcher of " + year,
                topResearcher.getFullName() + " became top cited researcher with " + citations + " citations.",
                NewsTopic.RESEARCH);
    }

    public News generateTopCitedResearcherNews(int year, School school) {
        User topResearcher = userRepository.findAll().stream()
                .filter(user -> isResearcherFromSchool(user, school))
                .max(Comparator.comparingInt(user -> yearCitations(user, year)))
                .orElseThrow(() -> new NotFoundException("No researchers found for school " + school));

        int citations = yearCitations(topResearcher, year);
        return publishResearchNews(topResearcher.getId(),
                "Top cited researcher of " + school + " in " + year,
                topResearcher.getFullName() + " became top cited researcher with " + citations + " citations.",
                NewsTopic.RESEARCH);
    }

    public List<NewsComment> listNewsComments(UUID newsId) {
        return newsCommentRepository.findAll().stream()
                .filter(comment -> newsId.equals(comment.getNewsId()))
                .sorted(Comparator.comparing(NewsComment::getCreatedAt))
                .toList();
    }

    public News publishResearchNews(UUID authorId, String title, String content, NewsTopic topic) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found: " + authorId));
        if (!(author instanceof Manager) && resolveResearchPaperIds(author).isEmpty()) {
            throw new ValidationException("Only managers or researchers can publish research news");
        }
        News news = new News();
        news.setTitle(title);
        news.setContent(content);
        news.setTopic(topic);
        news.setPinned(true);
        News saved = newsRepository.save(news);
        actionLogger.log(authorId, "PUBLISH_RESEARCH_NEWS", "Published research news " + saved.getId());
        return saved;
    }

    private void ensureManager(UUID managerId) {
        User user = userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("User not found: " + managerId));
        if (!(user instanceof Manager)) {
            throw new NotFoundException("Only manager can perform this action");
        }
    }

    private int yearCitations(User user, int year) {
        List<java.util.UUID> paperIds = resolveResearchPaperIds(user);
        return researchRepository.findAll().stream()
                .filter(paper -> paperIds.contains(paper.getId()))
                .filter(paper -> paper.getPublicationDate() != null)
                .filter(paper -> paper.getPublicationDate().getYear() == year)
                .map(ResearchPaper::getCitations)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private List<java.util.UUID> resolveResearchPaperIds(User user) {
        if (user instanceof Researcher researcher) {
            return researcher.getResearchPaperIds();
        }
        return researcherProfileRepository.findByUserId(user.getId())
                .map(ResearcherProfile::getResearchPaperIds)
                .orElse(List.of());
    }

    private boolean isResearcherFromSchool(User user, School school) {
        if (!(user instanceof Student student)) {
            return false;
        }
        return school == student.getSchool() && !resolveResearchPaperIds(user).isEmpty();
    }
}