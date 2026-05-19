package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.research.ResearchJournal;
import com.kbtu.oop.project.repository.ResearchJournalRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonResearchJournalRepository extends AbstractJsonRepository<ResearchJournal>
        implements ResearchJournalRepository {

    public JsonResearchJournalRepository() {
        super(DataPaths.researchJournalsPath(), ResearchJournal[].class);
    }
}
