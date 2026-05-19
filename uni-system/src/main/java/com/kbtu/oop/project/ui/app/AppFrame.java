package com.kbtu.oop.project.ui.app;

import com.kbtu.oop.project.model.user.User;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

public class AppFrame extends JFrame {

    private static final String CARD_LOGIN = "login";
    private static final String CARD_ROLE = "role";

    private final UiContext context;
    private final CardLayout cardLayout;
    private final JPanel cards;
    private final LoginPanel loginPanel;
    private RolePanel rolePanel;

    public AppFrame() {
        super("University System");
        this.context = new UiContext();
        this.cardLayout = new CardLayout();
        this.cards = new JPanel(cardLayout);
        this.loginPanel = new LoginPanel(context, this::onLoginSuccess);

        cards.add(loginPanel, CARD_LOGIN);
        cardLayout.show(cards, CARD_LOGIN);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1100, 700));
        setContentPane(cards);
        pack();
        setLocationRelativeTo(null);
    }

    private void onLoginSuccess(User user) {
        this.rolePanel = new RolePanel(context, user, this::onLogout);
        cards.add(rolePanel, CARD_ROLE);
        cardLayout.show(cards, CARD_ROLE);
    }

    private void onLogout() {
        cardLayout.show(cards, CARD_LOGIN);
        loginPanel.reset();
    }
}
