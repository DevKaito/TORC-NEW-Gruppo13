package scr;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class GamePadDriver extends SimpleDriver{
    private ControllerManager controllers;
    private ControllerState controller;
    private DataRecorder recorder;

    @Override
    public Action control(SensorModel sensors){
        Action action = new Action();

        controller = controllers.getState(0);
        float steer = controller.leftStickX;
        float accel = controller.rightTrigger;
        float brake = controller.leftTrigger;

        //Salva in RAM tutte le informazioni riguardo ai sensori
        recorder.record(sensors.getTrackEdgeSensors(), sensors.getAngleToTrackAxis(), sensors.getSpeed(), sensors.getTrackPosition(),
                sensors.getGear(), sensors.getLateralSpeed(), sensors.getCurrentLapTime(), sensors.getDamage(),
                sensors.getDistanceFromStartLine(), sensors.getDistanceRaced(), sensors.getFuelLevel(), sensors.getLastLapTime(),
                sensors.getRPM(), sensors.getWheelSpinVelocity(), sensors.getZSpeed(), sensors.getZ(), steer, accel, brake
        );


        action.steering = steer;
        action.accelerate = accel;
        action.brake = brake;
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
        controllers= new ControllerManager();

        recorder = new DataRecorder("training_data.csv");

        System.out.println("Restarting the race!");
    }
}
