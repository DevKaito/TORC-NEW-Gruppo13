package scr;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class GamePadDriver extends SimpleDriver{
    private ControllerManager controllers;
    private DataRecorder recorder;
    private boolean initialized;


    @Override
    public Action control(SensorModel sensors){
        if(!initialized){
            System.out.println("Inizializzazione Controller");
            controllers = new ControllerManager();
            controllers.initSDLGamepad();
            recorder = new DataRecorder("data.csv");
            initialized = true;
        }
        Action action = new Action();

        ControllerState controller = controllers.getState(0);
        float rawSteer = -controller.leftStickX;
        float deadzone = 0.15f;
        float accel = controller.rightTrigger;
        float brake = controller.leftTrigger;

        float steer = Math.abs(rawSteer) < deadzone ? 0.0f : rawSteer;

        //Salva in RAM tutte le informazioni riguardo ai sensori
        recorder.record(sensors.getTrackEdgeSensors(), sensors.getAngleToTrackAxis(), sensors.getSpeed(), sensors.getTrackPosition(),
                sensors.getGear(), sensors.getLateralSpeed(), sensors.getCurrentLapTime(), sensors.getDamage(),
                sensors.getDistanceFromStartLine(), sensors.getDistanceRaced(), sensors.getFuelLevel(), sensors.getLastLapTime(),
                sensors.getRPM(), sensors.getWheelSpinVelocity(), sensors.getZSpeed(), sensors.getZ(), steer, accel, brake
        );


        action.steering = steer;
        action.accelerate = accel;
        action.brake = brake;
        action.gear = getGear(sensors);
        return action;
    }

    @Override
    public void shutdown(){
        System.out.println("Scrittura su file CSV...");
        recorder.saveToFile();

        if(controllers != null)
            controllers.quitSDLGamepad();
    }

    @Override
    public void reset() {

        recorder = new DataRecorder("training_data.csv");

        System.out.println("Restarting the race!");
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
}
