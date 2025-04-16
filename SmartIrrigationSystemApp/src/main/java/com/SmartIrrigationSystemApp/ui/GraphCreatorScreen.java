package com.SmartIrrigationSystemApp.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.SmartIrrigationSystemApp.styling.ThemeManager;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

public class GraphCreatorScreen extends JFrame {
    private JFrame parent;
    private JLabel imagePreview;
    private BufferedImage combinedImage;

    public GraphCreatorScreen(JFrame previous) {
        this.parent = previous;
        setTitle("Graph Creator");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ThemeManager.applyTheme(this);
        addThemeMenu();

        JButton selectFileBtn = new JButton("Select CSV Log File");
        selectFileBtn.addActionListener(e -> selectCSV());

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            parent.setVisible(true);
            dispose();
        });

        JPanel topPanel = new JPanel();
        topPanel.add(selectFileBtn);
        topPanel.add(backBtn);

        imagePreview = new JLabel();
        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(imagePreview), BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void selectCSV() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File csv = chooser.getSelectedFile();
            try {
                generateAndShowGraphs(csv);
            } catch (Exception ex) {
                ex.printStackTrace(); // 👈 add this
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
            }
        }
    }

    private void generateAndShowGraphs(File csvFile) throws Exception {
        List<Date> time = new ArrayList<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        List<Double> moist1 = new ArrayList<>();
        List<Double> moist2 = new ArrayList<>();
        List<Double> moistMean = new ArrayList<>();
        List<Double> light = new ArrayList<>();
        List<Double> temp = new ArrayList<>();
        List<Integer> watering = new ArrayList<>();
        String date = "Unknown Date";

        BufferedReader reader = new BufferedReader(new FileReader(csvFile));
        String header = reader.readLine(); // skip header
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length < 8) continue;

            // Check for "N/A" and skip the row
            boolean hasNA = false;
            for (int i = 2; i <= 6; i++) {
                if (parts[i].trim().equalsIgnoreCase("N/A")) {
                    hasNA = true;
                    break;
                }
            }
            if (hasNA) {
                System.out.println("Skipping row with N/A: " + line);
                continue;
            }

            try {
                try {
                    time.add(timeFormat.parse(parts[0]));
                } catch (Exception ex) {
                    System.out.println("Skipping row with invalid time: " + parts[0]);
                    continue;
                }
                date = parts[1];
                moist1.add(Double.parseDouble(parts[2]));
                moist2.add(Double.parseDouble(parts[3]));
                moistMean.add(Double.parseDouble(parts[4]));
                light.add(Double.parseDouble(parts[5]));
                temp.add(Double.parseDouble(parts[6]));
                watering.add(Boolean.parseBoolean(parts[7]) ? 1 : 0);
            } catch (Exception ex) {
                System.out.println("Failed to parse line: " + line);
            }
        }
        reader.close();

        // Create and combine charts as before
        XYChart chart1 = new XYChartBuilder().width(800).height(300).title("Sensor Data - %").xAxisTitle("Time").yAxisTitle("Percent").build();
        chart1.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart1.addSeries("Moisture 1", time, moist1);
        chart1.addSeries("Moisture 2", time, moist2);
        chart1.addSeries("Moisture Mean", time, moistMean);
        chart1.addSeries("Light", time, light);

        XYChart chart2 = new XYChartBuilder().width(800).height(300).title("Soil Temperature").xAxisTitle("Time").yAxisTitle("Fahrenheit").build();
        chart2.addSeries("Temperature", time, temp);

        XYChart chart3 = new XYChartBuilder().width(800).height(200).title("Watering Events").xAxisTitle("Time").yAxisTitle("Watering").build();
        chart3.addSeries("Watering", time, watering);

        BufferedImage img1 = BitmapEncoder.getBufferedImage(chart1);
        BufferedImage img2 = BitmapEncoder.getBufferedImage(chart2);
        BufferedImage img3 = BitmapEncoder.getBufferedImage(chart3);

        int totalHeight = img1.getHeight() + img2.getHeight() + img3.getHeight();
        int width = Math.max(img1.getWidth(), Math.max(img2.getWidth(), img3.getWidth()));

        combinedImage = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combinedImage.getGraphics();
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, 0, img1.getHeight(), null);
        g.drawImage(img3, 0, img1.getHeight() + img2.getHeight(), null);
        g.dispose();

        imagePreview.setIcon(new ImageIcon(combinedImage));

        String fileName = JOptionPane.showInputDialog(this, "Enter name for PNG file:", "Graph_" + date);
        if (fileName != null && !fileName.trim().isEmpty()) {
            File output = new File(SerialService.getInstance().getLogFolder(), fileName + ".png");
            ImageIO.write(combinedImage, "png", output);
            JOptionPane.showMessageDialog(this, "Graph saved to " + output.getAbsolutePath());
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
}

