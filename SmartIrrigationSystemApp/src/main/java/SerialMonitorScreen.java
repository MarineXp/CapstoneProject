import javax.swing.*;

public class SerialMonitorScreen extends JFrame {
    private JTextArea outputArea;

    public SerialMonitorScreen(JFrame previous) {
        setTitle("Serial Monitor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> {
            previous.setVisible(true);
            dispose();
        });

        add(scrollPane, "Center");
        add(backBtn, "South");

        SerialService.getInstance().setOnDataReceived(data -> SwingUtilities.invokeLater(() -> {
            outputArea.append(data);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        }));

        setLocationRelativeTo(null);
        setVisible(true);
    }
}

