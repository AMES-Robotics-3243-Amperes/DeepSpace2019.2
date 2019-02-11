package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;

import com.ctre.phoenix.ILoopable;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;

public class MotorController {
    //final means it becomes a constant
    final int DARTMOUTH = 25; //dart min & max deadzone
    final int SLOWDOWN = 350; //slow down +- 500

    BaseMotorController driveM1;
    BaseMotorController driveM2;
    BaseMotorController driveM3;
    BaseMotorController driveM4;

    AnalogInput darty = new AnalogInput(0);
    VictorSP collectDart = new VictorSP(3);

    AnalogInput darty2 = new AnalogInput(1);
    VictorSP collectDart2 = new VictorSP(6); // right

    AnalogInput darty3 = new AnalogInput(2);
    VictorSP collectDart3 = new VictorSP(5); // left

    VictorSPX carWash1 = new VictorSPX(11);
    VictorSPX carWash2 = new VictorSPX(12);
    VictorSPX collectorBag = new VictorSPX(4);
    VictorSPX gearBox1 = new VictorSPX(1);
    VictorSPX gearBox2 = new VictorSPX(2);
    Encoder leftE = new Encoder(3, 4, false, EncodingType.k4X);
    Encoder rightE = new Encoder(0, 1, false, EncodingType.k4X);
    
    //Encoders: 
        //1, 2, 3, & 4 are the ports in the roboRIO. They are digital input values
        //True/false tells whether the encoder will reverse the counting direction or not. True inverts and false does not.
        //EncodingType.k4X shows the type of encoder we are using. It means that the encoder has 4X accuracy.

    // 2 car wash motors: just like cim motors (kinda like cube collector from last
    // year) motor type similar to bag motor
    // 3 linear actuator: use buttons to push out (increase numbers) and push in
    // (decrease numbers) 3 mini cims
    // collector: controls conveyor belt 1 Bag motor(?) controls it
    // gearbox: wraps up rope on a spool. Powered by two mini cims

    public void setMotorControllers() { //initialization for drive objects and gearbox2 following
        driveM1 = new WPI_VictorSPX(9); //left
        driveM2 = new WPI_VictorSPX(8); //right
        driveM3 = new WPI_VictorSPX(10);
        driveM4 = new WPI_VictorSPX(7); // change when new VictorSPX for motor 4 come
        // odds are left side and evens are right side of joystick
        driveM3.follow(driveM1);
        driveM4.follow(driveM2);
        leftE.reset();
        rightE.reset();
        leftE.setDistancePerPulse(1.0/76.25);
        rightE.setDistancePerPulse(1.0/76.25);  //1.0/76.25 shows that there is 10 cm for every 76.25 pulses in the encoder
    }

    public void drive(Double[] driveSpeed, boolean Vision, boolean turbo, boolean turboMode) { //for driving
        if (!Vision) {
            //System.out.println(driveM4.getSelectedSensorPosition(0));
            if (turbo) {
                if (turboMode) {
                    driveM1.set(ControlMode.PercentOutput, driveSpeed[0] * -0.75);
                    driveM2.set(ControlMode.PercentOutput, driveSpeed[1] * 0.75);
                } else {
                    driveM1.set(ControlMode.PercentOutput, driveSpeed[0] * -0.278);
                    driveM2.set(ControlMode.PercentOutput, driveSpeed[1] * 0.278);
                }
            } else {
                driveM1.set(ControlMode.PercentOutput, driveSpeed[0] * -0.50);
                driveM2.set(ControlMode.PercentOutput, driveSpeed[1] * 0.50);
            }
        }
    }

    public boolean FIFTYcent(boolean cargoStart, boolean cargoToggle){
        if(cargoStart){
            leftE.reset();
            rightE.reset();
        }
        if(cargoToggle){
            if(leftE.getDistance() < 5){
                driveM1.set(ControlMode.PercentOutput, 0.2987);
                return true;
            } else if(leftE.getDistance() > 5.05){
                driveM1.set(ControlMode.PercentOutput, -0.15);
                return true;
            } else{
                driveM1.set(ControlMode.PercentOutput, 0.0);
                leftE.reset();
                return false;
            }
        }
        return false;
    }

    public void setLift(Double val){        //for gearbox lifting
        gearBox1.set(ControlMode.PercentOutput, val * 0.4);
        gearBox2.follow(gearBox1);
    }

    public void setCarMotor(Boolean outPoke, Boolean inPoke){   //for ball intake
        if(outPoke == true && !inPoke){
            carWash1.set(ControlMode.PercentOutput, 0.75);
            carWash2.set(ControlMode.PercentOutput, -0.75);
        } else if(!outPoke && inPoke == true){
            carWash1.set(ControlMode.PercentOutput, -0.75);
            carWash2.set(ControlMode.PercentOutput, 0.75);
        } else{
            carWash1.set(ControlMode.PercentOutput, 0.0);
            carWash2.set(ControlMode.PercentOutput, 0.0);
        }
    }   
    public void setBelt(Boolean belter, Boolean beltee) {   //for conveyor belt

        if (belter && !beltee) {
            collectorBag.set(ControlMode.PercentOutput, 0.55);
        } else if (!belter && beltee) {
            collectorBag.set(ControlMode.PercentOutput, -0.55);
        } else {
            collectorBag.set(ControlMode.PercentOutput, 0.0);
        }
    }

