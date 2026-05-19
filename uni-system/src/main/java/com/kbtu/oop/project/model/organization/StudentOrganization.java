package com.kbtu.oop.project.model.organization;

import com.kbtu.oop.project.model.common.BaseEntity;
import com.kbtu.oop.project.model.common.RequestStatus;
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
public class StudentOrganization extends BaseEntity {

    private String name;
    private UUID requesterId;
    private UUID headId;
    private RequestStatus status = RequestStatus.NEW;
    private List<UUID> memberIds = new ArrayList<>();
}
