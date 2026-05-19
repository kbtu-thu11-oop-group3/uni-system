package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.research.ResearchProject;
import com.kbtu.oop.project.repository.ResearchProjectRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonResearchProjectRepository extends AbstractJsonRepository<ResearchProject>
        implements ResearchProjectRepository {

    public JsonResearchProjectRepository() {
        super(DataPaths.researchProjectsPath(), ResearchProject[].class);
    }
}
