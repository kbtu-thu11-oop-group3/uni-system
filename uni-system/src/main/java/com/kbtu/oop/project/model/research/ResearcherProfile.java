package com.kbtu.oop.project.model.research;

import com.kbtu.oop.project.model.common.BaseEntity;
import com.kbtu.oop.project.model.user.Researcher;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ResearcherProfile extends BaseEntity implements Researcher {

    private UUID userId;
    private List<UUID> researchPaperIds = new ArrayList<>();
    private List<UUID> researchProjectIds = new ArrayList<>();
}
