package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ValidationException;
import com.kbtu.oop.project.model.communication.Message;
import com.kbtu.oop.project.model.user.Employee;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.MessageRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonMessageRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;

import java.util.List;
import java.util.UUID;

public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ActionLogger actionLogger;

    public MessageService() {
        this(new JsonMessageRepository(), new JsonUserRepository(), ActionLogger.getInstance());
    }

    public MessageService(MessageRepository messageRepository,
            UserRepository userRepository,
            ActionLogger actionLogger) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.actionLogger = actionLogger;
    }

    public Message sendEmployeeMessage(UUID senderId, UUID recipientId, String subject, String body) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Sender not found: " + senderId));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new NotFoundException("Recipient not found: " + recipientId));
        if (!(sender instanceof Employee) || !(recipient instanceof Employee)) {
            throw new ValidationException("Only employees can exchange official messages");
        }

        Message message = new Message();
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setSubject(subject);
        message.setBody(body);
        Message saved = messageRepository.save(message);
        actionLogger.log(senderId, "SEND_MESSAGE", "Sent message " + saved.getId() + " to " + recipientId);
        return saved;
    }

    public List<Message> inbox(UUID userId) {
        return messageRepository.findAll().stream()
                .filter(message -> userId.equals(message.getRecipientId()))
                .toList();
    }

    public List<Message> outbox(UUID userId) {
        return messageRepository.findAll().stream()
                .filter(message -> userId.equals(message.getSenderId()))
                .toList();
    }

    public Message markRead(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found: " + messageId));
        if (!userId.equals(message.getRecipientId())) {
            throw new ValidationException("Only recipient can mark message as read");
        }
        message.setRead(true);
        return messageRepository.save(message);
    }
}