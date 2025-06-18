package com.smartcity.parking.simulator.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String PARKING_SPOT_QUEUE = "parking-spot-updates";

    @Bean
    public Queue parkingSpotQueue() {
        return new Queue(PARKING_SPOT_QUEUE, false);
    }
}
