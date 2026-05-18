package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.communication.Message;
import com.kbtu.oop.project.repository.MessageRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonMessageRepository extends AbstractJsonRepository<Message> implements MessageRepository {

    public JsonMessageRepository() {
        super(DataPaths.messagesPath(), Message[].class);
    }
}