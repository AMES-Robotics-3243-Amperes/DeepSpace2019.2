package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

    //////////////////////////////////////////////
    // 
    // PRIMARY DRIVER CONTROLS
    //
    // Left & Right Joysticks (Tank Drive)
    //      Controls the robot's left and right side wheels accordingly.
    //
    // X Y B A (Climbing Mechanism)
    //      X - Extends Back DART on Robot
    //      Y - Retracts Back DART on Robot
    //      B - Retracts Front 2 DARTs on Robot
    //      A - Extends Front 2 DARTs on Robot
    //
    // RB (Right Bumper) aka TURBO
    //      Sets Robot speed to 75% for duration the button is pressed.
    //
    // LB (Left Bumper) aka VISION
    //      Uses limelight camera to detect any reflective tape and center on it.
    //
    ///////////////////////////////////////////////
    //
    // SECONDARY DRIVER CONTROLS
    //
    // Joysticks
    //
    //      Left Stick -
    //
    //      Right Stick - Move vertically
    //          Use to move neck up and down to adjust for rocket heights.
    //
    // X Y B A (Car Wash & ~ )
    //
    //      X - Collect Ball
    //      Y - Spit Ball
    //      B - 
    //      A - 
    //
    // Left & Right Triggers (Conveyer Belt)
    //
    //      LT - Conveyer Counter-Clockwise
    //
    //      RT - Conveyer Clockwise
    //
    /////////////////////////////

public class InputManager {

    Joystick firstInput = new Joystick(0);
    Joystick secondInput = new Joystick(1);

    boolean paid = false;
    boolean laid = false;
    //boolean paidUpfront
    boolean paidUpfront = false;
    boolean laidUpfront = false;

    boolean belter = false;
    boolean beltee = false;

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

    Boolean getPaid() { // Back Linear Actuator pull in

        paid = firstInput.getRawButton(4);
        
        return paid;
    }

    Boolean getLaid() { // Back Linear Actuator push out

        laid = firstInput.getRawButton(1);
        
        return laid;
    }

    public boolean getOrade() { //Vision Button

        visionButton = firstInput.getRawButton(5);

        return visionButton;
    }

    public boolean getPaidUpFront() {

        paidUpfront = firstInput.getRawButton(2);

        return paidUpfront;
    }

    public boolean getLaidUpFront() {

        laidUpfront = firstInput.getRawButton(3);

        return laidUpfront;
    }

    Boolean getinPoke(){

        inPoke = secondInput.getRawButton(1);

        return inPoke;
    }

    Boolean getoutPoke(){

        outPoke = secondInput.getRawButton(2);

        return outPoke;
    }

    Boolean getBelter() {

        belter = secondInput.getRawButton(7);

        return belter;
    }

    Boolean getBeltee() {

        beltee = secondInput.getRawButton(8);

        return beltee;
    }
    
    Double getLift(){

        liftVal = Math.signum(((Math.pow(secondInput.getRawAxis(3), 3))*(2/3)));
        //Math.signum makes it so that any number above 1 will turn into 1 and any number below -1 will turn into -1
        //secondInput right joystick up down    
        return liftVal;
    }

    
}
