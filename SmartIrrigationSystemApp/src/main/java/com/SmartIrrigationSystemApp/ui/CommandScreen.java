package com.SmartIrrigationSystemApp.ui;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;

public class CommandScreen extends JFrame {
    private JTextField commandField;

    private final String[] availableCommands = {
            "Open Valve = OV", "Close Valve = CV", "Get Moisture = GM"
    };

    public CommandScreen(JFrame previous) {
        setTitle("Send Command");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== Menu Bar =====
        JMenuBar menuBar = new JMenuBar();
        JMenu commandMenu = new JMenu("Command List");

        JPanel commandListPanel = new JPanel();
        commandListPanel.setLayout(new BoxLayout(commandListPanel, BoxLayout.Y_AXIS));

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

        // ==== New Buttons ====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
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
    }

    private void sendCommand() {
        String raw = commandField.getText().trim();
        if (raw.isEmpty()) return;
        sendRawCommand("COM:" + raw + "\n");
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
}
