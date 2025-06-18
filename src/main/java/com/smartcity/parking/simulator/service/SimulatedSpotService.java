package com.smartcity.parking.simulator.service;

import com.smartcity.parking.simulator.entity.SimulatedSpot;
import com.smartcity.parking.simulator.repository.SimulatedSpotRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class SimulatedSpotService {

    private static final Logger logger = LoggerFactory.getLogger(SimulatedSpotService.class);

    private final SimulatedSpotRepository repository;
    private final Random random = new Random();

    public SimulatedSpotService(SimulatedSpotRepository repository) {
        this.repository = repository;
    }

    // Inicializa 20 plazas simuladas si la colección está vacía
    @PostConstruct
    public void initSimulationData() {
        if (repository.count() == 0) {
            for (int i = 1; i <= 20; i++) {
                SimulatedSpot spot = new SimulatedSpot(
                        "spot" + i,
                        "PARK1",
                        "Level" + (i % 3 + 1),
                        random.nextBoolean()
                );
                repository.save(spot);
            }
            logger.info("Se inicializaron 20 plazas simuladas.");
        }
    }

    // Simula cada 10 segundos el cambio de estado aleatorio de algunas plazas
    @Scheduled(fixedRate = 10000)
    public void simulateOccupancyChanges() {
        List<SimulatedSpot> allSpots = repository.findAll();
        if (allSpots.isEmpty()) {
            logger.warn("No hay plazas simuladas para actualizar.");
            return;
        }

        int changes = random.nextInt(5) + 1; // Cambia entre 1 y 5 plazas
        for (int i = 0; i < changes; i++) {
            SimulatedSpot spot = allSpots.get(random.nextInt(allSpots.size()));
            spot.setOccupied(!spot.isOccupied());
            repository.save(spot);
            logger.info("Plaza {} ahora está {}", spot.getId(), spot.isOccupied() ? "OCUPADA" : "LIBRE");
        }
    }
}
