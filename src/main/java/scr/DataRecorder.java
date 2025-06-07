package scr;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataRecorder {
    private final List<String> dataRows = new ArrayList<>();
    private final String filename;

    public DataRecorder(String filename){
        this.filename = filename;
        StringBuilder header = new StringBuilder();
        for(int i = 0; i < 19; i++){
            header.append("trackSensor_").append(i).append(",");
        }

        header.append("angleToTrackAxis,speed,trackPosition,gear," +
                "lateralSpeed,currentLapTime,damage,distanceFromStartLine," +
                "distanceRaced,fuelLevel,lastLapTime,RPM,");
        for(int i = 0; i < 4; i++){
            header.append("wheelSpinVelocity_").append(i).append(",");
        }
        header.append("ZSpeed,Z");
        dataRows.add(header.toString());
    }

    public void record(double[] trackEdgeSensors, double angleToTrackAxis, double speed,
                       double trackPosition, int gear, double lateralSpeed, double currentLapTime,
                       double damage, double distanceFromStartLine, double distanceRaced,
                       double fuelLevel, double lastLapTime, double RPM, double[] wheelSpinVelocity,
                       double ZSpeed, double Z, float steer, float accel, float brake){

        StringBuilder row = new StringBuilder();

        for (int i = 0; i < 19; i++) {
            row.append(String.format(Locale.US, "%.3f", trackEdgeSensors[i])).append(",");
        }

        row.append(String.format(Locale.US, "%.4f", angleToTrackAxis)).append(",");
        row.append(String.format(Locale.US, "%.3f", speed)).append(",");
        row.append(String.format(Locale.US, "%.4f", trackPosition)).append(",");
        row.append(gear).append(",");
        row.append(String.format(Locale.US, "%.4f", lateralSpeed)).append(",");
        row.append(String.format(Locale.US, "%.3f", currentLapTime)).append(",");
        row.append(String.format(Locale.US, "%.2f", damage)).append(",");
        row.append(String.format(Locale.US, "%.3f", distanceFromStartLine)).append(",");
        row.append(String.format(Locale.US, "%.3f", distanceRaced)).append(",");
        row.append(String.format(Locale.US, "%.3f", fuelLevel)).append(",");
        row.append(String.format(Locale.US, "%.3f", lastLapTime)).append(",");
        row.append(String.format(Locale.US, "%.3f", RPM)).append(",");
        for (int i = 0; i < 4; i++) {
            row.append(String.format(Locale.US, "%.3f", wheelSpinVelocity[i])).append(",");
        }
        row.append(String.format(Locale.US, "%.3f", ZSpeed)).append(",");
        row.append(String.format(Locale.US, "%.3f", Z)).append(",");
        row.append(String.format(Locale.US, "%.3f", steer)).append(",");
        row.append(String.format(Locale.US, "%.3f", accel)).append(",");
        row.append(String.format(Locale.US, "%.3f", brake));

        dataRows.add(row.toString());
    }

    public void saveToFile() {
        try(FileWriter writer = new FileWriter(filename)){
            for(String row : dataRows){
                writer.write(row +"\n");
            }
            System.out.println("Dati salvati in " + filename);
        }
        catch (IOException e){
            System.err.println("Errore durante il salvataggio:" + e.getMessage());
        }
    }
}
