package com.SmartIrrigationSystemApp.ui;

import com.SmartIrrigationSystemApp.styling.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SettingsScreen extends JFrame {
    public SettingsScreen(JFrame previous) {
        setTitle("Settings");
        setSize(350, 640);
        ThemeManager.applyTheme(this);
        addThemeMenu();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel portLabel = new JLabel("Select COM Port:");
        portLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<String> portList = new JComboBox<>(SerialService.getInstance().getAvailablePorts());
        portList.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoLabel = new JLabel("\u2139");
        infoLabel.setToolTipText("Typically the Arduino COM Port is cu.usbmodem1101");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField wateringThresholdField = new JTextField(Float.toString(SerialService.getInstance().getWateringThreshold()));
        JTextField moist1OverrideField = new JTextField(Float.toString(SerialService.getInstance().getMoisture1Override()));
        JTextField moist2OverrideField = new JTextField(Float.toString(SerialService.getInstance().getMoisture2Override()));
        JTextField wateringTimeField = new JTextField(Float.toString(SerialService.getInstance().getWateringTime()));
        JTextField sundownWaterStopField = new JTextField(Float.toString(SerialService.getInstance().getSundownWaterStop()));
        JTextField sunupWaterStartField = new JTextField(Float.toString(SerialService.getInstance().getSunupWaterStart()));

        wateringThresholdField.setMaximumSize(new Dimension(200, 25));
        moist1OverrideField.setMaximumSize(new Dimension(200, 25));
        moist2OverrideField.setMaximumSize(new Dimension(200, 25));
        wateringTimeField.setMaximumSize(new Dimension(200, 25));
        sundownWaterStopField.setMaximumSize(new Dimension(200, 25));
        sunupWaterStartField.setMaximumSize(new Dimension(200, 25));

        wateringThresholdField.setAlignmentX(Component.CENTER_ALIGNMENT);
        moist1OverrideField.setAlignmentX(Component.CENTER_ALIGNMENT);
        moist2OverrideField.setAlignmentX(Component.CENTER_ALIGNMENT);
        wateringTimeField.setAlignmentX(Component.CENTER_ALIGNMENT);
        sundownWaterStopField.setAlignmentX(Component.CENTER_ALIGNMENT);
        sunupWaterStartField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logPathLabel = new JLabel("Log Folder: " + SerialService.getInstance().getLogFolder().getAbsolutePath());
        logPathLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton browseBtn = new JButton("Change Log Folder");
        browseBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton savePortBtn = new JButton("Save Port");
        savePortBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton saveValuesBtn = new JButton("Save Values");
        saveValuesBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backBtn = new JButton("Back");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        savePortBtn.addActionListener(e -> {
            String selected = (String) portList.getSelectedItem();
            if (selected != null) {
                SerialService.getInstance().setPort(selected);
                JOptionPane.showMessageDialog(this, "Port set to " + selected);
                SerialService.getInstance().scheduleInitialGM1Command();
            }
        });

        saveValuesBtn.addActionListener(e -> {
            try {
                float threshold = Float.parseFloat(wateringThresholdField.getText().trim());
                float override1 = Float.parseFloat(moist1OverrideField.getText().trim());
                float override2 = Float.parseFloat(moist2OverrideField.getText().trim());
                float waterTime = Float.parseFloat(wateringTimeField.getText().trim());
                float sundownTime = Float.parseFloat(sundownWaterStopField.getText().trim());
                float sunupTime = Float.parseFloat(sunupWaterStartField.getText().trim());

                SerialService.getInstance().setWateringThreshold(threshold);
                SerialService.getInstance().setMoisture1Override(override1);
                SerialService.getInstance().setMoisture2Override(override2);
                SerialService.getInstance().setWateringTime(waterTime);
                SerialService.getInstance().setSundownWaterStop(sundownTime);
                SerialService.getInstance().setSunupWaterStart(sunupTime);

                JOptionPane.showMessageDialog(this, "Threshold values saved successfully.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers for thresholds and overrides.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> {
            previous.setVisible(true);
            dispose();
        });

        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Select Log Folder");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = chooser.getSelectedFile();
                SerialService.getInstance().setCustomLogFolder(selectedFolder);
                logPathLabel.setText("Log Folder: " + selectedFolder.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Log folder updated.");
            }
        });

        // Layout
        add(Box.createVerticalStrut(10));
        add(portLabel);
        add(infoLabel);
        add(Box.createVerticalStrut(5));
        add(portList);
        add(Box.createVerticalStrut(5));
        add(savePortBtn);
        add(Box.createVerticalStrut(10));
        add(logPathLabel);
        add(Box.createVerticalStrut(5));
        add(browseBtn);
        add(Box.createVerticalStrut(15));
        add(createLabeledField("Watering Threshold (%):", wateringThresholdField));
        add(Box.createVerticalStrut(10));
        add(createLabeledField("Moisture Sensor 1 Override (%):", moist1OverrideField));
        add(Box.createVerticalStrut(10));
        add(createLabeledField("Moisture Sensor 2 Override (%):", moist2OverrideField));
        add(Box.createVerticalStrut(10));
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); // Make it stretch full width and thin
        add(separator);
        add(Box.createVerticalStrut(10));
        add(createLabeledField("Watering Time (Min):", wateringTimeField));
        add(Box.createVerticalStrut(10));
        add(createLabeledField("Sunset Water Stop Time (Hour of Day):", sundownWaterStopField));
        add(Box.createVerticalStrut(10));
        add(createLabeledField("Sunrise Water Start Time (Hour of Day):", sunupWaterStartField));
        add(Box.createVerticalStrut(10));
        add(saveValuesBtn);
        add(Box.createVerticalStrut(10));
        add(backBtn);

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

    private JPanel createLabeledField(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(textField);

        return panel;
    }
}