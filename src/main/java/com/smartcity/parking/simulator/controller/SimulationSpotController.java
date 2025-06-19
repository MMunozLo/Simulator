package com.smartcity.parking.simulator.controller;

import com.smartcity.parking.simulator.service.SimulationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulate")
public class SimulationSpotController {

    private final SimulationService simulatorService;

    public SimulationSpotController(SimulationService simulatorService) {
        this.simulatorService = simulatorService;
    }

    @PostMapping
    public String triggerSimulation() {
        simulatorService.simulate();
        return "Simulación lanzada manualmente";
    }

    @GetMapping
    public String triggerSimulationGet() {
        simulatorService.simulate();
        return "Simulación lanzada manualmente";
    }
}
