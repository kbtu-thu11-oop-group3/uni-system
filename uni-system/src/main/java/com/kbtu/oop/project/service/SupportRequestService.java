package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ValidationException;
import com.kbtu.oop.project.model.common.RequestStatus;
import com.kbtu.oop.project.model.request.SupportRequest;
import com.kbtu.oop.project.model.user.Employee;
import com.kbtu.oop.project.model.user.TechSupportSpecialist;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.SupportRequestRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonSupportRequestRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;

import java.util.List;
import java.util.UUID;

public class SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;
    private final ActionLogger actionLogger;

    public SupportRequestService() {
        this(new JsonSupportRequestRepository(), new JsonUserRepository(), ActionLogger.getInstance());
    }

    public SupportRequestService(SupportRequestRepository supportRequestRepository,
            UserRepository userRepository,
            ActionLogger actionLogger) {
        this.supportRequestRepository = supportRequestRepository;
        this.userRepository = userRepository;
        this.actionLogger = actionLogger;
    }

    public SupportRequest createRequest(UUID requesterId, String title, String description, String assetName,
            String location) {
        User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> new NotFoundException("Requester not found: " + requesterId));
        if (!(requester instanceof Employee) || requester instanceof TechSupportSpecialist) {
            throw new ValidationException("Only employees (excluding support) can create requests");
        }
        SupportRequest request = new SupportRequest();
        request.setRequesterId(requesterId);
        request.setTitle(title);
        request.setDescription(description);
        request.setAssetName(assetName);
        request.setLocation(location);
        SupportRequest saved = supportRequestRepository.save(request);
        actionLogger.log(requesterId, "CREATE_SUPPORT_REQUEST", "Created support request " + saved.getId());
        return saved;
    }

    public List<SupportRequest> listByRequester(UUID requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Requester not found: " + requesterId));
        return supportRequestRepository.findAll().stream()
                .filter(request -> requesterId.equals(request.getRequesterId()))
                .toList();
    }

    public List<SupportRequest> listAll(UUID specialistId) {
        ensureSpecialist(specialistId);
        return supportRequestRepository.findAll();
    }

    public List<SupportRequest> listNewRequests(UUID specialistId) {
        ensureSpecialist(specialistId);
        List<SupportRequest> requests = supportRequestRepository.findAll().stream()
                .filter(request -> request.getStatus() == RequestStatus.NEW)
                .toList();
        for (SupportRequest request : requests) {
            request.setStatus(RequestStatus.VIEWED);
            supportRequestRepository.save(request);
        }
        actionLogger.log(specialistId, "VIEW_SUPPORT_REQUESTS", "Viewed new support requests");
        return requests;
    }

    public SupportRequest updateStatus(UUID specialistId, UUID requestId, RequestStatus status) {
        ensureSpecialist(specialistId);
        if (status == RequestStatus.NEW) {
            throw new ValidationException("Specialist cannot reset request to NEW status");
        }
        SupportRequest request = supportRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Support request not found: " + requestId));
        request.setStatus(status);
        SupportRequest saved = supportRequestRepository.save(request);
        actionLogger.log(specialistId, "UPDATE_SUPPORT_STATUS", "Updated request " + requestId + " to " + status);
        return saved;
    }

    private void ensureSpecialist(UUID specialistId) {
        User user = userRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found: " + specialistId));
        if (!(user instanceof TechSupportSpecialist)) {
            throw new ValidationException("Only tech support specialist can perform this action");
        }
    }
}