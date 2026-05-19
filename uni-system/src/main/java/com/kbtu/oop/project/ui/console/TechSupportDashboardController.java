package com.kbtu.oop.project.ui.console;

import com.kbtu.oop.project.model.common.RequestStatus;
import com.kbtu.oop.project.model.request.SupportRequest;
import com.kbtu.oop.project.model.user.TechSupportSpecialist;

import java.util.List;
import java.util.UUID;

public class TechSupportDashboardController extends BaseDashboardController {

    public TechSupportDashboardController(ConsolePrompter prompter) {
        super(prompter);
    }

    public void open(TechSupportSpecialist specialist) {
        while (true) {
            System.out.println();
            System.out.println("Welcome, " + specialist.getFullName());
            System.out.println("1. View profile");
            System.out.println("2. View new support requests");
            System.out.println("3. Update support request status");
            System.out.println("0. Logout");

            switch (prompter.prompt("Choose action")) {
                case "1" -> printProfile(specialist.getId());
                case "2" -> listSupportRequests(specialist.getId());
                case "3" -> updateSupportStatus(specialist.getId());
                case "0" -> {
                    return;
                }
                default -> System.out.println("Unknown action");
            }
        }
    }

    private void listSupportRequests(UUID specialistId) {
        List<SupportRequest> requests = supportRequestService.listNewRequests(specialistId);
        for (SupportRequest request : requests) {
            System.out.println(request.getId() + " | " + request.getTitle() + " | " + request.getStatus());
        }
    }

    private void updateSupportStatus(UUID specialistId) {
        UUID requestId = prompter.promptUuid("Request ID");
        RequestStatus status = RequestStatus
                .valueOf(prompter.prompt("Status [VIEWED, ACCEPTED, IN_PROGRESS, DONE, REJECTED]").toUpperCase());
        supportRequestService.updateStatus(specialistId, requestId, status);
        System.out.println("Request updated.");
    }
}