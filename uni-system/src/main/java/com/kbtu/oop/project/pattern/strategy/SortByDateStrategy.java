package com.kbtu.oop.project.pattern.strategy;

import com.kbtu.oop.project.model.research.ResearchPaper;

import java.time.LocalDate;

public class SortByDateStrategy implements PaperSortStrategy {

    @Override
    public int compare(ResearchPaper left, ResearchPaper right) {
        LocalDate leftDate = left.getPublicationDate();
        LocalDate rightDate = right.getPublicationDate();
        if (leftDate == null && rightDate == null) {
            return 0;
        }
        if (leftDate == null) {
            return 1;
        }
        if (rightDate == null) {
            return -1;
        }
        return rightDate.compareTo(leftDate);
    }
}