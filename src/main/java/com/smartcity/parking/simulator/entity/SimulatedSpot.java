package com.smartcity.parking.simulator.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "simulated_spots")
@Getter
@Setter
public class SimulatedSpot {

    @Id
    private String id;
    private String parkingId;
    private String level;
    private boolean occupied;


    public SimulatedSpot(String id, String parkingId, String level, boolean occupied) {
        this.id = id;
        this.parkingId = parkingId;
        this.level = level;
        this.occupied = occupied;
    }

}
