package com.smartcity.parking.simulator.controller;

import com.smartcity.parking.simulator.entity.SimulatedSpot;
import com.smartcity.parking.simulator.repository.SimulatedSpotRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/simulator/spots")
public class SimulatedSpotController {

    private final SimulatedSpotRepository repository;

    public SimulatedSpotController(SimulatedSpotRepository repository) {
        this.repository = repository;
    }

    // Obtener todas las plazas simuladas
    @GetMapping
    public List<SimulatedSpot> getAllSpots() {
        return repository.findAll();
    }

    // Obtener una plaza por ID
    @GetMapping("/{id}")
    public SimulatedSpot getSpotById(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot no encontrado con ID: " + id));
    }

    // Cambiar el estado manualmente (útil para testeo)
    @PutMapping("/{id}/toggle")
    public SimulatedSpot toggleOccupancy(@PathVariable String id) {
        SimulatedSpot spot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot no encontrado con ID: " + id));
        spot.setOccupied(!spot.isOccupied());
        return repository.save(spot);
    }
}
