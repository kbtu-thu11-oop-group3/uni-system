package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.communication.NewsComment;
import com.kbtu.oop.project.repository.NewsCommentRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonNewsCommentRepository extends AbstractJsonRepository<NewsComment> implements NewsCommentRepository {

    public JsonNewsCommentRepository() {
        super(DataPaths.newsCommentsPath(), NewsComment[].class);
    }
}