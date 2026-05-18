package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ValidationException;
import com.kbtu.oop.project.model.common.UrgencyLevel;
import com.kbtu.oop.project.model.request.Complaint;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.ComplaintRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonComplaintRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;

import java.util.List;
import java.util.UUID;

public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final ActionLogger actionLogger;

    public ComplaintService() {
        this(new JsonComplaintRepository(), new JsonUserRepository(), ActionLogger.getInstance());
    }

    public ComplaintService(ComplaintRepository complaintRepository,
            UserRepository userRepository,
            ActionLogger actionLogger) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.actionLogger = actionLogger;
    }

    public Complaint sendComplaint(UUID teacherId, List<UUID> studentIds, UrgencyLevel urgencyLevel, String title,
            String description) {
        User teacherCandidate = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher not found: " + teacherId));
        if (!(teacherCandidate instanceof Teacher)) {
            throw new ValidationException("Only teacher can send complaints");
        }

        Complaint complaint = new Complaint();
        complaint.setSenderTeacherId(teacherId);
        complaint.setStudentIds(studentIds);
        complaint.setUrgencyLevel(urgencyLevel);
        complaint.setTitle(title);
        complaint.setDescription(description);
        Complaint saved = complaintRepository.save(complaint);
        actionLogger.log(teacherId, "SEND_COMPLAINT", "Sent complaint " + saved.getId());
        return saved;
    }
}