package com.kbtu.oop.project.ui.console;

import com.kbtu.oop.project.demo.DemoScenarioService;
import com.kbtu.oop.project.demo.SeedDataService;

import java.util.Arrays;

public class ConsoleApp {

    public void start(String[] args) {
        Menu.showWelcome();

        var argList = Arrays.asList(args);
        boolean seedOnly = argList.contains("seed");
        boolean runDemo = argList.contains("demo") || argList.contains("run");
        boolean defaultMode = argList.isEmpty();

        if (defaultMode || seedOnly || runDemo) {
            new SeedDataService().resetAndSeed();
            System.out.println("Seed data generated.");
        }

        if (defaultMode || runDemo) {
            new DemoScenarioService().runAll();
        }
    }
}