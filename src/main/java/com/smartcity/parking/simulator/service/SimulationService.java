package com.smartcity.parking.simulator.service;

import com.smartcity.parking.simulator.model.ParkingSpotUpdate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Service
public class SimulationService {
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final String baseUrl;
    private final Random random = new Random();

    public SimulationService(RestTemplateBuilder builder, RabbitTemplate rabbitTemplate,
                             @Value("${pk4u.base-url}") String baseUrl) {
        this.restTemplate = builder.build();
        this.rabbitTemplate = rabbitTemplate;
        this.baseUrl = baseUrl;
    }

    @Scheduled(fixedDelayString = "${simulator.interval:10}000")
    public void simulate() {
        System.out.println("🔄 Simulación iniciada...");

        try {
            ResponseEntity<List> response = restTemplate.getForEntity(baseUrl + "/api/v1/parkings", List.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> parkings = response.getBody();
                if (!parkings.isEmpty()) {
                    Map<String, Object> parking = parkings.get(random.nextInt(parkings.size()));
                    String parkingId = (String) parking.get("id");

                    ResponseEntity<List> spotResponse = restTemplate.getForEntity(
                            baseUrl + "/api/v1/parkings/" + parkingId + "/spots", List.class);
                    List<Map<String, Object>> spots = spotResponse.getBody();

                    if (spots != null && !spots.isEmpty()) {
                        int maxToUpdate = Math.min(spots.size(), 5); //si hay menos de 5 plazas, usa ese numero
                        int numToUpdate = random.nextInt(maxToUpdate) + 1; // selecciona entre 1 y maxToUpdate

                        Collections.shuffle(spots); // mezcla aleatoriamente la lista

                        for (int i = 0; i < numToUpdate; i++) {
                            Map<String, Object> spot = spots.get(i);
                            String spotId = (String) spot.get("id");
                            boolean newStatus = random.nextBoolean();

                            ParkingSpotUpdate update = new ParkingSpotUpdate();
                            update.setParkingId(parkingId);
                            update.setId(spotId);
                            update.setOccupied(newStatus);

                            rabbitTemplate.convertAndSend("parking.spot.exchange", "parking.spot.key", update);

                            System.out.printf("📤 Plaza actualizada: Parking %s - Plaza %s → Ocupado: %s%n",
                                    parkingId, spotId, newStatus);
                        }
                    } else {
                        System.out.println("⚠️ No se encontraron plazas en el parking.");
                    }
                } else {
                    System.out.println("⚠️ No se encontraron parkings.");
                }
            } else {
                System.out.println("❌ Error al obtener parkings: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("🚨 Error en la simulación:");
            e.printStackTrace();
        }
    }


}
