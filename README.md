<div align="center">
  <img src="https://github.com/Jefffer/pk4u-frontend/blob/main/public/logo-transparent.png?raw=true" alt="PK4U Logo" width="80"/>
  <h1>PK4U: Parking for You - IoT Simulator</h1>
  <p>
    <em>Emulates IoT sensor behavior for real-time data generation.</em>
  </p>
  <p>
    <img src="https://img.shields.io/badge/Java-21-blue.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
    <img src="https://img.shields.io/badge/Spring_Boot-3-green.svg?style=for-the-badge&logo=spring&logoColor=green" alt="Spring Boot 3"/>
    <img src="https://img.shields.io/badge/RabbitMQ-blue.svg?style=for-the-badge&logo=rabbitmq&logoColor=FF6600" alt="RabbitMQ"/>
    <img src="https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge" alt="License: MIT"/>
  </p>
</div>

This repository contains the **IoT Simulator** for the PK4U system. This service is a critical component for testing and demonstration, designed to mimic the behavior of real-world IoT sensors in parking lots. Its main responsibilities are:

-   Periodically fetching the list of all available parkings from the **PK4U Backend**.
-   Randomly selecting parking spots and changing their occupancy status (from free to occupied and vice versa).
-   Publishing these status changes as messages to a **RabbitMQ** queue.
-   Registering itself with the **Eureka Server** to be part of the microservice ecosystem.

## 🚀 Local Deployment

Follow these steps to run the Simulator service locally.

### **📋 Prerequisites**

-   **JDK 21** (or the version specified in the `pom.xml`).
-   **Maven** or **Gradle** for dependency management and project building.

### **1. External Dependencies** 📦

For the Simulator to function correctly, it needs to connect to the following services, which must be running first:

-   ✅ **Eureka Server**: To register itself and discover other services.
-   ✅ **PK4U Backend**: To fetch the list of parkings and spots to simulate.
-   ✅ **RabbitMQ**: The message broker where spot status updates will be sent.

### **2. Clone the Repository ⬇️**

```bash
git clone https://github.com/MMunozLo/Simulator.git
cd Simulator
```

### 3. Environment Setup 🛠️
The application's configuration is managed in the `src/main/resources/application.yml` file. Ensure that the connection properties for databases and external services are correct for your local environment.

Example `application.yml`:

```yaml
server:
  port: 8083 # Port for the Simulator service

spring:
  application:
    name: simulator-ms # Service name to register in Eureka
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

# URL of the PK4U Backend to fetch parkings, can be direct or via Gateway
pk4u:
  backend:
    url: http://localhost:8081/api/v1 

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka # URL of your Eureka Server
```

### 4. Running the Application ▶️
You can run the application using the Maven or Gradle plugin.

With Maven:

```bash
mvn spring-boot:run
```

With Gradle:

```bash
./gradlew bootRun
```

Once started, the service will register with **Eureka** and begin periodically sending messages to **RabbitMQ**, simulating real-time parking activity.

### 5. Running the Full System 🌐
Keep in mind that the frontend is only the **presentation layer** of the PK4U system. for full functionality, you need to have all the backend services running.

Make sure to clone and run the following repositories in the recommended order:

| Order | Component             | Description                                                                                          | Repository                                                                    |
| :---: | --------------------- | ---------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------- |
|   1   | **Eureka Server** | Essential service for dynamic discovery and registration of all microservices.                       | [eureka_service_tfm](https://github.com/gecamara/eureka_service_tfm)       |
|   2   | **API Gateway** | Single entry point for all requests, routing traffic from the frontend to the appropriate services.  | [gateway_service](https://github.com/gecamara/gateway_service)         |
|   3   | **PK4U Backend** | The core application that centralizes business logic and communicates with the database.             | [PK4U-backend](https://github.com/MMunozLo/PK4U-backend.git)         |
|   4   | **DB Scripts** | Scripts to initialize the MongoDB database with the required data structure.                         | [pk4u-db-scripts](https://github.com/Jefffer/pk4u-db-scripts)           |
|   5   | **Simulator** | Emulates IoT sensor behavior, generating and sending real-time parking occupancy data.               | [Simulator](https://github.com/MMunozLo/Simulator)                   |
|   6   | **PK4U Frontend** | Presentation layer of the PK4U system developed with React, Vite and TailwindCSS              | [pk4u-frontend](https://github.com/Jefffer/pk4u-frontend)                   |

---
## 🌟 What is PK4U?

In modern cities, finding parking has become a daily challenge that causes stress and unnecessarily increases traffic and pollution. This phenomenon, known as _cruising for parking_, negatively affects the quality of life and urban sustainability.

**PK4U** was created to address this problem by offering an open-source solution that centralizes and displays real-time parking availability in a city. Our platform unifies data from multiple parking facilities into a single interface with interactive maps, empowering drivers to make better decisions and contributing to smarter, more sustainable mobility.

### 💻 Core Technology Stack

| Área                | Tecnologías Clave                                                              |
| ------------------- | ------------------------------------------------------------------------------ |
| **Frontend** | `React` `Vite` `React Router` `Tailwind CSS` `Leaflet` `i18next`                 |
| **Backend** | `Java` `Spring Boot` `Spring Cloud`                                            |
| **Data & Search**| `MongoDB` `Elasticsearch`                                                      |
| **Communication** | `REST API` `RabbitMQ`                                                          |
| **Architecture** | `Microservicios` `API Gateway` `Service Registry (Eureka)`                     |

### 🤝 Contribution
Your help is welcome! If you wish to contribute to this script project, please feel free to:

* Open an **Issue** to report a problem or propose an improvement.
* Open a **Pull Request** with your changes and contributions.

### 📄 License
This project is distributed under an Open Source license, encouraging collaboration and transparency in the development of solutions for Smart Cities.
