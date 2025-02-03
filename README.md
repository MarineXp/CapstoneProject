# Smart Irrigation System

## Overview
The **Smart Irrigation System** is an IoT-based project designed to optimize water usage in agriculture by monitoring soil moisture, rainfall, and sunlight levels. The system ensures efficient watering by using sensors to determine when and how much to irrigate.

## Features
- **Automated Watering**: Uses soil moisture data to control the irrigation process.
- **Weather Awareness**: A raindrop sensor prevents watering when it is raining.
- **Optimized Scheduling**: A photoelectric sensor ensures watering is done at optimal times to reduce evaporation.
- **Energy Efficient**: Operates on a low-power microcontroller.
- **Scalability**: Can be expanded with additional sensors or integrated with cloud services for remote monitoring.

## Components
| Component             | Description                                          |
|----------------------|--------------------------------------------------|
| **Microcontroller**  | Arduino Uno (or compatible board)                 |
| **Soil Moisture Sensor** | Measures the moisture content in the soil         |
| **Raindrop Sensor**  | Detects rainfall to prevent unnecessary watering  |
| **Photoelectric Sensor** | Determines sunlight levels for optimal watering timing |
| **Water Pump**       | Activates based on sensor data                     |
| **Relay Module**     | Controls the water pump                            |
| **Power Source**     | 3.7V Battery or external power supply              |

## How It Works
1. The soil moisture sensor constantly monitors soil moisture levels.
2. If the soil moisture is below a predefined threshold, the system checks the raindrop and photoelectric sensors.
3. If no rain is detected and the light conditions are suitable, the relay module activates the water pump.
4. The pump irrigates the soil until the moisture reaches an optimal level, then turns off automatically.

## Future Enhancements
- **IoT Integration**: Connect the system to a cloud platform for remote monitoring.
- **Machine Learning**: Use predictive analytics to enhance irrigation schedules.
- **Solar Power**: Implement solar panels for sustainable operation.

## License
This project is open-source and available under the MIT License.

---

Developed by **Robert Cunningham** & TOR
