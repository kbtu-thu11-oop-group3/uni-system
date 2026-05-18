package com.kbtu.oop.project.model.communication;

import com.kbtu.oop.project.model.common.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Message extends BaseEntity {

    private UUID senderId;
    private UUID recipientId;
    private String subject;
    private String body;
    private boolean read;
}