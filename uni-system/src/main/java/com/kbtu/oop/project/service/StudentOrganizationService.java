package com.kbtu.oop.project.service;

import com.kbtu.oop.project.exception.NotFoundException;
import com.kbtu.oop.project.exception.ValidationException;
import com.kbtu.oop.project.model.common.RequestStatus;
import com.kbtu.oop.project.model.organization.StudentOrganization;
import com.kbtu.oop.project.model.user.Manager;
import com.kbtu.oop.project.model.user.Student;
import com.kbtu.oop.project.model.user.User;
import com.kbtu.oop.project.repository.StudentOrganizationRepository;
import com.kbtu.oop.project.repository.UserRepository;
import com.kbtu.oop.project.repository.impl.JsonStudentOrganizationRepository;
import com.kbtu.oop.project.repository.impl.JsonUserRepository;
import com.kbtu.oop.project.util.ActionLogger;

import java.util.List;
import java.util.UUID;

public class StudentOrganizationService {

    private final StudentOrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final ActionLogger actionLogger;

    public StudentOrganizationService() {
        this(new JsonStudentOrganizationRepository(), new JsonUserRepository(), ActionLogger.getInstance());
    }

    public StudentOrganizationService(StudentOrganizationRepository organizationRepository,
            UserRepository userRepository,
            ActionLogger actionLogger) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.actionLogger = actionLogger;
    }

    public List<StudentOrganization> findAll() {
        return organizationRepository.findAll();
    }

    public List<StudentOrganization> findApproved() {
        return organizationRepository.findAll().stream()
                .filter(org -> org.getStatus() == RequestStatus.ACCEPTED)
                .toList();
    }

    public StudentOrganization createRequest(UUID studentId, String name) {
        ensureStudent(studentId);
        StudentOrganization organization = new StudentOrganization();
        organization.setName(name);
        organization.setRequesterId(studentId);
        organization.setStatus(RequestStatus.NEW);
        StudentOrganization saved = organizationRepository.save(organization);
        actionLogger.log(studentId, "CREATE_ORG", "Created organization request " + saved.getId());
        return saved;
    }

    public StudentOrganization join(UUID studentId, UUID orgId) {
        ensureStudent(studentId);
        StudentOrganization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new NotFoundException("Organization not found: " + orgId));
        if (org.getStatus() != RequestStatus.ACCEPTED) {
            throw new ValidationException("Organization is not approved yet");
        }
        if (!org.getMemberIds().contains(studentId)) {
            org.getMemberIds().add(studentId);
        }
        StudentOrganization saved = organizationRepository.save(org);
        actionLogger.log(studentId, "JOIN_ORG", "Joined organization " + orgId);
        return saved;
    }

    public StudentOrganization leave(UUID studentId, UUID orgId) {
        ensureStudent(studentId);
        StudentOrganization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new NotFoundException("Organization not found: " + orgId));
        org.getMemberIds().remove(studentId);
        if (studentId.equals(org.getHeadId())) {
            org.setHeadId(org.getMemberIds().isEmpty() ? null : org.getMemberIds().get(0));
        }
        StudentOrganization saved = organizationRepository.save(org);
        actionLogger.log(studentId, "LEAVE_ORG", "Left organization " + orgId);
        return saved;
    }

    public StudentOrganization setHead(UUID studentId, UUID orgId) {
        ensureStudent(studentId);
        StudentOrganization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new NotFoundException("Organization not found: " + orgId));
        if (org.getStatus() != RequestStatus.ACCEPTED) {
            throw new ValidationException("Organization is not approved yet");
        }
        if (!org.getMemberIds().contains(studentId)) {
            throw new ValidationException("Only members can become head");
        }
        org.setHeadId(studentId);
        StudentOrganization saved = organizationRepository.save(org);
        actionLogger.log(studentId, "SET_ORG_HEAD", "Set organization head " + orgId);
        return saved;
    }

    public StudentOrganization approve(UUID managerId, UUID orgId) {
        ensureManager(managerId);
        StudentOrganization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new NotFoundException("Organization not found: " + orgId));
        org.setStatus(RequestStatus.ACCEPTED);
        org.setHeadId(org.getRequesterId());
        if (org.getRequesterId() != null && !org.getMemberIds().contains(org.getRequesterId())) {
            org.getMemberIds().add(org.getRequesterId());
        }
        StudentOrganization saved = organizationRepository.save(org);
        actionLogger.log(managerId, "APPROVE_ORG", "Approved organization " + orgId);
        return saved;
    }

    public StudentOrganization reject(UUID managerId, UUID orgId) {
        ensureManager(managerId);
        StudentOrganization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new NotFoundException("Organization not found: " + orgId));
        org.setStatus(RequestStatus.REJECTED);
        StudentOrganization saved = organizationRepository.save(org);
        actionLogger.log(managerId, "REJECT_ORG", "Rejected organization " + orgId);
        return saved;
    }

    public void delete(UUID managerId, UUID orgId) {
        ensureManager(managerId);
        organizationRepository.deleteById(orgId);
        actionLogger.log(managerId, "DELETE_ORG", "Deleted organization " + orgId);
    }

    private void ensureStudent(UUID studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
        if (!(user instanceof Student)) {
            throw new ValidationException("Only student can perform this action");
        }
    }

    private void ensureManager(UUID managerId) {
        User user = userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Manager not found: " + managerId));
        if (!(user instanceof Manager)) {
            throw new ValidationException("Only manager can perform this action");
        }
    }
}
