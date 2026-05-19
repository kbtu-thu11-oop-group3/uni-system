package com.kbtu.oop.project.ui.console;

public class ConsoleApp {

    public void start(String[] args) {
        Menu.showWelcome();
        new LoginController().start();
    }
}