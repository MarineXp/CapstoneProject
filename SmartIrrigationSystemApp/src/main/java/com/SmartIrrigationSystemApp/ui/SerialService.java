package com.SmartIrrigationSystemApp.ui;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.function.Consumer;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.io.File;


public class SerialService {
    private static SerialService instance;
    private SerialPort serialPort;
    private Thread readerThread;
    private boolean running = false;
    private String selectedPort = null;

    private Consumer<String> onDataReceived;
    private Consumer<String> onMoistureUpdate1;
    private Consumer<String> onMoistureUpdate2;
    private Consumer<String> onLightUpdate;
    private Consumer<String> onTemperatureUpdate;
    private Consumer<String> onTtnuUpdate;

    private String latestMoisture1 = "N/A";
    private String latestMoisture2 = "N/A";
    private String latestLight = "N/A";
    private String latestTemperature = "N/A";
    private String timeTillNextUpdate = "30";
    private boolean watering = false;
    private boolean startedWatering = false;
    private float wateringThreshold = 15f;
    private float moisture1Override = 5f;
    private float moisture2Override = 5f;
    private float sundownWaterStop = 20f;
    private float sunupWaterStart = 8f;

    private boolean leakDetected = false;
    private Runnable onLeakUIUpdate = null;

    private File customLogFolder = null;
    private float wateringTime = 10;

    private SerialService() {}

    public static SerialService getInstance() {
        if (instance == null) instance = new SerialService();
        return instance;
    }

    public void setPort(String portName) {
        stop();
        this.selectedPort = portName;
        start();
    }

    public void start() {
        if (selectedPort == null) return;

        serialPort = SerialPort.getCommPort(selectedPort);
        serialPort.setBaudRate(9600);
        serialPort.flushIOBuffers();

        if (serialPort.openPort()) {
            running = true;
            readerThread = new Thread(() -> {
                try (InputStream in = serialPort.getInputStream()) {
                    while (running) {
                        int available = serialPort.bytesAvailable();
                        if (available > 0) {
                            byte[] buffer = new byte[available];
                            int len = in.read(buffer);
                            if (len > 0) {
                                StringBuilder bufferBuilder = new StringBuilder();
                                while (running) {
                                    available = serialPort.bytesAvailable();
                                    if (available > 0) {
                                        buffer = new byte[available];
                                        len = in.read(buffer);
                                        if (len > 0) {
                                            bufferBuilder.append(new String(buffer, 0, len));

                                            int newlineIndex;
                                            while ((newlineIndex = bufferBuilder.indexOf("\n")) != -1) {
                                                String line = bufferBuilder.substring(0, newlineIndex).trim();
                                                bufferBuilder.delete(0, newlineIndex + 1);

                                                // Filter: only process if line starts with "ACKM:"
                                                if (line.startsWith("ACKM:")) {
                                                    if (onDataReceived != null) onDataReceived.accept(line + "\n");

                                                    // Handle embedded data logic inside ACKM line
                                                    if (line.contains("MOIST1:")) {
                                                        String moisture = line.substring(line.indexOf("MOIST1:") + 7).trim();
                                                        latestMoisture1 = moisture;
                                                        if (onMoistureUpdate1 != null)
                                                            onMoistureUpdate1.accept(moisture);
                                                    } else if (line.contains("MOIST2:")) {
                                                        String moisture = line.substring(line.indexOf("MOIST2:") + 7).trim();
                                                        latestMoisture2 = moisture;
                                                        if (onMoistureUpdate2 != null)
                                                            onMoistureUpdate2.accept(moisture);
                                                    } else if (line.contains("LIGHT:")) {
                                                        String light = line.substring(line.indexOf("LIGHT:") + 6).trim();
                                                        latestLight = light;
                                                        if (onLightUpdate != null) onLightUpdate.accept(light);
                                                    } else if (line.contains("LEAK")) {
                                                        if (!leakDetected) { // Prevent spamming
                                                            leakDetected = true;
                                                            showLeakErrorPopup();
                                                            logLeakToFile();
                                                            if (onLeakUIUpdate != null) onLeakUIUpdate.run();
                                                        }
                                                    } else if (line.contains("TEMP:")) {
                                                        String temperature = line.substring(line.indexOf("TEMP:") + 5).trim();
                                                        latestTemperature = temperature;
                                                        if (onTemperatureUpdate != null)
                                                            onTemperatureUpdate.accept(temperature);
                                                    } else if (line.contains("TIME")) {
                                                        java.time.LocalDateTime now = java.time.LocalDateTime.now();
                                                        String reply = String.format("TIME:%d,%02d,%02d,%02d,%02d,%02d\n",
                                                                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                                                                now.getHour(), now.getMinute(), now.getSecond());
                                                        try {
                                                            OutputStream out = serialPort.getOutputStream();
                                                            out.write(reply.getBytes());
                                                            out.flush();
                                                        } catch (Exception ex) {
                                                            System.err.println("Failed to send time: " + ex.getMessage());
                                                        }
                                                        continue;
                                                    } else if (line.contains("DayChange:")) {
                                                        if (line.contains("1")) {
                                                            createDayLogFile();
                                                        }
                                                    } else if (line.contains("LOG")) {
                                                        java.time.LocalDateTime now = java.time.LocalDateTime.now();
                                                        int hour = now.getHour();
                                                        boolean inAllowedTimeWindow = (sundownWaterStop > sunupWaterStart)
                                                                ? hour >= sunupWaterStart && hour < sundownWaterStop
                                                                : hour >= sunupWaterStart || hour < sundownWaterStop;
                                                        if (((getMeanMoisture() < wateringThreshold && getMeanMoisture() != -1
                                                                && !watering) || ((Float.valueOf(getLatestMoisture(1)) < moisture1Override
                                                                || Float.valueOf(getLatestMoisture(2)) < moisture2Override)) && !watering)
                                                                && inAllowedTimeWindow) {
                                                            try {
                                                                OutputStream out = serialPort.getOutputStream();
                                                                out.write("COM:OV\n".getBytes());
                                                                out.flush();
                                                                watering = true;
                                                            } catch (IOException e) {
                                                                System.err.println("Failed to send COM:OV: " + e.getMessage());
                                                            }
                                                        }

                                                        addLog();

                                                        if (watering && !startedWatering) {
                                                            startedWatering = true;
                                                            new Thread(() -> {
                                                                try {
                                                                    Thread.sleep((long) wateringTime * 60 * 1000); // 10 minutes in milliseconds

                                                                    // Send COM:CV to close valve
                                                                    try {
                                                                        OutputStream out = serialPort.getOutputStream();
                                                                        out.write("COM:CV\n".getBytes());
                                                                        out.flush();
                                                                        watering = false;
                                                                        startedWatering = false;
                                                                    } catch (IOException e) {
                                                                        System.err.println("Failed to send COM:CV: " + e.getMessage());
                                                                    }

                                                                } catch (InterruptedException ignored) {
                                                                }
                                                            }).start();
                                                        }

                                                    } else if (line.contains("TTNU:")) {
                                                        String TTNU = line.substring(line.indexOf("TTNU:") + 5).trim();
                                                        timeTillNextUpdate = TTNU;
                                                        if (onTtnuUpdate != null)
                                                            onTtnuUpdate.accept(TTNU);
                                                    } else {
                                                        leakDetected = false;
                                                    }
                                                }

                                                if (line.startsWith("ACKS:")) {
                                                    if (onDataReceived != null) onDataReceived.accept(line + "\n");
                                                }
                                            }
                                        }
                                    }

                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException ignored) {}
                                }

                            }
                        }
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    if (onDataReceived != null) onDataReceived.accept("Error: " + e.getMessage());
                }
            });
            readerThread.start();
        }
    }

