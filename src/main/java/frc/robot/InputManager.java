package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class InputManager {

    Joystick firstInput = new Joystick(0);
    Joystick secondInput = new Joystick(1);

    boolean paid = false;
    boolean laid = false;

    boolean outPoke = false;
    boolean inPoke = false;
    boolean visionButton = false;

    Double liftVal;

    Double [] drivingJoysticks() {
        Double [] axisVar = new Double[2];
        axisVar[0] = firstInput.getRawAxis(1);
        axisVar[1] = firstInput.getRawAxis(3);
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

    Double getLift(){

        liftVal = Math.signum(((Math.pow(secondInput.getRawAxis(3), 3))*(2/3)));
        //Math.signum makes it so that any number above 1 will turn into 1 and any number below -1 will turn into -1

        return liftVal;
    }

    Boolean getPaid() { // Linear Actuator pull in

        paid = firstInput.getRawButton(7);
        
        return paid;
    }

    Boolean getLaid() { // Linear Actuator push out

        laid = firstInput.getRawButton(8);
        
        return laid;
    }

    Boolean getinPoke(){

        inPoke = firstInput.getRawButton(1);

        return inPoke;
    }

    Boolean getoutPoke(){

        outPoke = firstInput.getRawButton(2);

        return outPoke;
    }

    public boolean getOrade() { //Vision Button

        visionButton = firstInput.getRawButton(5);

        return visionButton;
    }

}
