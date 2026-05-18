package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.research.ResearchPaper;
import com.kbtu.oop.project.repository.ResearchRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonResearchRepository extends AbstractJsonRepository<ResearchPaper> implements ResearchRepository {

    public JsonResearchRepository() {
        super(DataPaths.researchPapersPath(), ResearchPaper[].class);
    }
}