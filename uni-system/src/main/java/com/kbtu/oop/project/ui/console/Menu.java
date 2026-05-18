package com.kbtu.oop.project.ui.console;

public final class Menu {

    private Menu() {
    }

    public static void showWelcome() {
        System.out.println("University system initialized.");
        System.out.println("Modes: default (seed+demo), seed, demo");
    }
}