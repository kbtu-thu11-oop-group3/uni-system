package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.model.common.NewsTopic;
import com.kbtu.oop.project.model.communication.News;
import com.kbtu.oop.project.model.communication.NewsComment;
import com.kbtu.oop.project.model.research.ResearchPaper;
import com.kbtu.oop.project.model.user.Researcher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.NewsCommentRepository;
import com.kbtu.oop.project.repository.NewsRepository;
import com.kbtu.oop.project.repository.ResearchRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonNewsCommentRepository;
import com.kbtu.oop.project.repository.impl.JsonNewsRepository;
import com.kbtu.oop.project.repository.impl.JsonResearchRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;

import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsCommentRepository newsCommentRepository;
    private final ResearchRepository researchRepository;
    private final UserRepository userRepository;
    private final ActionLogger actionLogger;

    public NewsService() {
        this(new JsonNewsRepository(), new JsonNewsCommentRepository(), new JsonResearchRepository(),
                new JsonUserRepository(), ActionLogger.getInstance());
    }

    public NewsService(NewsRepository newsRepository,
            NewsCommentRepository newsCommentRepository,
            ResearchRepository researchRepository,
            UserRepository userRepository,
            ActionLogger actionLogger) {
        this.newsRepository = newsRepository;
        this.newsCommentRepository = newsCommentRepository;
        this.researchRepository = researchRepository;
        this.userRepository = userRepository;
        this.actionLogger = actionLogger;
    }

    public News publishNews(UUID managerId, String title, String content, NewsTopic topic) {
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
        return publishNews(researcherId,
                "Research publication: " + paper.getTitle(),
                "A new paper has been published in " + paper.getJournal() + ". DOI: " + paper.getDoi(),
                NewsTopic.RESEARCH);
    }

    public News generateTopCitedResearcherNews(int year) {
        User topResearcher = userRepository.findAll().stream()
                .filter(Researcher.class::isInstance)
                .max(Comparator.comparingInt(user -> yearCitations(user, year)))
                .orElseThrow(() -> new NotFoundException("No researchers found"));

        int citations = yearCitations(topResearcher, year);
        return publishNews(topResearcher.getId(),
                "Top cited researcher of " + year,
                topResearcher.getFullName() + " became top cited researcher with " + citations + " citations.",
                NewsTopic.RESEARCH);
    }

    public List<NewsComment> listNewsComments(UUID newsId) {
        return newsCommentRepository.findAll().stream()
                .filter(comment -> newsId.equals(comment.getNewsId()))
                .sorted(Comparator.comparing(NewsComment::getCreatedAt))
                .toList();
    }

    private int yearCitations(User user, int year) {
        if (!(user instanceof Researcher researcher)) {
            return 0;
        }
        return researchRepository.findAll().stream()
                .filter(paper -> researcher.getResearchPaperIds().contains(paper.getId()))
                .filter(paper -> paper.getPublicationDate() != null)
                .filter(paper -> paper.getPublicationDate().getYear() == year)
                .map(ResearchPaper::getCitations)
                .mapToInt(Integer::intValue)
                .sum();
    }
}