package com.kbtu.oop.project.ui.app;

import javax.swing.SwingUtilities;

public class SwingApp {

    public void start() {
        SwingUtilities.invokeLater(() -> {
            AppFrame frame = new AppFrame();
            frame.setVisible(true);
        });
    }
}
