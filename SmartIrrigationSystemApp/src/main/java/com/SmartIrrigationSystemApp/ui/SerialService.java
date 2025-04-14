package com.SmartIrrigationSystemApp.ui;

import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Consumer;

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

    private String latestMoisture1 = "N/A";
    private String latestMoisture2 = "N/A";
    private String latestLight = "N/A";

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
                                                        if (onMoistureUpdate1 != null) onMoistureUpdate1.accept(moisture);
                                                    } else if (line.contains("MOIST2:")) {
                                                        String moisture = line.substring(line.indexOf("MOIST2:") + 7).trim();
                                                        latestMoisture2 = moisture;
                                                        if (onMoistureUpdate2 != null) onMoistureUpdate2.accept(moisture);
                                                    } else if (line.contains("LIGHT:")) {
                                                        String light = line.substring(line.indexOf("LIGHT:") + 7).trim();
                                                        latestLight = light;
                                                        if (onLightUpdate != null) onLightUpdate.accept(light);
                                                    } else if (line.contains("LEAK")) {
                                                        // Add logic to put up an error
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
}

