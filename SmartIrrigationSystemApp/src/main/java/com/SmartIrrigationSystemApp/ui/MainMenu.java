package com.SmartIrrigationSystemApp.ui;

import com.SmartIrrigationSystemApp.styling.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Main Menu");
        setSize(400, 300);
        ThemeManager.applyTheme(this); // Apply current theme
        addThemeMenu(); // Toggle menu
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use vertical box layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // Title label
        JLabel titleLabel = new JLabel("Smart Irrigation System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        JButton monitorBtn = new JButton("Serial Monitor");
        JButton settingsBtn = new JButton("Settings");
        JButton summaryBtn = new JButton("Moisture Summary");
        JButton commandBtn = new JButton("Commands");

        // Align buttons
        monitorBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        summaryBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        commandBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add button actions
        monitorBtn.addActionListener(e -> {
            SerialMonitorScreen.showMonitor(); // Singleton-safe open
        });

        settingsBtn.addActionListener(e -> {
            new SettingsScreen(this);
            setVisible(false);
        });

        summaryBtn.addActionListener(e -> {
            new MoistureSummaryScreen(this);
            setVisible(false);
        });

        commandBtn.addActionListener(e -> {
            new CommandScreen(this);
            setVisible(false);
        });

        // Add everything to main panel
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(monitorBtn);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(settingsBtn);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(summaryBtn);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(commandBtn);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }

    private void addThemeMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu themeMenu = new JMenu("Theme");
        JMenuItem toggleItem = new JMenuItem("Toggle Light/Dark Mode");

        toggleItem.addActionListener(e -> ThemeManager.toggleTheme(this));
        themeMenu.add(toggleItem);
        menuBar.add(themeMenu);
        setJMenuBar(menuBar);
    }

}