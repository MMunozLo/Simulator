package com.smartcity.parking.simulator.service;

import com.smartcity.parking.simulator.config.RabbitConfig;
import com.smartcity.parking.simulator.entity.SimulatedSpot;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class SimulatedSpotService {

    private final RabbitTemplate rabbitTemplate;

    public SimulatedSpotService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void simulateParkingStatusChange(SimulatedSpot update) {
            rabbitTemplate.convertAndSend(RabbitConfig.PARKING_SPOT_QUEUE, update);
        }
    }


