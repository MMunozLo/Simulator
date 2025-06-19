package com.smartcity.parking.simulator.service;

import com.smartcity.parking.simulator.model.ParkingSpotUpdate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(baseUrl + "/api/v1/parkings", List.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                List<Map<String, Object>> parkings = response.getBody();
                if (parkings != null) {
                    for (Map<String, Object> parking : parkings) {
                        String parkingId = (String) parking.get("id");
                        ResponseEntity<List> spotResponse = restTemplate.getForEntity(
                                baseUrl + "/api/v1/parkings/" + parkingId + "/spots", List.class);
                        List<Map<String, Object>> spots = spotResponse.getBody();
                        if (spots != null) {
                            for (Map<String, Object> spot : spots) {
                                String spotId = (String) spot.get("id");
                                Integer levelObj = (Integer) spot.get("level");
                                String level = levelObj != null ? levelObj.toString() : "0";
                                boolean newStatus = random.nextBoolean();
                                ParkingSpotUpdate update = new ParkingSpotUpdate();
                                update.setParkingId(parkingId);
                                update.setId(spotId);
                                update.setLevel(level);
                                update.setOccupied(newStatus);
                                rabbitTemplate.convertAndSend("parking.spot.exchange", "parking.spot.key", update);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Aquí podrías agregar un manejo de errores más robusto, como registrar el error o enviar una alerta.
        }
    }
}
