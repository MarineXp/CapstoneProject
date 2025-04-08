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
    private Consumer<String> onMoistureUpdate;

    private String latestMoisture = "N/A";

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

                                                if (onDataReceived != null) onDataReceived.accept(line + "\n");

                                                if (line.startsWith("MOIST1:")) {
                                                    String moisture = line.substring(7).trim();
                                                    latestMoisture = moisture;
                                                    if (onMoistureUpdate != null) onMoistureUpdate.accept(moisture);
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

    public void setOnMoistureUpdate(Consumer<String> listener) {
        this.onMoistureUpdate = listener;
    }

    public String getLatestMoisture() {
        return latestMoisture;
    }

    public String[] getAvailablePorts() {
        return Arrays.stream(SerialPort.getCommPorts())
                .map(SerialPort::getSystemPortName)
                .toArray(String[]::new);
    }

}