    public void dartPosFront(int Position, AnalogInput input, VictorSP dartM){
        
        if(input.getValue() < Position + DARTMOUTH && input.getValue() > Position - DARTMOUTH){
            dartM.set(0.0);
        } else if(input.getValue() > Position){
            if(input.getValue() > Position + SLOWDOWN){
                dartM.set(-0.5); }
            else dartM.set(-0.25);
        } else if(input.getValue() < Position){
            if(input.getValue() < Position - SLOWDOWN){
                dartM.set(0.75); }
            else dartM.set(0.25);
        } else{
            dartM.set(0.0);
        }
        double dartyval = collectDart3.getSpeed();
        //System.out.println("Speed: " + String.format("%.4f",dartyval) + "   Position: " + input.getValue() + "    Extreme:" + Position + "   ");
    }

    public void dartPosBack(int Position, AnalogInput input, VictorSP dartM){
        
        if(input.getValue() < Position + DARTMOUTH && input.getValue() > Position - DARTMOUTH){
            dartM.set(0.0);
        } else if(input.getValue() > Position){
            if(input.getValue() > Position + SLOWDOWN){
                dartM.set(-0.5); }
            else dartM.set(-0.25);
        } else if(input.getValue() < Position){
            if(input.getValue() < Position - SLOWDOWN){
                dartM.set(0.75); }
            else dartM.set(0.25);
        } else{
            dartM.set(0.0);
        }
        double dartyval = collectDart3.getSpeed();
        //System.out.println("Speed: " + String.format("%.4f",dartyval) + "   Position: " + input.getValue() + "    Extreme:" + Position + "   ");
    }







////////////this is front & back dartPos split up
    public void extendBackDartM(int Position, AnalogInput input, VictorSP dartM){
        if(input.getValue() < Position + DARTMOUTH && input.getValue() > Position - DARTMOUTH){
            dartM.set(0.0);
        } else if(input.getValue() < Position){
            if(input.getValue() < Position - SLOWDOWN){
                dartM.set(0.75); }
            else dartM.set(0.25);
        } else{
            dartM.set(0.0);
        }
    }
    public void extendFrontDartM(int Position, AnalogInput input, VictorSP dartM){
        if(input.getValue() < Position + DARTMOUTH && input.getValue() > Position - DARTMOUTH){
            dartM.set(0.0);
        } else if(input.getValue() < Position){
            if(input.getValue() < Position - SLOWDOWN){
                dartM.set(0.75); }
            else dartM.set(0.25);
        } else{
            dartM.set(0.0);
        }
    }
    public void retractBackDartM(int Position, AnalogInput input, VictorSP dartM){
        
        if(input.getValue() < Position + DARTMOUTH && input.getValue() > Position - DARTMOUTH){
            dartM.set(0.0);
        } else if(input.getValue() > Position){
            if(input.getValue() > Position + SLOWDOWN){
                dartM.set(-0.5); }
            else dartM.set(-0.25);
        } else{
            dartM.set(0.0);
        }
    }
    public void retractFrontDartM(int Position, AnalogInput input, VictorSP dartM){
        
        if(input.getValue() < Position + DARTMOUTH && input.getValue() > Position - DARTMOUTH){
            dartM.set(0.0);
        } else if(input.getValue() > Position){
            if(input.getValue() > Position + SLOWDOWN){
                dartM.set(-0.5); }
            else dartM.set(-0.25);
        } else{
            dartM.set(0.0);
        }
    }
///////////to here





