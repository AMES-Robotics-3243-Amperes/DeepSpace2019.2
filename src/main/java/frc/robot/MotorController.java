package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Servo;

import com.ctre.phoenix.ILoopable;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;

public class MotorController {
    // final means it becomes a constant
    final int DARTZONE = 15; // dart min & max deadzone
    final int SLOWDOWN = 85; // slow down +- 500
    final int EXTEND = 35; // rate of darts extension

    int frontPositionL = 240; // JAMS AT 150-
    int frontPositionR = 300; // JAMS AT 219
    int backPosition = 275; // JAMS AT ???

    long startTime = 0; // for Timer in Encoders

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

    Servo cameraTop = new Servo(2); //change num later

    VictorSPX rotateBelt = new VictorSPX(11);
    VictorSPX collectorBag = new VictorSPX(4);
    VictorSPX gearBox1 = new VictorSPX(1);
    VictorSPX gearBox2 = new VictorSPX(2);

    Encoder leftE = new Encoder(3, 4, false, EncodingType.k4X);
    Encoder rightE = new Encoder(0, 1, false, EncodingType.k4X);

    DigitalOutput underGlow = new DigitalOutput(7); // for the lights under the robot

    public void setMotorControllers(boolean compBot) { // initialization for drive objects and gearbox2 following
        if (compBot == true) {
            driveM1 = new WPI_VictorSPX(9); // left
            driveM2 = new WPI_VictorSPX(8); // right
            driveM3 = new WPI_VictorSPX(10);
            driveM4 = new WPI_VictorSPX(7); // change when new VictorSPX for motor 4 come
        } else {
            driveM1 = new WPI_TalonSRX(3);
            driveM2 = new WPI_TalonSRX(1);
            driveM3 = new WPI_TalonSRX(4);
            driveM4 = new WPI_TalonSRX(2);
        }
        // odds are left side and evens are right side of joystick
        driveM3.follow(driveM1);
        driveM4.follow(driveM2);
        leftE.reset();
        rightE.reset();
        leftE.setDistancePerPulse(1.0 / 76.25);
        rightE.setDistancePerPulse(1.0 / 76.25); // 1.0/76.25 shows that there is 10 cm for every 76.25 pulses in the encoder

    }

    public void setCamera(int moveCam){
        if(moveCam == 180){
            cameraTop.setAngle(15);
        } else if(moveCam == 0){
            cameraTop.setAngle(90);
        } else{
            cameraTop.setAngle(50);
        }
    }

    public void drive(Double[] driveSpeed, boolean Vision, boolean turbo, boolean turboMode) { // for driving
        if (!Vision) {
            // System.out.println(driveM4.getSelectedSensorPosition(0));
            if (turbo) {
                if (turboMode) {
                    driveM1.set(ControlMode.PercentOutput, driveSpeed[0] * -0.75);
                    driveM2.set(ControlMode.PercentOutput, driveSpeed[1] * 0.75);
                } else {
                    driveM1.set(ControlMode.PercentOutput, driveSpeed[0] * -0.2);
                    driveM2.set(ControlMode.PercentOutput, driveSpeed[1] * 0.2);
                }
            } else {
                driveM1.set(ControlMode.PercentOutput, driveSpeed[0] * -0.50);
                driveM2.set(ControlMode.PercentOutput, driveSpeed[1] * 0.50);
            }
        }
    }

    public boolean FIFTYcent(boolean cargoStart, boolean cargoToggle) { // For Encoders
        boolean left = false;
        boolean right = false;

        if (cargoStart) {
            leftE.reset();
            rightE.reset();
            startTime = System.currentTimeMillis();
            //System.out.println("START: " + startTime);
        }

        long timeNow = System.currentTimeMillis();
        timeNow = timeNow - startTime;
        //System.out.println(timeNow);
        if (cargoToggle && timeNow <= 2000) {
            if (leftE.getDistance() < 1.8) {
                if (rightE.getDistance() - leftE.getDistance() >= 0.1) {
                    driveM1.set(ControlMode.PercentOutput, 0.225);
                } else {
                    driveM1.set(ControlMode.PercentOutput, 0.18);
                }
                left = true;
            } else if (leftE.getDistance() > 1.9) {
                driveM1.set(ControlMode.PercentOutput, -0.15);
                left = true;
            } else {
                driveM1.set(ControlMode.PercentOutput, 0.0);
                left = false;
            }

            if (rightE.getDistance() < 1.8) {
                if (leftE.getDistance() - rightE.getDistance() >= 0.1) {
                    driveM2.set(ControlMode.PercentOutput, -0.225);
                } else {
                    driveM2.set(ControlMode.PercentOutput, -0.18);
                }
                right = true;
            } else if (rightE.getDistance() > 1.9) {
                driveM2.set(ControlMode.PercentOutput, 0.15);
                right = true;
            } else {
                driveM2.set(ControlMode.PercentOutput, 0.0);
                right = false;
            }
            //System.out.println(rightE.getDistance() - leftE.getDistance());
            if (!left && !right) {
                leftE.reset();
                rightE.reset();
            }
        } else if (cargoToggle && timeNow >= 2000) {
            driveM1.set(ControlMode.PercentOutput, 0.0);
            driveM2.set(ControlMode.PercentOutput, 0.0);
            left = false;
            right = false;
        }

        return left || right; // returns true when either left OR right is set to true and when both are true.
                              // returns false when both are false.
    }

