package com.kbtu.oop.project.model.request;

import com.kbtu.oop.project.model.common.BaseEntity;
import com.kbtu.oop.project.model.common.RequestStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class Request extends BaseEntity {

    private String title;
    private String description;
    private RequestStatus status = RequestStatus.NEW;
}