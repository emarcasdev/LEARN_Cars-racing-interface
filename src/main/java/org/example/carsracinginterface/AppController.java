package org.example.carsracinginterface;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppController {
    // Distancia total de la carrera
    private static int finishDistance = 1500;

    // Referencias para la carrera
    @FXML private AnchorPane raceTrack;
    @FXML private ImageView ferrariCar;
    @FXML private ImageView redBullCar;
    @FXML private ImageView astonMartinCar;
    @FXML private ImageView williamsCar;
    @FXML private TextArea classification;
    @FXML private Button startButton;

    // Listas de los pilotos con sus tiempos:
    private List<String> pilots = new ArrayList<>();
    private List<Long> times = new ArrayList<>();

    // Evitar que haya más de 1 carrera ejecutandose
    private boolean raceRunning = false;

    // Cuando la aplicación se inicia
    @FXML
    public void initialize() {
        // Poner los coches en linea de salida
        carsOnPosition();

        // Asegurarnos que el botón está activo
        if (startButton != null) {
            startButton.setDisable(false);
        }
    }

    // Pone los coches en la linea de salida para que ninguno tenga ventaja
    private void carsOnPosition() {
        double startLine = 0;

        ferrariCar.setLayoutX(startLine);
        redBullCar.setLayoutX(startLine);
        astonMartinCar.setLayoutX(startLine);
        williamsCar.setLayoutX(startLine);
    }

    // Iniciar la carrera cundo pulsemos el botón
    @FXML
    private void startRace(ActionEvent event) {
        if (raceRunning) {
            return;
        }
        raceRunning = true;

        // Desactivamos el botón mientras la carrera siga en marcha
        if (startButton != null) {
            startButton.setDisable(true);
        }

        // Limpiar el resultado y la clasificación
        pilots.clear();
        times.clear();
        classification.clear();

        // Colocamos los coches en la linea de salida
        carsOnPosition();

        // Creamos nuestros competidores y iniciamos sus hilos
        startCar("Lewis Hamilton (Ferrari)", finishDistance, 60, ferrariCar);
        startCar("Max Verstappen (Red Bull)", finishDistance, 65, redBullCar);
        startCar("El Nano (Aston Martin)", finishDistance, 55, astonMartinCar);
        startCar("Carlos Sainz Jr. (Williams)", finishDistance, 50, williamsCar);
    }

    // Crear los coches para la carrera
    private void startCar(String name, int finishDistance, int speedMax, ImageView carImage) {
        Car car = new Car(name, finishDistance, speedMax, this, carImage);
        Thread thread = new Thread(car);
        thread.start();
    }

    // Mostramos el avance del coche de forma horizontal
    public void updateCarPosition(ImageView carImage, int distanceTraveled, int finishDistance) {
        // Obtener el ancho tatal de la pista de carreras
        double trackWidth = raceTrack.getWidth();

        // % de la carrera completado
        double progress = (double) distanceTraveled / finishDistance;
        // Distancia máxima que puede recorrer el coche en la pista de carreras.
        double maxX = trackWidth - carImage.getFitWidth() - 10;
        // Nueva posicion del coche en el eje X
        double newPosition = 10 + progress * maxX;

        // Actualizarmos la interfaz
        carImage.setLayoutX(newPosition);
    }

    // Resgistra cuando el coche llega y actualizamos la clasificación
    public synchronized void finish(String pilotName, long finishNano) {
        pilots.add(pilotName);
        times.add(finishNano);

        // Ordenomos por el tiempo de llegada
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            index.add(i);
        }
        index.sort(Comparator.comparingLong(times::get));

        // Creamos la clasificación
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < index.size(); i++) {
            int position = index.get(i);
            String pilot = pilots.get(position);
            sb.append("#").append(position + 1).append(". ").append(pilot).append("\n");
        }

        // Mostrar cuantos faltan por llegar
        if (pilots.size() < 4) {
            int restantes = 4 - pilots.size();
            sb.append("\nFaltan por llegar ").append(restantes).append(" coche(s)...");
        } else {
            raceRunning = false;
        }

        // Actualizamos la interfaz
        Platform.runLater(() -> {
            classification.setText(sb.toString());
            // Si la carrera termino, volver a activar el botón
            if (!raceRunning && startButton != null) {
                startButton.setDisable(false);
            }
        });
    }
}
