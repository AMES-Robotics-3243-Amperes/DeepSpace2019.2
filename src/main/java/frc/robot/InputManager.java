package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class InputManager {

    Joystick firstInput = new Joystick(0);
    Joystick secondInput = new Joystick(1);

    boolean paid = false;
    boolean laid = false;
    boolean visionButton = false;

    Double [] drivingJoysticks() {
        Double [] axisVar = new Double[2];
        axisVar[0] = firstInput.getRawAxis(3);
        axisVar[1] = firstInput.getRawAxis(1);
        axisVar = deadZone(axisVar);
        return axisVar;
    }

    Double[] deadZone(Double[] in) {
        if (-.09 < in[0] && in[0] < .09) {
            in[0] = .0;
        }
        if (-.09 < in[1] && in[1] < .09) {
            in[1] = .0;
        }
        return in;
    }

    Boolean turbo() {

        boolean turbo = firstInput.getRawButton(6);
        
        return turbo;
    }

    Boolean getPaid() { // Linear Actuator pull in

        paid = firstInput.getRawButton(7);
        
        return paid;
    }

    Boolean getLaid() { // Linear Actuator push out

        laid = firstInput.getRawButton(8);
        
        return laid;
    }

    public boolean getOrade() {

        visionButton = firstInput.getRawButton(5);

        return visionButton;
    }

}
