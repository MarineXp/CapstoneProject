import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Main Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1, 10, 10));

        JButton monitorBtn = new JButton("Serial Monitor");
        JButton settingsBtn = new JButton("Settings");
        JButton summaryBtn = new JButton("Moisture Summary");

        monitorBtn.addActionListener(e -> {
            new SerialMonitorScreen(this);
            setVisible(false);
        });

        settingsBtn.addActionListener(e -> {
            new SettingsScreen(this);
            setVisible(false);
        });

        summaryBtn.addActionListener(e -> {
            new MoistureSummaryScreen(this);
            setVisible(false);
        });

        add(new JLabel("Smart Irrigation System", SwingConstants.CENTER));
        add(monitorBtn);
        add(settingsBtn);
        add(summaryBtn);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}