    public void setLift(Double val) { // for gearbox lifting
        if (val > 0) {
            gearBox1.set(ControlMode.PercentOutput, val * 0.4);
            gearBox2.follow(gearBox1);
        } else if (val < 0) {
            gearBox1.set(ControlMode.PercentOutput, val * 0.5375);
            gearBox2.follow(gearBox1);
        } else {
            gearBox1.set(ControlMode.PercentOutput, 0.0);
            gearBox2.follow(gearBox1);
        }
    }

    public void setRotate(Double val /* , boolean limitSwitchDisableNeutralOnLOS, int timeoutMs */) { // rotates conveyor belt up or down

        if (val > 0) {
            rotateBelt.set(ControlMode.PercentOutput, val * 0.75);
            // rotateBelt.configLimitSwitchDisableNeutralOnLOS(limitSwitchDisableNeutralOnLOS,
            // timeoutMs);
            // The above code was research on how to use limit switches that is built into the TalonSRX
        } else if (val < 0) {
            rotateBelt.set(ControlMode.PercentOutput, val * 0.75);
        } else {
            rotateBelt.set(ControlMode.PercentOutput, 0.0);
        }
    }

    public void setBelt(Boolean belter, Boolean beltee) { // for conveyor belt

        if (belter && !beltee) {
            collectorBag.set(ControlMode.PercentOutput, 0.55);
        } else if (!belter && beltee) {
            collectorBag.set(ControlMode.PercentOutput, -0.55);
        } else {
            collectorBag.set(ControlMode.PercentOutput, 0.0);
        }
    }

    public void backDartPos(boolean Top, boolean Bottom, boolean Extend, boolean Retract) { 
        if (Bottom == false && Extend == true){
            collectDart.set(1);
        } else if (Top == false && Retract == true){
            collectDart.set(-1);
        } else {
            collectDart.set(0);
        }

    }
    public void dartPos(int Position, AnalogInput input, VictorSP dartM, boolean isBackdart) {

        if (input.getValue() < Position + DARTZONE && input.getValue() > Position - DARTZONE) {
            dartM.set(0.0);
        } else if (input.getValue() > Position) {
            if (input.getValue() > Position + SLOWDOWN) {
                if (isBackdart)
                    dartM.set(-0.8);
                else {
                    dartM.set(-1);
                }
            } else {
                dartM.set(-0.25);
            }
        } else if (input.getValue() < Position) {
            if (input.getValue() < Position - SLOWDOWN) {
                if (isBackdart)
                    dartM.set(0.85);
                else {
                    dartM.set(0.80);
                }
            } else {
                dartM.set(0.25);
            }
        } else {
            dartM.set(0.0);
        }
    }

    public void setDart(Boolean paidBack, Boolean laidBack, Boolean paidUpfront, Boolean laidUpfront, Boolean hallTop, Boolean hallBottom) { // for all three darts


        if (paidBack) {
            backPosition = backPosition - EXTEND;
        }
        if (laidBack) {
            backPosition = backPosition + EXTEND;
        }
        if (laidUpfront) {
            frontPositionL = frontPositionL + EXTEND;
            frontPositionR = frontPositionR + EXTEND;
        }
        if (paidUpfront) {
            frontPositionL = frontPositionL - EXTEND;
            frontPositionR = frontPositionR - EXTEND;
        }
        if (frontPositionL < 240) {
            frontPositionL = 240;
        }
        if (frontPositionL > 3600) { // JAMS 3650 +
            frontPositionL = 3600; // JAMS 3770
        }
        if (frontPositionR < 300) {
            frontPositionR = 300;
        }
        if (frontPositionR > 3620) {
            frontPositionR = 3620;
        }
        if (backPosition < 275) {
            backPosition = 275;
        }
        if (backPosition > 3650) {
            backPosition = 3650;
        }

        //dartPos(backPosition, darty, collectDart, true);
        backDartPos(hallTop, hallBottom, laidBack, paidBack);
        dartPos(frontPositionR, darty2, collectDart2, false);
        dartPos(frontPositionL, darty3, collectDart3, false);
        
    }

    // For Vision 2019 ~! :D
    public void setVision(boolean value, double x, double v, double area) {
        float KpSteering = 0.00002f;
        float KpSteering2 = 0.02f;
        float KpDistance = 0.01f;
        float KpDistance2 = 0.2f;
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
            // System.out.println("Steering_Adjust = " + steering_adjust);
            driveM1.set(ControlMode.PercentOutput, steering_adjust + distance_adjust);
            driveM2.set(ControlMode.PercentOutput, steering_adjust - distance_adjust);
            driveM3.follow(driveM1);
            driveM4.follow(driveM2);
            leftE.reset();
            rightE.reset();
        }

    }

    /*public void setGlow(boolean input) { // for the lights under the robot
        underGlow.set(input);
    }*/

}
