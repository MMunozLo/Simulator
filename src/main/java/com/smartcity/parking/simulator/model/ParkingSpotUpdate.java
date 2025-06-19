package com.smartcity.parking.simulator.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;

@Document(collection = "simulated_spots")
@Getter
@Setter
public class ParkingSpotUpdate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String parkingId;
    private String level;
    private boolean occupied;


    public ParkingSpotUpdate(String id, String parkingId, String level, boolean occupied) {
        this.id = id;
        this.parkingId = parkingId;
        this.level = level;
        this.occupied = occupied;
    }

    public ParkingSpotUpdate() {

    }

}
