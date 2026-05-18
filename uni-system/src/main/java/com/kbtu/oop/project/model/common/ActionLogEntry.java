package com.kbtu.oop.project.model.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ActionLogEntry extends BaseEntity {

    private UUID actorId;
    private String action;
    private String details;
    private LocalDateTime occurredAt = LocalDateTime.now();
}