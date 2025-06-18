package com.smartcity.parking.simulator.repository;

import com.smartcity.parking.simulator.entity.SimulatedSpot;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SimulatedSpotRepository extends MongoRepository<SimulatedSpot, String> {

    List<SimulatedSpot> findByParkingId(String parkingId);

    List<SimulatedSpot> findByLevel(String level);
}
