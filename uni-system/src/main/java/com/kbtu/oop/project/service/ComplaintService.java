package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ValidationException;
import com.kbtu.oop.project.model.common.UrgencyLevel;
import com.kbtu.oop.project.model.request.Complaint;
import com.kbtu.oop.project.model.common.RequestStatus;
import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.model.user.Teacher;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.ComplaintRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonComplaintRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;
import com.kbtu.oop.project.util.I18n;

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
                .orElseThrow(() -> new NotFoundException(I18n.getf("errors.teacherNotFoundById", teacherId)));
        if (!(teacherCandidate instanceof Teacher)) {
            throw new ValidationException(I18n.get("errors.onlyTeacherCanSendComplaints"));
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

    public List<Complaint> listAll(UUID managerId) {
        ensureManager(managerId);
        return complaintRepository.findAll();
    }

    public Complaint updateStatus(UUID managerId, UUID complaintId, RequestStatus status) {
        ensureManager(managerId);
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new NotFoundException(I18n.getf("errors.complaintNotFoundById", complaintId)));
        complaint.setStatus(status);
        Complaint saved = complaintRepository.save(complaint);
        actionLogger.log(managerId, "UPDATE_COMPLAINT", "Updated complaint " + complaintId + " to " + status);
        return saved;
    }

    public List<Complaint> listByTeacher(UUID teacherId) {
        User teacherCandidate = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException(I18n.getf("errors.teacherNotFoundById", teacherId)));
        if (!(teacherCandidate instanceof Teacher)) {
            throw new ValidationException(I18n.get("errors.onlyTeacherCanViewComplaints"));
        }

        return complaintRepository.findAll().stream()
                .filter(complaint -> teacherId.equals(complaint.getSenderTeacherId()))
                .toList();
    }

    private void ensureManager(UUID managerId) {
        User user = userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException(I18n.getf("errors.managerNotFoundById", managerId)));
        if (!(user instanceof Manager)) {
            throw new ValidationException(I18n.get("errors.onlyManagerAction"));
        }
    }
}