    /*public void setDart(Boolean paidBack, Boolean laidBack, Boolean paidUpfront, Boolean laidUpfront) { //for all three darts

        int backPosition = 270;
        if(laidBack){
            backPosition = 3650;
        } else if(paidBack){
            backPosition = 1250;
        }

        // 100 UNITS = 1/4 INCH

        int frontPositionL = 275; // JAMS AT 150-
        int frontPositionR = 104; // JAMS AT 4
        if(laidUpfront){
            frontPositionL = 3600; // JAMS AT ABOVE 3650
            frontPositionR = 3425; // JAMS AT 3470
        }
        /*if (paid && !laid) {
            extendBackDartM(backPosition, darty, collectDart); // stick a comment in there saying this is for the back dart or something
        } else if (!laid && paid) {
            retractBackDartM(backPosition, darty, collectDart); // stick a comment in there saying this is for the back dart or something
        } else 
        if (paidUpfront && !laidUpfront) {
        extendFrontDartM(frontPositionR, darty2, collectDart2); // front right dart
        extendFrontDartM(frontPositionL, darty3, collectDart3); // front left dart
        } else if (!laidUpfront && paidUpfront) {
        retractFrontDartM(frontPositionR, darty2, collectDart2); // front right dart
        retractFrontDartM(frontPositionL, darty3, collectDart3); // front left dart
        } else {
            collectDart.set(0.0);
        }
        
        dartPosBack(backPosition, darty, collectDart);
        dartPosFront(frontPositionR, darty2, collectDart2);
        dartPosFront(frontPositionL, darty3, collectDart3);
        //System.out.println(darty.getValue() + "  " + darty2.getValue() + "  " + darty3.getValue());
}*/



// TESTING IT OUT
    public void setDart(Boolean paid, Boolean laid, Boolean paidUpfront, Boolean laidUpfront) {

        if (!paid && laid && darty.getValue() < 270 + DARTMOUTH) {
            if(darty.getValue() > 270 + SLOWDOWN){
                collectDart.set(ControlMode.PercentOutput, 0.75);
            else{ collectDart.set(ControlMode.PercentOutput, 0.25);
            }
        } else if (!laid && paid && darty.getValue() > 3650 - DARTMOUTH) {
            if(darty.getValue() < 3650 - SLOWDOWN){
                collectDart.set(ControlMode.PercentOutput, -0.5);
            else collectDart.set(ControlMode.PercentOutput, -0.25);
        } else {
        collectDart.set(ControlMode.PercentOutput, 0.0);
        }
        if (!paidUpfront && laidUpfront && darty2.getValue() < 104 + DARTMOUTH && darty3.getValue() < 275 + DARTMOUTH) {
            if(darty2.getValue() > 104 + SLOWDOWN && darty3.getValue() > 275 + SLOWDOWN){
                collectDart2.set(ControlMode.PercentOutput, 0.75);
                collectDart3.set(ControlMode.PercentOutput, 0.75);
            else{ 
                collectDart2.set(ControlMode.PercentOutput, 0.25);
                collectDart3.set(ControlMode.PercentOutput, 0.25);
            }
        } else if (!laid && paid && darty2.getValue() > 3425 - DARTMOUTH && darty3.getValue() > 3600 - DARTMOUTH) {
            if(darty2.getValue() < 3425 - SLOWDOWN && darty3.getValue() < 3600 - SLOWDOWN){
                collectDart2.set(ControlMode.PercentOutput, -0.5);
                collectDart3.set(ControlMode.PercentOutput, -0.5);
            else{
                collectDart2.set(ControlMode.PercentOutput, -0.25);
                collectDart3.set(ControlMode.PercentOutput, -0.25);
        } else {
        collectDart.set(ControlMode.PercentOutput, 0.0);
        collectDart2.set(ControlMode.PercentOutput, 0.0);
        collectDart3.set(ControlMode.PercentOutput, 0.0);
        }
///////////////////////





    // For Vision 2019 ~! :D
    public void setVision(boolean value, double x, double v, double area) {
        float KpSteering = 0.00002f;
        float KpSteering2 = 0.025f;
        float KpDistance = 0.01f;
        float KpDistance2 = 0.16f;
        float min_command = 0.05f;
        float refArea = 2.25f;

        if (value == true) {
            double heading_error = (float) x;
            double steering_adjust = 0.0d;
            double distance_error = (float) area;
            double distance_adjust = 0.0d;
            double maxDistAdjust = 0.8d;
            double maxAngAdjust = 1.0d;

            if (-.25 < heading_error && heading_error < .25) {
                heading_error = 0.0;
            }

            if (v == 0.0)
            // We don't see the target, seek for the target by spinning in place at a safe
            // speed.
            {
                steering_adjust = 0.25f;
            }

            else if (x > 0 && v != 0)
            // We do see the target, execute aiming code
            {
                steering_adjust = (KpSteering * Math.pow(heading_error, 3) + KpSteering2 * heading_error) + min_command;
            } else if (x < 0 && v != 0) {
                steering_adjust = (KpSteering * Math.pow(heading_error, 3) + KpSteering2 * heading_error) - min_command;
            }

            if (v == 0.0)
            // We don't see the target, seek for the target by spinning in place at a safe
            // speed.
            {
                distance_adjust = 0.0f;
            }
            if (area != 0) {

                distance_error = refArea - distance_error;
                distance_adjust = KpDistance * Math.pow(distance_error, 3) + KpDistance2 * distance_error;
            }

            distance_adjust = Math.tanh(distance_adjust) * maxDistAdjust;
            steering_adjust = Math.tanh(steering_adjust) * maxAngAdjust;
            //System.out.println("Steering_Adjust = " + steering_adjust);
            driveM1.set(ControlMode.PercentOutput, steering_adjust - distance_adjust);
            driveM2.set(ControlMode.PercentOutput, steering_adjust + distance_adjust);
            driveM3.follow(driveM1);
            driveM4.follow(driveM2);
            leftE.reset();
            rightE.reset();
        }

    }

}