    public void stop() {
        running = false;
        if (serialPort != null && serialPort.isOpen()) serialPort.closePort();
    }

    public void setOnDataReceived(Consumer<String> listener) {
        this.onDataReceived = listener;
    }

    public void setOnMoistureUpdate(Consumer<String> listener, int sensor) {
        if (sensor == 1) {
            this.onMoistureUpdate1 = listener;
        } else if (sensor == 2) {
            this.onMoistureUpdate2 = listener;
        }
    }

    public void setOnLightUpdate(Consumer<String> listener) {
        this.onLightUpdate = listener;
    }

    public void setOnTemperatureUpdate(Consumer<String> listener) {
        this.onTemperatureUpdate = listener;
    }

    public void setOnLeakUIUpdate(Runnable callback) {
        this.onLeakUIUpdate = callback;
    }

    public void setOnTimeTillNextUpdate(Consumer<String> listener) {
        this.onTtnuUpdate = listener;
    }

    public String getLatestMoisture(int sensor) {
        if (sensor == 1) {
            return latestMoisture1;
        } else if (sensor == 2) {
            return latestMoisture2;
        }
        return "N/A";
    }

    public String getLatestLight() {
        return latestLight;
    }

    public String getLatestTemperature() {
        return latestTemperature;
    }

    public String getTimeTillNextUpdate() {
        return timeTillNextUpdate;
    }

