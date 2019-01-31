package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class InputManager {

    Joystick firstInput = new Joystick(0);
    Joystick secondInput = new Joystick(1);
    Double[] stickData = new Double[3];

    boolean paid = false;
    boolean laid = false;
    boolean visionButton = false;

    Double[] deadZone(Double[] in) {
        if (-.09 < in[0] && in[0] < .09) {
            in[0] = .0;
        }
        if (-.09 < in[1] && in[1] < .09) {
            in[1] = .0;
        }
        return in;
    }



    Boolean getPaid() { // Linear Actuator pull in
        paid = secondInput.getRawButton(5);
        return paid;
    }

    Boolean getLaid() { // Linear Actuator push out
        laid = secondInput.getRawButton(6);
        return laid;
    }

    public boolean getGatorade() {

        visionButton = firstInput.getRawButton(5);

        return visionButton;
    }

}
