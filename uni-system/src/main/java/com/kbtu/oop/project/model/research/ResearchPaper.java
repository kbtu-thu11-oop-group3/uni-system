package com.kbtu.oop.project.model.research;

import com.kbtu.oop.project.model.common.BaseEntity;
import com.kbtu.oop.project.model.common.ResearchPaperFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ResearchPaper extends BaseEntity implements Comparable<ResearchPaper> {

    private String title;
    private List<String> authors = new ArrayList<>();
    private String journal;
    private int pages;
    private LocalDate publicationDate;
    private String doi;
    private int citations;

    public String getCitation(ResearchPaperFormat format) {
        return switch (format) {
            case BIBTEX -> "@article{" + doi + ", title={" + title + "}}";
            case PLAIN_TEXT -> String.join(", ", authors) + ". " + title + ". " + journal;
        };
    }

    @Override
    public int compareTo(ResearchPaper other) {
        if (publicationDate == null && other.publicationDate == null) {
            return 0;
        }
        if (publicationDate == null) {
            return -1;
        }
        if (other.publicationDate == null) {
            return 1;
        }
        return publicationDate.compareTo(other.publicationDate);
    }
}