package com.SmartIrrigationSystemApp.ui;

import com.SmartIrrigationSystemApp.styling.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class SummaryScreen extends JFrame {
    private JLabel moistureLabel1;
    private JLabel moistureLabel2;
    private JLabel moistureMeanLabel;
    private JLabel lightLabel;
    private JLabel temperatureLabel;
    private JLabel timeTillNextUpdateLabel;

    public SummaryScreen(JFrame previous) {
        setTitle("Summary");
        setSize(400, 250);
        ThemeManager.applyTheme(this); // Apply current theme
        addThemeMenu(); // Toggle menu
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        moistureLabel1 = new JLabel("  Sensor 1 Moisture % in Past 30 Min: N/A", SwingConstants.CENTER);

        // Immediately show the last known moisture reading
        String latestMoist1 = SerialService.getInstance().getLatestMoisture(1);
        moistureLabel1.setText("  Sensor 1 Moisture % in Past 30 Min: " + latestMoist1);

        // Subscribe to real-time updates
        SerialService.getInstance().setOnMoistureUpdate(val ->
                SwingUtilities.invokeLater(() -> moistureLabel1.setText("  Sensor 1 Moisture % in Past 30 Min: " + val))
        , 1);

        moistureLabel2 = new JLabel("  Sensor 2 Moisture % in Past 30 Min: N/A", SwingConstants.CENTER);

        String latestMoist2 = SerialService.getInstance().getLatestMoisture(2);
        moistureLabel2.setText("  Sensor 2 Moisture % in Past 30 Min: " + latestMoist2);

        // Subscribe to real-time updates
        SerialService.getInstance().setOnMoistureUpdate(val ->
                SwingUtilities.invokeLater(() -> moistureLabel2.setText("  Sensor 2 Moisture % in Past 30 Min: " + val))
        , 2);

        moistureMeanLabel = new JLabel("  Mean Sensor % in Past 30 Min: N/A", SwingConstants.CENTER);

        int latestMeanMoist = SerialService.getInstance().getMeanMoisture();
        String stringOfLatestMeanMoist = "N/A";

        if (latestMeanMoist != -1) {
            stringOfLatestMeanMoist = String.valueOf((float) latestMeanMoist);
        }
        moistureMeanLabel.setText("  Mean Sensor % in Past 30 Min: " + stringOfLatestMeanMoist);

        lightLabel = new JLabel("  Light % in Past 30 Min: N/A", SwingConstants.CENTER);

        String latestLight = SerialService.getInstance().getLatestLight();
        lightLabel.setText("  Light % in Past 30 Min: " + latestLight);

        // Subscribe to real-time updates
        SerialService.getInstance().setOnLightUpdate(val ->
                SwingUtilities.invokeLater(() -> lightLabel.setText("  Light % in Past 30 Min: " + val))
        );

        temperatureLabel = new JLabel("  Soil Temperature (°F) in Past 30 Min: N/A", SwingConstants.CENTER);

        // Immediately show the last known moisture reading
        String latestTemp = SerialService.getInstance().getLatestTemperature();
        temperatureLabel.setText("  Soil Temperature (°F) in Past 30 Min: " + latestTemp);

        // Subscribe to real-time updates
        SerialService.getInstance().setOnTemperatureUpdate(val ->
                        SwingUtilities.invokeLater(() -> temperatureLabel.setText("  Soil Temperature (°F) in Past 30 Min: " + val))
                );

        timeTillNextUpdateLabel = new JLabel("  Time Till Next Sensor Update (Min): 30", SwingConstants.CENTER);

        // Immediately show the last known moisture reading
        String TTNU = SerialService.getInstance().getTimeTillNextUpdate();
        timeTillNextUpdateLabel.setText("  Time Till Next Sensor Update (Min): " + TTNU);

        // Subscribe to real-time updates
        SerialService.getInstance().setOnTimeTillNextUpdate(val ->
                SwingUtilities.invokeLater(() -> timeTillNextUpdateLabel.setText("  Time Till Next Sensor Update (Min): " + val))
        );

        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> {
            previous.setVisible(true);
            dispose();
        });

        add(Box.createVerticalGlue());
        add(moistureLabel1);
        add(moistureLabel2);
        add(moistureMeanLabel);
        add(lightLabel);
        add(temperatureLabel);
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); // Make it stretch full width and thin
        add(separator);
        add(Box.createVerticalStrut(10));
        add(timeTillNextUpdateLabel);
        add(Box.createVerticalStrut(20));
        add(backBtn);
        add(Box.createVerticalGlue());

        setLocationRelativeTo(null);
        setVisible(true);

        if (SerialService.getInstance().isLeakDetected()) {
            addLeakIndicator();
        } else {
            SerialService.getInstance().setOnLeakUIUpdate(this::addLeakIndicator);
        }
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


