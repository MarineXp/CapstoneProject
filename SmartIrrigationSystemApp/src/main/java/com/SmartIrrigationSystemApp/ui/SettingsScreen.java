package com.SmartIrrigationSystemApp.ui;

import com.SmartIrrigationSystemApp.styling.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class SettingsScreen extends JFrame {
    public SettingsScreen(JFrame previous) {
        setTitle("Settings");
        setSize(300, 200);
        ThemeManager.applyTheme(this); // Apply current theme
        addThemeMenu(); // Toggle menu
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel portLabel = new JLabel("Select COM Port:");
        portLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<String> portList = new JComboBox<>(SerialService.getInstance().getAvailablePorts());
        portList.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ℹ️ info icon below the label
        JLabel infoLabel = new JLabel("\u2139");
        infoLabel.setToolTipText("Typically the Arduino COM Port is cu.usbmodem1101");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton saveBtn = new JButton("Save");
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backBtn = new JButton("Back");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        saveBtn.addActionListener(e -> {
            String selected = (String) portList.getSelectedItem();
            if (selected != null) {
                SerialService.getInstance().setPort(selected);
                JOptionPane.showMessageDialog(this, "Port set to " + selected);
            }
        });

        backBtn.addActionListener(e -> {
            previous.setVisible(true);
            dispose();
        });

        add(Box.createVerticalStrut(10)); // spacing
        add(portLabel);
        add(infoLabel); // here it is underneath the label
        add(Box.createVerticalStrut(5));
        add(portList);
        add(Box.createVerticalStrut(10));
        add(saveBtn);
        add(Box.createVerticalStrut(5));
        add(backBtn);

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
