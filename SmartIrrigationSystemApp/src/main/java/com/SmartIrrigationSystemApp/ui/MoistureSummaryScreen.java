package com.SmartIrrigationSystemApp.ui;

import com.SmartIrrigationSystemApp.styling.ThemeManager;

import javax.swing.*;

public class MoistureSummaryScreen extends JFrame {
    private JLabel moistureLabel;

    public MoistureSummaryScreen(JFrame previous) {
        setTitle("Moisture Summary");
        setSize(400, 200);
        ThemeManager.applyTheme(this); // Apply current theme
        addThemeMenu(); // Toggle menu
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        moistureLabel = new JLabel("Moisture in Past 30 Min: N/A", SwingConstants.CENTER);

        // Immediately show the last known moisture reading
        String latest = SerialService.getInstance().getLatestMoisture();
        moistureLabel.setText("Moisture % in Past 30 Min: " + latest);

        // Subscribe to real-time updates
        SerialService.getInstance().setOnMoistureUpdate(val ->
                SwingUtilities.invokeLater(() -> moistureLabel.setText("Moisture % in Past 30 Min: " + val))
        );

        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> {
            previous.setVisible(true);
            dispose();
        });

        add(Box.createVerticalGlue());
        add(moistureLabel);
        add(Box.createVerticalStrut(20));
        add(backBtn);
        add(Box.createVerticalGlue());

        setLocationRelativeTo(null);
        setVisible(true);
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


