package com.kbtu.oop.project.model.communication;

import com.kbtu.oop.project.model.common.BaseEntity;
import com.kbtu.oop.project.model.common.NewsTopic;
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
public class News extends BaseEntity {

    private String title;
    private String content;
    private NewsTopic topic = NewsTopic.GENERAL;
    private boolean pinned;
    private List<UUID> commentIds = new ArrayList<>();
}