    public String[] getAvailablePorts() {
        return Arrays.stream(SerialPort.getCommPorts())
                .map(SerialPort::getSystemPortName)
                .toArray(String[]::new);
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public int getMeanMoisture() {
        if (!latestMoisture1.equals("N/A") && !latestMoisture2.equals("N/A")) {
            int meanMoisture = -1;
            try {
                double moistOne = Double.parseDouble(latestMoisture1);
                double moistTwo = Double.parseDouble(latestMoisture2);
                double moistMean = (moistOne + moistTwo) / 2;
                meanMoisture = (int) moistMean;
            } catch (NumberFormatException ignored) {}
            return meanMoisture;
        }
        return(-1);
    }

    private void showLeakErrorPopup() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                    "⚠ Leak detected!\nPlease check the irrigation system immediately.",
                    "Leak Alert",
                    JOptionPane.ERROR_MESSAGE);
        });
    }

    private void logLeakToFile() {
        try {
            // Get user's home directory and build Documents path
            String userHome = System.getProperty("user.home");
            File logDir = getLogFolder();

            // Create directory if it doesn't exist
            if (!logDir.exists()) {
                if (!logDir.mkdirs()) {
                    System.err.println("Failed to create log directory: " + logDir.getAbsolutePath());
                    return;
                }
            }

            // Create or append to leak_log.txt
            File logFile = new File(logDir, "leak_log.txt");
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write("Leak detected at " + LocalDateTime.now() + "\n");
            }

        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }


    public boolean isLeakDetected() {
        return leakDetected;
    }

    public void createDayLogFile() {
        // Build log folder path
        String userHome = System.getProperty("user.home");
        File logDir = getLogFolder();

        // Create folder if it doesn't exist
        if (!logDir.exists()) {
            if (!logDir.mkdirs()) {
                System.err.println("Failed to create log directory.");
                return;
            }
        }

        // Create file name with date format LOGGERyyyy-MM-dd.csv
        String date = java.time.LocalDate.now().toString(); // e.g., 2025-04-10
        File logFile = new File(logDir, "LOGGER" + date + ".csv");

        // If file does not exist, write header line
        if (!logFile.exists()) {
            try (FileWriter writer = new FileWriter(logFile)) {
                writer.write("time,date,Moisture Sensor 1 (%),Moisture Sensor 2 (%),Moisture Mean (%),Light Sensor (%),Soil Temperature (°F),Watering\n");
            } catch (IOException e) {
                System.err.println("Failed to create log file: " + e.getMessage());
            }
        }
    }

    public void tryAutoConnect() {
        String autoPortName = "cu.usbmodem1101"; // macOS USB serial port
        String[] availablePorts = getAvailablePorts();

        for (String port : availablePorts) {
            if (port.equals(autoPortName)) {
                System.out.println("Attempting auto-connect to " + port);
                setPort(autoPortName);

                // Notify user of successful auto-connect
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null,
                            "Auto-connect to Arduino on '" + autoPortName + "' was successful!",
                            "Connection Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                });

                scheduleInitialGM1Command();

                return;
            }
        }

        // If not found, notify user to use settings
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                    "Could not automatically connect to Arduino on '" + autoPortName + "'.\nPlease go to Settings to select the correct COM port.",
                    "Connection Failed",
                    JOptionPane.WARNING_MESSAGE);
        });
    }

    public void scheduleInitialGM1Command() {
        new Thread(() -> {
            try {
                Thread.sleep(5 * 1000); // Wait 30 seconds
                if (serialPort != null && serialPort.isOpen()) {
                    OutputStream out = serialPort.getOutputStream();
                    out.write("COM:GM1\n".getBytes());
                    out.flush();
                    System.out.println("Sent COM:GM1 after auto-connect delay.");
                }
            } catch (Exception e) {
                System.err.println("Failed to send COM:GM1 after delay: " + e.getMessage());
            }
        }).start();
    }

    public float getWateringThreshold() {
        return wateringThreshold;
    }

    public float getMoisture1Override() {
        return moisture1Override;
    }

    public float getMoisture2Override() {
        return moisture2Override;
    }

    public float getSundownWaterStop() {
        return sundownWaterStop;
    }

    public float getSunupWaterStart() {
        return sunupWaterStart;
    }

    public void setWateringThreshold(float value) {
        wateringThreshold = value;
    }

    public void setMoisture1Override(float value) {
        moisture1Override = value;
    }

    public void setMoisture2Override(float value) {
        moisture2Override = value;
    }

    public void setSunupWaterStart(float value) {
        sunupWaterStart = value;
    }

    public void setSundownWaterStop(float value) {
        sundownWaterStop = value;
    }

    public void addLog() {
        String userHome = System.getProperty("user.home");
        File logDir = getLogFolder();
        String date = java.time.LocalDate.now().toString();
        File logFile = new File(logDir, "LOGGER" + date + ".csv");

        if (!logFile.exists()) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                        "Log file not found for today (" + logFile.getName() + ").\nPlease trigger logging initialization first.",
                        "Logging Error",
                        JOptionPane.ERROR_MESSAGE);
            });

            // Just skip this specific line — DO NOT exit the whole thread
            return; // skips to next serial line, keeps serial thread alive
        }

        String time = java.time.LocalTime.now().withNano(0).toString();
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(String.format("%s,%s,%s,%s,%d,%s,%s,%b\n",
                    time,
                    date,
                    getLatestMoisture(1),
                    getLatestMoisture(2),
                    getMeanMoisture(),
                    getLatestLight(),
                    getLatestTemperature(),  // make sure you have this field or getter
                    watering));
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    public File getLogFolder() {
        if (customLogFolder != null) return customLogFolder;
        return new File(System.getProperty("user.home"), "Documents/IrrigationSystemLogs");
    }

    public void setCustomLogFolder(File folder) {
        this.customLogFolder = folder;
    }

    public float getWateringTime() {
        return wateringTime;
    }

    public void setWateringTime(float value) {
        this.wateringTime = value;
    }
}

