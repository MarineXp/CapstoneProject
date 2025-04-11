package com.SmartIrrigationSystemApp.ui;

import javax.swing.*;

public class SerialMonitorScreen extends JFrame {
    private static SerialMonitorScreen instance = null;

    private JTextArea outputArea;

    private SerialMonitorScreen() {
        setTitle("Serial Monitor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, "Center");

        // Set up listener
        SerialService.getInstance().setOnDataReceived(data -> SwingUtilities.invokeLater(() -> {
            outputArea.append(data);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        }));

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                instance = null; // Allow reopening after window is closed
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void showMonitor() {
        if (instance == null) {
            instance = new SerialMonitorScreen();
        } else {
            instance.toFront();
            instance.requestFocus();
        }
    }
}