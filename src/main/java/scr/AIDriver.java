package scr;

import java.io.*;
import java.net.Socket;

public class AIDriver extends SimpleDriver {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public AIDriver() {
        try {
            socket = new Socket("localhost", 3001);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Action control(SensorModel sensors) {
        Action action = new Action();

        try {
            StringBuilder input = new StringBuilder();

            for (int i = 0; i < 19; i++) {
                input.append(sensors.getTrackEdgeSensors()[i]).append(",");
            }

            input.append(sensors.getAngleToTrackAxis()).append(",");
            input.append(sensors.getSpeed()).append(",");
            input.append(sensors.getTrackPosition()).append(",");
            input.append(sensors.getGear()).append(",");
            input.append(sensors.getLateralSpeed()).append(",");
            input.append(sensors.getRPM()).append(",");

            for (int i = 0; i < 4; i++) {
                input.append(sensors.getWheelSpinVelocity()[i]).append(",");
            }

            input.append(sensors.getZSpeed()).append(",");
            input.append(sensors.getZ());

            out.write(input.toString());
            out.newLine();
            out.flush();

            String[] result = in.readLine().split(",");

            action.steering = Double.parseDouble(result[0]);
            action.accelerate = Double.parseDouble(result[1]);
            action.brake = Double.parseDouble(result[2]);
            action.gear = getGear(sensors);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return action;
    }

    private int getGear(SensorModel sensors) {
        int gear = sensors.getGear();
        double rpm = sensors.getRPM();

        // Se la marcia è 0 (N) o -1 (R) restituisce semplicemente 1
        if (gear < 1)
            return 1;

        // Se il valore di RPM dell'auto è maggiore di quello suggerito
        // sale di marcia rispetto a quella attuale
        if (gear < 6 && rpm >= gearUp[gear - 1])
            return gear + 1;
        else

            // Se il valore di RPM dell'auto è inferiore a quello suggerito
            // scala la marcia rispetto a quella attuale
            if (gear > 1 && rpm <= gearDown[gear - 1])
                return gear - 1;
            else // Altrimenti mantenere l'attuale
                return gear;
    }

    @Override
    public void shutdown() {
        try {
            out.write("exit\n");
            out.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

