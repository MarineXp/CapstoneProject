package com.SmartIrrigationSystemApp.ui;

import com.SmartIrrigationSystemApp.styling.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.Serial;

public class CommandScreen extends JFrame {
    private JTextField commandField;

    private final String[] availableCommands = {
            "Open Valve = OV", "Close Valve = CV", "Get Moisture From Sensor 1 = GM1", "Get Moisture From Sensor 2 = GM2",
            "Get Light = GL", "Get Soil Temperature in °F = GT", "Get all Sensor Values = GA","Force Folder Creation for Day = ForceFolder",
            "Force Log of Sensors = ForceLog"
    };

    public CommandScreen(JFrame previous) {
        setTitle("Send Command");
        setSize(400, 250);
        ThemeManager.applyTheme(this);
        addThemeMenu();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== Menu Bar =====
        JMenuBar menuBar = new JMenuBar();
        JMenu commandMenu = new JMenu("Command List");

        JPanel commandListPanel = new JPanel();
        commandListPanel.setLayout(new BoxLayout(commandListPanel, BoxLayout.Y_AXIS));
        commandListPanel.setOpaque(false);

        for (String cmd : availableCommands) {
            JLabel cmdLabel = new JLabel(cmd);
            cmdLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
            commandListPanel.add(cmdLabel);
        }

        JScrollPane scrollPane = new JScrollPane(commandListPanel);
        scrollPane.setPreferredSize(new Dimension(200, 150));

        JMenuItem listItem = new JMenuItem("Show Commands");
        listItem.addActionListener(e -> JOptionPane.showMessageDialog(this, scrollPane, "Available Commands", JOptionPane.PLAIN_MESSAGE));
        commandMenu.add(listItem);
        menuBar.add(commandMenu);
        setJMenuBar(menuBar);

        // ===== Main content panel =====
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // ==== New Buttons ====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setOpaque(false);
        JButton openValveBtn = new JButton("Open Valve");
        JButton closeValveBtn = new JButton("Close Valve");

        openValveBtn.addActionListener(e -> sendRawCommand("COM:OV\n"));
        closeValveBtn.addActionListener(e -> sendRawCommand("COM:CV\n"));
        buttonPanel.add(openValveBtn);
        buttonPanel.add(closeValveBtn);

        // ==== Command entry ====
        JLabel label = new JLabel("Enter Command to Send:");
        label.setAlignmentX(CENTER_ALIGNMENT);

        commandField = new JTextField();
        commandField.setMaximumSize(new Dimension(250, 30));
        commandField.setAlignmentX(CENTER_ALIGNMENT);

        JButton sendBtn = new JButton("Send");
        sendBtn.setAlignmentX(CENTER_ALIGNMENT);

        JButton backBtn = new JButton("Back");
        backBtn.setAlignmentX(CENTER_ALIGNMENT);

        sendBtn.addActionListener(e -> sendCommand());
        backBtn.addActionListener(e -> {
            previous.setVisible(true);
            dispose();
        });

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(buttonPanel); // Added buttons here
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(label);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(commandField);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(sendBtn);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(backBtn);
        contentPanel.add(Box.createVerticalGlue());

        add(contentPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);

        if (SerialService.getInstance().isLeakDetected()) {
            addLeakIndicator();
        } else {
            SerialService.getInstance().setOnLeakUIUpdate(this::addLeakIndicator);
        }
    }

    private void sendCommand() {
        String raw = commandField.getText().trim();
        if (raw.isEmpty()) return;
        if (raw.equals("ForceFolder") || raw.equals("ForceLog")) {
            if (raw.equals("ForceLog")) {
                SerialService.getInstance().addLog();
                JOptionPane.showMessageDialog(this, "Sent: ForceLog");
            } else if (raw.equals("ForceFolder")) {
                SerialService.getInstance().createDayLogFile();
                JOptionPane.showMessageDialog(this, "Sent: ForceFolder");
            }
        } else {
            sendRawCommand("COM:" + raw + "\n");
        }
    }

    private void sendRawCommand(String message) {
        SerialService service = SerialService.getInstance();
        if (service.getSerialPort() != null && service.getSerialPort().isOpen()) {
            try {
                OutputStream out = service.getSerialPort().getOutputStream();
                out.write(message.getBytes());
                out.flush();
                JOptionPane.showMessageDialog(this, "Sent: " + message);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error sending command: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Serial port not open. Set it in settings first.");
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
