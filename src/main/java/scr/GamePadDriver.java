package scr;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class GamePadDriver extends SimpleDriver{
    private ControllerManager controllers;
    private DataRecorder recorder;
    private boolean initialized;
    private int lastActiveControllerIndex = -1;

    @Override
    public Action control(SensorModel sensors){
        if(!initialized){
            System.out.println("Inizializzazione Controller");
            controllers = new ControllerManager();
            controllers.initSDLGamepad();
            initialized = true;
            recorder = new DataRecorder("data.csv");
        }
        Action action = new Action();

        ControllerState controller = getActiveController();
        if(controller == null)
            return action;

        float rawSteer = -controller.leftStickX;
        float deadzone = 0.2f;
        float accel = controller.rightTrigger;
        float brake = controller.leftTrigger;

        float sens = 0.15f;

        float scaledSteer = 0;
        if(Math.abs(rawSteer) < deadzone)
            scaledSteer = 0.0f;
        else{
            scaledSteer = (Math.abs(rawSteer) - deadzone) / (1 - deadzone);
        }
        float steer = Math.signum(rawSteer) * scaledSteer * sens;

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
        recorder = new DataRecorder("data.csv");

        System.out.println("Restarting the race!");
    }

    private ControllerState getActiveController() {
        for (int i = 0; i < controllers.getNumControllers(); i++) {
            ControllerState state = controllers.getState(i);
            if (state.isConnected && (
                    Math.abs(state.leftStickX) > 0.15 ||
                            state.rightTrigger > 0.05 ||
                            state.leftTrigger > 0.05
            )) {
                lastActiveControllerIndex = i;
                return state;
            }
        }

        // fallback: se nessun input nuovo, ma c'è un controller selezionato
        if (lastActiveControllerIndex != -1) {
            ControllerState fallback = controllers.getState(lastActiveControllerIndex);
            if (fallback.isConnected) return fallback;
        }

        return null;
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
