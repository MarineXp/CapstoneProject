package com.SmartIrrigationSystemApp.styling;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {
    public enum Theme {
        LIGHT, DARK
    }

    private static Theme currentTheme = Theme.LIGHT;

    public static void applyTheme(JFrame frame) {
        Color bg, fg, btnBg;
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 20);

        if (currentTheme == Theme.DARK) {
            bg = new Color(101, 101, 101);        // dark background
            fg = new Color(220, 220, 220);     // light text
            btnBg = new Color(70, 73, 75);     // dark button
        } else {
            bg = new Color(139, 198, 252);     // light background
            fg = Color.BLACK;
            btnBg = new Color(173, 216, 230);  // light button
        }

        // Apply to root pane
        frame.getContentPane().setBackground(bg);
        updateComponentStyles(frame.getContentPane(), bg, fg, btnBg, labelFont);
    }

    private static void updateComponentStyles(Component comp, Color bg, Color fg, Color btnBg, Font font) {
        if (comp instanceof JPanel || comp instanceof JScrollPane || comp instanceof Box) {
            comp.setBackground(bg);
        }

        if (comp instanceof JLabel || comp instanceof JButton || comp instanceof JTextField || comp instanceof JTextArea) {
            comp.setFont(font);
            comp.setForeground(fg);
        }

        if (comp instanceof JButton) {
            comp.setBackground(btnBg);
        }

        if (comp instanceof JTextField || comp instanceof JTextArea) {
            comp.setBackground(Color.WHITE); // keep white for readability
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                updateComponentStyles(child, bg, fg, btnBg, font);
            }
        }
    }

    public static void toggleTheme(JFrame frame) {
        currentTheme = (currentTheme == Theme.DARK) ? Theme.LIGHT : Theme.DARK;
        JOptionPane.showMessageDialog(frame, "Switched to " + currentTheme + " mode.\nPlease reopen this window.");
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }
}
