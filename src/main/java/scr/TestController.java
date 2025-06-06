package scr;
import com.studiohartman.jamepad.*;

public class TestController {
    public static void main(String[] args) throws InterruptedException {
        ControllerManager gamepads = new ControllerManager();
        try {
            gamepads.initSDLGamepad();
            while (true) {
                ControllerState state0 = gamepads.getState(0);
                ControllerState state1 = gamepads.getState(0);

                if (state0.isConnected) {
                    System.out.println(gamepads.getNumControllers());
                    System.out.println("StickX0: " + state0.leftStickX + " Trigger0: " + state0.rightTrigger);
                    System.out.println("StickX1: " + state1.leftStickX + " Trigger1: " + state1.rightTrigger);
                } else {
                    System.out.println("Gamepad non connesso");
                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            gamepads.quitSDLGamepad();
        }
    }
}

