package com.kbtu.oop.project.ui.console;

import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

public class ConsolePrompter {

    private final Scanner scanner;

    public ConsolePrompter(Scanner scanner) {
        this.scanner = scanner;
    }

    public String prompt(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    public String promptOptional(String label) {
        System.out.print(label + " (optional): ");
        return scanner.nextLine().trim();
    }

    public int promptInt(String label) {
        while (true) {
            try {
                return Integer.parseInt(prompt(label));
            } catch (NumberFormatException exception) {
                System.out.println("Enter a valid integer.");
            }
        }
    }

    public double promptDouble(String label) {
        while (true) {
            try {
                return Double.parseDouble(prompt(label));
            } catch (NumberFormatException exception) {
                System.out.println("Enter a valid decimal number.");
            }
        }
    }

    public UUID promptUuid(String label) {
        while (true) {
            try {
                return UUID.fromString(prompt(label));
            } catch (IllegalArgumentException exception) {
                System.out.println("Enter a valid UUID.");
            }
        }
    }

    public boolean promptYesNo(String label) {
        String answer = prompt(label + " [y/n]");
        return answer.toLowerCase(Locale.ROOT).startsWith("y");
    }

    public void pressEnterToContinue() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
