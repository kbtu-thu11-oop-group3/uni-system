package com.kbtu.oop.project.pattern.strategy;

import com.kbtu.oop.project.model.research.ResearchPaper;

public class SortByPagesStrategy implements PaperSortStrategy {

    @Override
    public int compare(ResearchPaper left, ResearchPaper right) {
        return Integer.compare(right.getPages(), left.getPages());
    }
}