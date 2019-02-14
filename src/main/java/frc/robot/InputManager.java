package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

    //////////////////////////////////////////////
    // When Mode is Green, Left Joystick switches to D-PAD
    // PRIMARY DRIVER CONTROLS
    //
    // Left & Right Joysticks (Tank Drive)
    //      Controls the robot's left and right side wheels accordingly.
    //
    // X A B Y
    //      X - Vision Button - Uses limelight camera to detect any reflective tape and center on it.
    //      A - 
    //      B - 
    //      Y - 
    //
    // RB (Right Bumper) aka TURBO
    //      Sets Robot speed to 75% for duration the button is pressed.
    //
    // LB (Left Bumper) aka VISION
    //      
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
    // X A B Y
    //
    //      X - 
    //      A - 
    //      B - 
    //      Y - 
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
    boolean paidUpfront = false;
    boolean laidUpfront = false;

    boolean belter = false;
    boolean beltee = false;
    boolean visionButton = false;

    boolean turboToggle = true;
    boolean cargoDepositToggle = false;

    Double liftVal;
    Double rotateLift;

    boolean lightStatus = false;

    Boolean getToggleTurbo() {
        if (firstInput.getRawButtonPressed(9)){
            turboToggle = !turboToggle;
        }
        return turboToggle;
    }

    Boolean getCargoStart() {
        if (secondInput.getRawButtonPressed(2)){
            cargoDepositToggle = true;
            return true;
        } else{
            return false;
        }
    }

    Double [] drivingJoysticks() {
        Double [] axisVar = new Double[2];
        axisVar[0] = firstInput.getRawAxis(1);
        axisVar[1] = firstInput.getRawAxis(3);
        axisVar = scaleFactor(axisVar);
        axisVar = deadZone(axisVar);    
        return axisVar;
    }

    Double[] deadZone(Double[] in) {
        if (-.09 < in[0] && in[0] < .09) {
            in[0] = 0.0;
        }
        if (-.09 < in[1] && in[1] < .09) {
            in[1] = 0.0;
        }
        return in;
    }

    Double[] scaleFactor(Double[] scale) {
        scale[0] = 0.8 * Math.pow(scale[0], 3) + 0.2 * scale[0];
        scale[1] = 0.8 * Math.pow(scale[1], 3) + 0.2 * scale[1];
        return scale;
    }
    Boolean turbo() {

        boolean turbo = firstInput.getRawButton(8);
         
        return turbo;
    }

    Boolean getPaid() { // Back Linear Actuator

        paid = firstInput.getRawButton(5);
        
        return paid;
    }

    Boolean getLaid() { // Back Linear Actuator extends

        laid = firstInput.getRawButton(6);
        
        return laid;
    }

    public boolean getOrade() { //Vision Button

        visionButton = firstInput.getRawButton(1);

        return visionButton;
    }

    public boolean getPaidUpFront() { //front two darts retracts

        paidUpfront = secondInput.getRawButton(1);

        return paidUpfront;
    }

    public boolean getLaidUpFront() { //front two darts extends

        laidUpfront = secondInput.getRawButton(4);

        return laidUpfront;
    }

    ///////////////////////////////////////////////////SECOND INPUT
    Boolean getBelter() {

        belter = secondInput.getRawButton(7);

        return belter;
    }

    Boolean getBeltee() {

        beltee = secondInput.getRawButton(8);

        return beltee;
    }
    
    Double getLift(){
        if(secondInput.getRawButton(12)){
            liftVal = -0.22;
        } else{
            liftVal = secondInput.getRawAxis(3);
        }

        if (-.09 < liftVal && liftVal < .09) {
            liftVal = 0.0;
        }

        return liftVal;
    }

    Double getRotateConveyor(){

        rotateLift = secondInput.getRawAxis(1);
        if (-.09 < rotateLift && rotateLift < .09) {
            rotateLift = 0.0;
        }

        return rotateLift;
    }

    Boolean getGlow() {  //for the lights under the robot
		if(secondInput.getRawButtonPressed(10)) {
			lightStatus = !lightStatus;
		}
		return lightStatus;
	}

}
