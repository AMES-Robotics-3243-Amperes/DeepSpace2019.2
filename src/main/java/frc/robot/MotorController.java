package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Spark;
import com.ctre.phoenix.ILoopable;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;

public class MotorController {

    BaseMotorController driveM1;
    BaseMotorController driveM2;
    BaseMotorController driveM3;
    BaseMotorController driveM4;

    Float[] alter = new Float[2];
    AnalogInput Darty = new AnalogInput(0);
    Spark collect = new Spark(3);

    public void setMotorControllers(Boolean botType) {
        if (botType) {                                      //for comp bot
            driveM1 = new WPI_VictorSPX(1);     // change later
            driveM2 = new WPI_VictorSPX(2);     // change later
            driveM3 = new WPI_VictorSPX(3);     // change later
            driveM4 = new WPI_VictorSPX(4);     // change later
            
        }
        else {                                              //for prac bot
            driveM1 = new WPI_TalonSRX(2); // right
            driveM2 = new WPI_TalonSRX(3); // left
            driveM3 = new WPI_TalonSRX(1); // right
            driveM4 = new WPI_TalonSRX(4); // left
        }

    }
    public void drive(Double[] driveSpeed, boolean Vision, boolean turbo) {
        if (!Vision) {
            System.out.println(driveM4.getSelectedSensorPosition(0));
            if (turbo) {
                driveM1.set(ControlMode.PercentOutput, driveSpeed[0] * 0.75);
                driveM2.set(ControlMode.PercentOutput, driveSpeed[1] * -0.75);
            }
            else {
                driveM1.set(ControlMode.PercentOutput, driveSpeed[0] * 0.50);
                driveM2.set(ControlMode.PercentOutput, driveSpeed[1] * -0.50);
            }
        }
    }
    public void setDart(Boolean paid, Boolean laid) {

            if (!paid && laid && Darty.getValue() < 3900) {
                collect.set(0.8);
            } else if (!laid && paid && Darty.getValue() > 50) {
                collect.set(-0.8);
            } else {
                collect.set(0);
            }
    }

//herp me prease
    public void setDartMotor() {

        System.out.println("Move 1: " + Darty.getValue());
        System.out.print("");
        System.out.println("Move 2: " + Darty.getValue());

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
