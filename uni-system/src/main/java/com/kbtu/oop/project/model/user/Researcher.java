package com.kbtu.oop.project.model.user;

import com.kbtu.oop.project.model.research.ResearchPaper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public interface Researcher {

    List<UUID> getResearchPaperIds();

    void setResearchPaperIds(List<UUID> researchPaperIds);

    default int calculateHIndex(List<ResearchPaper> papersInput) {
        List<ResearchPaper> papers = new ArrayList<>(papersInput);
        papers.sort((left, right) -> Integer.compare(right.getCitations(), left.getCitations()));

        int hIndex = 0;
        for (int index = 0; index < papers.size(); index++) {
            if (papers.get(index).getCitations() >= index + 1) {
                hIndex = index + 1;
            } else {
                break;
            }
        }
        return hIndex;
    }

    default List<ResearchPaper> printPapers(List<ResearchPaper> papersInput, Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> papers = new ArrayList<>(papersInput);
        papers.sort(comparator);
        return papers;
    }
}