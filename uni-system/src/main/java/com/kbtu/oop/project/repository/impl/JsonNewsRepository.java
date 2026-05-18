package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.communication.News;
import com.kbtu.oop.project.repository.NewsRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonNewsRepository extends AbstractJsonRepository<News> implements NewsRepository {

    public JsonNewsRepository() {
        super(DataPaths.newsPath(), News[].class);
    }
}