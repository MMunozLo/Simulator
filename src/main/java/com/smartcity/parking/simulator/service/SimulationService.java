package com.smartcity.parking.simulator.service;

import com.smartcity.parking.simulator.model.ParkingSpotUpdate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class SimulationService {
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final String baseUrl;
    private final Random random = new Random();
    private int simulationCounter = 0;
    private static final int MIN_INTERVAL = 6; // Minimum interval in simulation cycles before updating a spot
    private Map<String, Map<String, Integer>> lastUpdateByParkingAndSpot = new HashMap<>();

    public SimulationService(RestTemplateBuilder builder, RabbitTemplate rabbitTemplate,
                             @Value("${pk4u.base-url}") String baseUrl) {
        this.restTemplate = builder.build();
        this.rabbitTemplate = rabbitTemplate;
        this.baseUrl = baseUrl;
    }

    @Scheduled(fixedDelayString = "${simulator.interval:10}000")
    public void simulate() {
        System.out.println("🔄 Simulación iniciada...");
        simulationCounter++;
        System.out.println("🔄 Simulación #" + simulationCounter);

        try {
            ResponseEntity<List> response = restTemplate.getForEntity(baseUrl + "/api/v1/parkings", List.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> parkings = new ArrayList<>(response.getBody());
                if (!parkings.isEmpty()) {
                    int numParkings = Math.min(3, parkings.size());
                    Collections.shuffle(parkings);
                    List<Map<String, Object>> selectedParkings = parkings.subList(0, numParkings);

                    for (Map<String, Object> parking : selectedParkings) {
                        String parkingId = (String) parking.get("id");
                        ResponseEntity<List> spotResponse = restTemplate.getForEntity(
                                baseUrl + "/api/v1/parkings/" + parkingId + "/spots", List.class);
                        List<Map<String, Object>> spots = spotResponse.getBody();

                        if (spots != null && !spots.isEmpty()) {
                            Map<String, Integer> lastUpdates = lastUpdateByParkingAndSpot
                                    .computeIfAbsent(parkingId, k -> new HashMap<>());
                            List<Map<String, Object>> availableSpots = spots.stream()
                                    .filter(spot -> {
                                        String spotId = (String) spot.get("id");
                                        Integer last = lastUpdates.get(spotId);
                                        return last == null || simulationCounter - last >= MIN_INTERVAL;
                                    })
                                    .toList();

                            if (!availableSpots.isEmpty()) {
                                List<Map<String, Object>> mutableAvailableSpots = new ArrayList<>(availableSpots);
                                int maxToUpdate = Math.min(mutableAvailableSpots.size(), 3);
                                int numToUpdate = random.nextInt(maxToUpdate) + 1;
                                Collections.shuffle(mutableAvailableSpots);

                                for (int i = 0; i < numToUpdate; i++) {
                                    Map<String, Object> spot = mutableAvailableSpots.get(i);
                                    String spotId = (String) spot.get("id");
                                    boolean newStatus = random.nextBoolean();

                                    ParkingSpotUpdate update = new ParkingSpotUpdate();
                                    update.setParkingId(parkingId);
                                    update.setId(spotId);
                                    update.setOccupied(newStatus);

                                    rabbitTemplate.convertAndSend("parking.spot.exchange", "parking.spot.key", update);

                                    lastUpdates.put(spotId, simulationCounter);

                                    System.out.printf("📤 Parking %s - Plaza %s → Ocupado: %s%n",
                                            parkingId, spotId, newStatus);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("🚨 Error en la simulación:");
            e.printStackTrace();
        }
    }
}
