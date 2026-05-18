package com.kbtu.oop.project.model.request;

import com.kbtu.oop.project.model.common.UrgencyLevel;
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
public class Complaint extends Request {

    private UUID senderTeacherId;
    private List<UUID> studentIds = new ArrayList<>();
    private UrgencyLevel urgencyLevel = UrgencyLevel.LOW;
}