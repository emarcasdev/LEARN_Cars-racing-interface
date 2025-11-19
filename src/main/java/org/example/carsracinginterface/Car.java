package org.example.carsracinginterface;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import java.util.Random;

public class Car extends Thread {
    // Nombre del piloto con su equipo
    private final String name;
    // Distancia a recorrer para llegar a la meta
    private final int finishDistance;
    // Velocidad máxima del coche
    private final int speedMax;
    // Distancia recorrida por el coche
    private int distanceTraveled;
    // Tiempo en nanosegundos que tardo en cruzar la meta
    private long finishInNano  = Long.MAX_VALUE;
    // Generador para avance y pausas
    private Random random = new Random();

    // Referencias para actualizar la interfaz
    private final AppController controller;
    private final ImageView carImage;

    // Constructor para crear un coche
    public Car(String name, int finishDistance, int speedMax, AppController controller, ImageView carImage) {
        this.name = name;
        this.finishDistance = finishDistance;
        this.speedMax = speedMax;
        this.controller = controller;
        this.carImage = carImage;
    }

    // Lógica de la carrera para los coches
    @Override
    public void run() {
        System.out.println("->" + name + " ha salido.");
        // El coche sigue avanzando hasta que cruce la meta
        while (distanceTraveled < finishDistance) {
            // Avance aleatorio ya que en una carrera no se va a una velocidad constante
            int advance = random.nextInt(speedMax + 1);
            distanceTraveled += advance;

            // Si nos pasamos, forzar el valor de la meta.
            if (distanceTraveled > finishDistance) {
                distanceTraveled = finishDistance;
            }

            // Actualizamos la posición del coche
            Platform.runLater(() ->
                    controller.updateCarPosition(carImage, distanceTraveled, finishDistance)
            );

            // Información de como va el coche en la carrera
            System.out.println("-" + name + " lleva " + distanceTraveled + " de " + finishDistance + " metros.");

            try {
                // Pausa aleatoria, para simular paradas
                Thread.sleep(200 + random.nextInt(301));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        // Cuando cruce la meta guardamos el tiempo que tardo en cruzarla
        finishInNano = System.nanoTime();
        System.out.println("->" + name + " ha llegado a meta.");
        // Registrar la llegada
        controller.finish(name, finishInNano);
    }

    // Devolvemos el nombre del Piloto con su equipo
    public String getPilotName() {
        return name;
    }

    // Devolvemos el tiempo en nanosegundos en el que el coche cruzo la meta
    public long getFinishInNano() {
        return finishInNano;
    }
}