package scr;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyboardDriver extends SimpleDriver implements NativeKeyListener {

    private static float steer = 0f;
    private static float accel = 0f;
    private static float brake = 0f;

    private final double[] gearUp = { 7000, 7500, 8000, 8500, 9000 };
    private final double[] gearDown = { 2500, 3000, 3500, 4000, 4500 };

    private DataRecorder recorder;

    public KeyboardDriver() {
        recorder = new DataRecorder("training_data_keyboard.csv");

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (Exception e) {
            System.err.println("Errore inizializzazione tastiera globale: " + e.getMessage());
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        switch (e.getKeyCode()) {
            case NativeKeyEvent.VC_W -> accel = 1.0f;
            case NativeKeyEvent.VC_S -> brake = 1.0f;
            case NativeKeyEvent.VC_LEFT -> steer = 0.5f;
            case NativeKeyEvent.VC_RIGHT -> steer = -0.5f;
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        switch (e.getKeyCode()) {
            case NativeKeyEvent.VC_W -> accel = 0.0f;
            case NativeKeyEvent.VC_S -> brake = 0.0f;
            case NativeKeyEvent.VC_LEFT, NativeKeyEvent.VC_RIGHT -> steer = 0.0f;
            case NativeKeyEvent.VC_Z -> {
                steer = 0.0f;
                accel = 0.0f;
                brake = 0.0f;
            }
        }
    }

    @Override public void nativeKeyTyped(NativeKeyEvent e) {}

    @Override
    public Action control(SensorModel sensors) {
        Action action = new Action();

        action.accelerate = accel;
        action.brake = brake;
        action.steering = steer;
        action.gear = getGear(sensors);  // 👈 gestione marce manuale

        // DEBUG
        System.out.println("[DEBUG] ACCEL: " + accel +
                " | BRAKE: " + brake +
                " | STEER: " + steer +
                " | GEAR SENT: " + action.gear +
                " | GEAR READ: " + sensors.getGear() +
                " | SPEED: " + sensors.getSpeed() +
                " | RPM: " + sensors.getRPM());

        recorder.record(
                sensors.getTrackEdgeSensors(), sensors.getAngleToTrackAxis(), sensors.getSpeed(), sensors.getTrackPosition(),
                sensors.getGear(), sensors.getLateralSpeed(), sensors.getCurrentLapTime(), sensors.getDamage(),
                sensors.getDistanceFromStartLine(), sensors.getDistanceRaced(), sensors.getFuelLevel(), sensors.getLastLapTime(),
                sensors.getRPM(), sensors.getWheelSpinVelocity(), sensors.getZSpeed(), sensors.getZ(), steer, accel, brake
        );

        return action;
    }

    private int getGear(SensorModel sensors) {
        int gear = sensors.getGear();
        double rpm = sensors.getRPM();

        if (gear < 1) return 1;
        if (gear < 6 && rpm >= gearUp[gear - 1]) return gear + 1;
        if (gear > 1 && rpm <= gearDown[gear - 1]) return gear - 1;

        return gear;
    }

    @Override
    public void reset() {
        System.out.println("Reset ricevuto da TORCS.");
    }

    @Override
    public void shutdown() {
        recorder.saveToFile();
        System.out.println("CSV salvato. FINE.");
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (Exception e) {
            System.err.println("Errore durante unregister hook: " + e.getMessage());
        }
    }
}
