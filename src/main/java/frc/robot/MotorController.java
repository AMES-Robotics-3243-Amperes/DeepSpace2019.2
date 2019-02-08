package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Timer;
import com.ctre.phoenix.ILoopable;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;

public class MotorController {

    BaseMotorController driveM1;
    BaseMotorController driveM2;
    BaseMotorController driveM3;
    BaseMotorController driveM4;

    //Number values to be changed when electronic board is set up
    AnalogInput darty = new AnalogInput(0);
    VictorSPX collectDart = new VictorSPX(3);

    AnalogInput darty2 = new AnalogInput(5);
    VictorSPX collectDart2 = new VictorSPX(76);

    AnalogInput darty3 = new AnalogInput(8);
    VictorSPX collectDart3 = new VictorSPX(745);

    VictorSPX carWash1 = new VictorSPX(6);
    VictorSPX carWash2 = new VictorSPX(5);
    VictorSPX collectorBag = new VictorSPX(12);
    VictorSPX gearBox1 = new VictorSPX(9);
    VictorSPX gearBox2 = new VictorSPX(8);
    

    // 7 & 8 not workable
    // 1,2,3 all on driving
    // left side 1 & 2
    // right side 3 & 10
    // 5 & 6 go to car wash motors
    // 9 gearbox (missing 1 gearbox motor)
    // no linear actuators (3 of them)
    // 1 collector motor (missing)

    // 2 car wash motors: just like cim motors (kinda like cube collector from last
    // year) motor type similar to bag motor
    // 3 linear actuator: use buttons to push out (increase numbers) and push in
    // (decrease numbers) 3 mini cims
    // collector: controls conveyor belt 1 Bag motor(?) controls it
    // gearbox: wraps up rope on a spool. Powered by two mini cims

    public void setMotorControllers() {
        driveM1 = new WPI_VictorSPX(1);
        driveM2 = new WPI_VictorSPX(3);
        driveM3 = new WPI_VictorSPX(2);
        driveM4 = new WPI_VictorSPX(8); // change when new VictorSPX for motor 4 come
        // odds are left side and evens are right side of joystick
        driveM3.follow(driveM1);
        driveM4.follow(driveM2);
    }

    public void drive(Double[] driveSpeed, boolean Vision, boolean turbo) {
        if (!Vision) {
            //System.out.println(driveM4.getSelectedSensorPosition(0));
            if (turbo) {
                driveM1.set(ControlMode.PercentOutput, driveSpeed[0] * -0.75);
                driveM2.set(ControlMode.PercentOutput, driveSpeed[1] * 0.75);
            } else {
                driveM1.set(ControlMode.PercentOutput, driveSpeed[0] * -0.45);
                driveM2.set(ControlMode.PercentOutput, driveSpeed[1] * 0.45);
            }
        }
    }

    public void setLift(Double val){
        gearBox1.set(ControlMode.PercentOutput, val);
        gearBox2.set(ControlMode.PercentOutput, -val);
    }

    public void setCarMotor(Boolean outPoke, Boolean inPoke){
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
    public void setBelt(Boolean belter, Boolean beltee) {

        if (belter && !beltee) {
            collectorBag.set(ControlMode.PercentOutput, 0.55);
        } else if (!belter && beltee) {
            collectorBag.set(ControlMode.PercentOutput, -0.55);
        } else {
            collectorBag.set(ControlMode.PercentOutput, 0.0);
        }
    }
    
    public void setDart(Boolean paid, Boolean laid, Boolean paidUpfront, Boolean laidUpfront) {

        if (!paid && laid && darty.getValue() < 3900) {
            collectDart.set(ControlMode.PercentOutput, 0.80);
        } else if (!laid && paid && darty.getValue() > 50) {
            collectDart.set(ControlMode.PercentOutput, -0.80);
        } else {
            collectDart.set(ControlMode.PercentOutput, 0.0);
        }
        
        if (!paidUpfront && laidUpfront && darty2.getValue() < 3900 && darty3.getValue() < 3900) {
            collectDart2.set(ControlMode.PercentOutput, 0.80);
            collectDart3.set(ControlMode.PercentOutput, 0.80);
        } else if (!laidUpfront && paidUpfront && darty2.getValue() > 50 && darty3.getValue() > 50) {
            collectDart2.set(ControlMode.PercentOutput, -0.80);
            collectDart3.set(ControlMode.PercentOutput, -0.80);
        } else {
            collectDart2.set(ControlMode.PercentOutput, 0.0);
            collectDart3.set(ControlMode.PercentOutput, 0.0);
        }
    }

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
                heading_error = .0;
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
            System.out.println("Steering_Adjust = " + steering_adjust);
            driveM1.set(ControlMode.PercentOutput, steering_adjust - distance_adjust);
            driveM2.set(ControlMode.PercentOutput, steering_adjust + distance_adjust);
            driveM3.follow(driveM1);
            driveM4.follow(driveM2);

        }

    }

}
