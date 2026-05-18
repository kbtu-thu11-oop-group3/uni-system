package com.kbtu.oop.project.pattern.factory;

import com.kbtu.oop.project.model.common.PaperSortType;
import com.kbtu.oop.project.pattern.strategy.PaperSortStrategy;
import com.kbtu.oop.project.pattern.strategy.SortByCitationsStrategy;
import com.kbtu.oop.project.pattern.strategy.SortByDateStrategy;
import com.kbtu.oop.project.pattern.strategy.SortByPagesStrategy;

public final class PaperSortStrategyFactory {

    private PaperSortStrategyFactory() {
    }

    public static PaperSortStrategy create(PaperSortType paperSortType) {
        return switch (paperSortType) {
            case BY_DATE -> new SortByDateStrategy();
            case BY_CITATIONS -> new SortByCitationsStrategy();
            case BY_PAGES -> new SortByPagesStrategy();
        };
    }
}