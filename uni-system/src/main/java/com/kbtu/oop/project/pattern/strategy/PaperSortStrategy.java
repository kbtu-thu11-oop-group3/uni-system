package com.kbtu.oop.project.pattern.strategy;

import com.kbtu.oop.project.model.research.ResearchPaper;

import java.util.Comparator;

public interface PaperSortStrategy extends Comparator<ResearchPaper> {
}