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

        SerialService.getInstance().tryAutoConnect();

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
        JButton summaryBtn = new JButton("Sensor Summary");
        JButton commandBtn = new JButton("Commands");
        JButton graphBtn = new JButton("Graph Creator");


        // Align buttons
        monitorBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        summaryBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        commandBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add button actions
        monitorBtn.addActionListener(e -> {
            SerialMonitorScreen.showMonitor(); // Singleton-safe open
        });

        settingsBtn.addActionListener(e -> {
            new SettingsScreen(this);
            setVisible(false);
        });

        summaryBtn.addActionListener(e -> {
            new SummaryScreen(this);
            setVisible(false);
        });

        commandBtn.addActionListener(e -> {
            new CommandScreen(this);
            setVisible(false);
        });

        graphBtn.addActionListener(e -> {
            new GraphCreatorScreen(this);
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
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(graphBtn);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);

        if (SerialService.getInstance().isLeakDetected()) {
            addLeakIndicator();
        } else {
            SerialService.getInstance().setOnLeakUIUpdate(this::addLeakIndicator);
        }

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

    private void addLeakIndicator() {
        SwingUtilities.invokeLater(() -> {
            JLabel alertIcon = new JLabel("⚠ Leak Detected!");
            alertIcon.setForeground(Color.RED);
            alertIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));
            alertIcon.setAlignmentX(Component.RIGHT_ALIGNMENT);

            Timer blinkTimer = new Timer(500, null);
            blinkTimer.addActionListener(e -> {
                alertIcon.setVisible(!alertIcon.isVisible());
            });
            blinkTimer.start();

            getContentPane().add(alertIcon, BorderLayout.SOUTH);
            revalidate();
            repaint();
        });
    }
}