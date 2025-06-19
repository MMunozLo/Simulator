package com.smartcity.parking.simulator.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String PARKING_SPOT_EXCHANGE = "parking.spot.exchange";
    public static final String PARKING_SPOT_ROUTING_KEY = "parking.spot.key";
    public static final String PARKING_SPOT_QUEUE = "parking.simulator.queue"; // Match with PK4U-backend

    @Bean
    public Queue queue() {
        return new Queue(PARKING_SPOT_QUEUE, false); // false = non-durable to match PK4U-backend
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(PARKING_SPOT_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(PARKING_SPOT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
